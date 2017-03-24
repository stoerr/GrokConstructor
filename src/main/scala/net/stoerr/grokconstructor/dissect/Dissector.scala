package net.stoerr.grokconstructor.dissect

import scala.util.matching.Regex
import scala.util.matching.Regex.quote

/**
  * Models the logstash dissect plugin.
  *
  * @see "https://www.elastic.co/guide/en/logstash/current/plugins-filters-dissect.html"
  */
object Dissector {

  /** Matches prefix, field and rest in a dissection. */
  private val fieldPat =
    """([^%{}]*+)%\{([^%{}]++)\}(.*+)""".r

  private def splitDissection(dissection: String): List[String] = dissection match {
    case fieldPat(start, field, rest) => start :: field :: splitDissection(rest)
    case other => other :: Nil
  }

  /** For example <code>(?&lt;bla&gt;((?!,).)*?),.*</code>. */
  private def dissectionRegex(splitted: List[String], startidx: Int = 0): String = splitted match {
    case delim1 :: field :: delim2 :: rest =>
      quote(delim1) + "(?<" + startidx + ":" + field + ">(?:(?!" + quote(delim2) + ").)*?)" +
        dissectionRegex(delim2 :: rest, startidx + 1)
    case delim :: Nil => quote(delim)
  }

  /** Generates a regex with named groups according to dissection. */
  def dissectionRegex(dissection: String): Regex = dissectionRegex(splitDissection(dissection)).r

  case class DissectionResult(fields: List[(String, String)], restSplitted: List[String], restNotMatching: Option[String])

  /** Parses line and returns parsed part and rest which could not be parsed. */
  def dissect(dissection: String, line: String): DissectionResult = parseDissection(splitDissection(dissection), line)

  private def parseDissection(splitted: List[String], line: String): DissectionResult = splitted match {
    case delim :: rest if !line.startsWith(delim) => DissectionResult(Nil, delim :: rest, Option(line))
    case delim1 :: field :: delim2 :: rest =>
      val afterDelim1 = line.substring(delim1.length)
      val delim2idx = afterDelim1.indexOf(delim2)
      if (delim2idx >= 0) {
        val fieldContent = afterDelim1.substring(0, delim2idx)
        val restParsed = parseDissection(delim2 :: rest, afterDelim1.substring(delim2idx))
        restParsed.copy(fields = (field, fieldContent) :: restParsed.fields)
      } else {
        DissectionResult(Nil, delim2 :: rest, Option(afterDelim1))
      }
    case delim :: Nil =>
      if (line.equals(delim)) DissectionResult(Nil, Nil, Option.empty)
      else DissectionResult(Nil, delim :: Nil, Option(line))
  }

}

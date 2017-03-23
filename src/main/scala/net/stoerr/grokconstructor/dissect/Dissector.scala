package net.stoerr.grokconstructor.dissect

import scala.util.matching.Regex
import Regex.quote

/**
  * Models the logstash dissect plugin.
  *
  * @see "https://www.elastic.co/guide/en/logstash/current/plugins-filters-dissect.html"
  */
object Dissector {

  /** Matches prefix, field and rest in a dissection. */
  private val fieldPat = """([^%{}]*+)%\{([^%{}]++)\}(.*+)""".r

  private def splitDissection(dissection: String) : List[String] = dissection match {
    case fieldPat(start,field,rest) => start :: field :: splitDissection(rest)
    case other => other :: Nil
  }

  /** For example <code>(?&lt;bla&gt;((?!,).)*?),.*</code>. */
  private def dissectionRegex(splitted: List[String]) : String = splitted match {
    case delim1 :: field :: delim2 :: rest =>
      quote(delim1) + "(?<" + field + ">(?:(?!" + quote(delim2) + ").)*?)" + dissectionRegex(delim2 :: rest)
    case delim :: Nil => quote(delim)
  }

  /** Generates a regex with named groups according to dissection. */
  // TODO: named groups doesn't work, since names can be duplicated.
  def dissectionRegex(dissection: String) : Regex = dissectionRegex(splitDissection(dissection)).r

}

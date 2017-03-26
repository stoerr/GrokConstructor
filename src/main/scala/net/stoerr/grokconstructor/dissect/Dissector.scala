package net.stoerr.grokconstructor.dissect

import scala.collection.immutable
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

  // ====== automatic dissector construction ======

  private def commonPrefix(lines: Array[String]): String = {
    val minlength = lines.map(_.length).min
    val firstDifference = Range(0, minlength + 1).filter(idx => idx >= minlength || lines.map(_ (idx)).exists(_ != lines(0)(idx))).head
    lines(0).substring(0, firstDifference)
  }

  def substrings(line: String): Iterator[String] =
    Range(0, line.length - 1).iterator.flatMap(start => Range(start + 1, line.length + 1).iterator.map(end => line.substring(start, end)))

  def commonSubstrings(lines: Array[String]): Set[String] = lines.iterator.map(substrings(_).toSet).reduce(_ intersect _)

  def commonSubstrings2(lines: Array[String]): Set[String] =
    lines.iterator.drop(1).foldLeft(substrings(lines(0)).toSet)((x, y) => substrings(y).filter(x.contains(_)).toSet)

  def commonSubstrings3(lines: Array[String]): Set[String] = {
    val sortedLines = lines.sortBy(_.length)
    val firstTwo = substrings(sortedLines(0)).filter(sortedLines(1).contains(_)).toSet
    sortedLines.iterator.drop(2).foldLeft(firstTwo)((x, y) => substrings(y).filter(x.contains(_)).toSet)
  }

  def commonSubstrings4(lines: Array[String]): Seq[String] =
    lines.iterator.drop(1).foldLeft(substrings(lines(0)))((x, y) => x.filter(y.contains(_))).toStream.distinct

  def isInMiddleAndRestAfterDelim(line: String, delim: String): (Boolean, Option[String]) = {
    val idx = line.indexOf(delim)
    if (0 <= idx) (0 < idx, Option(line.substring(idx + delim.length))) else (false, Option.empty)
  }

  def notAllwaysFirstAndRestAfterDelim(lines: Array[String], delim: String): Option[Array[String]] = {
    val rests: immutable.Seq[(Boolean, Option[String])] = lines.toStream.map(isInMiddleAndRestAfterDelim(_, delim))
    if (rests.exists(_._2.isEmpty) || !rests.exists(_._1)) Option.empty else Option(rests.map(_._2.get).toArray)
  }

  def dissectorsAsIterator(lines: Array[String]): Iterator[List[String]] = {
    val start = commonPrefix(lines)
    val linesAtStartOfFields = lines.map(_.substring(start.length))
    val substrings = commonSubstrings4(linesAtStartOfFields)

    def dissectionStartingWithField(rests: Array[String]): Iterator[List[String]] = {
      if (rests.exists(_.isEmpty)) return Iterator(List(""))
      substrings.iterator.map(s => (s, notAllwaysFirstAndRestAfterDelim(rests, s))).filter(_._2.isDefined)
        .flatMap(p => dissectionStartingWithField(p._2.get).map(p._1 :: _))
    }

    dissectionStartingWithField(linesAtStartOfFields).map(start :: _)
  }

  def dissectors(lines: Array[String]): Iterator[String] = dissectorsAsIterator2(lines).map(listAsDissector)

  private def listAsDissector(delims: List[String]): String = delims.reduce(_ + "%{}" + _)

  def restAfterDelim(line: String, delim: String): Option[String] = {
    val idx = line.indexOf(delim)
    if (0 <= idx) Option(line.substring(idx + delim.length)) else Option.empty
  }

  private val hasLetterOrDigit = ".*[a-zA-Z0-9].*".r

  def substringsNoLetterNoDigit(line: String): Iterator[String] =
    Range(0, line.length - 1).iterator.flatMap(start =>
      Range(start + 1, line.length + 1).iterator
        .map(end => line.substring(start, end))
        .takeWhile(hasLetterOrDigit.findFirstIn(_).isEmpty))

  def dissectorsAsIterator2(lines: Array[String]): Iterator[List[String]] = {
    val start = commonPrefix(lines)
    val linesAtStartOfFields = lines.map(_.substring(start.length))

    def dissectionStartingWithField(rests: Array[String]): Iterator[List[String]] = {
      if (rests.exists(_.isEmpty)) return Iterator(List(""))
      substringsNoLetterNoDigit(rests.sortBy(_.length).apply(0))
        .filter(s => rests.forall(_.contains(s)))
        .filterNot(s => rests.forall(_.startsWith(s)))
        .flatMap(s => dissectionStartingWithField(rests.map(restAfterDelim(_, s).get)).map(s :: _))
    }

    dissectionStartingWithField(linesAtStartOfFields).map(start :: _)
  }

}

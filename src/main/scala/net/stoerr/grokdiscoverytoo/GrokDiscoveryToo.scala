package net.stoerr.grokdiscoverytoo

import net.stoerr.grokdiscoverytoo.GrokDiscoveryToo.{FixedString, NamedRegex, RegexPart}

/**
 * We try to find all sensible regular expressions consisting of grok patterns and fixed strings that
 * match all of a given collection of lines. The algorithm is roughly: in each step we look whether the first characters
 * of all rest-lines are equal and are not letters/digits. If they are, we take that for the regex. If they aren't we try to match all grok
 * regexes against the string. The regexes are partitioned into sets that match exactly the same prefixes of all
 * rest-lines, sort these according to the average length of the matches and try these.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 06.02.13
 */
class GrokDiscoveryToo(grokMap: Map[String, String]) {

  val namedRegexps: Map[String, JoniRegex] = grokMap.map {
    case (name, regex) => (name -> new JoniRegex(GrokPatternLibrary.replacePatterns(regex, grokMap)))
  }

  /** We try at most this many calls to avoid endless loops because of
    * the combinatorical explosion */
  var callCountdown = 10000

  def matchingRegexpStructures(lines: List[String]): Iterator[List[RegexPart]] = {
    if (callCountdown <= 0) return Iterator(List(FixedString("SEARCH TRUNCATED")))
    callCountdown -= 1
    if (lines.find(!_.isEmpty).isEmpty) return Iterator(List())
    val commonPrefix = biggestCommonPrefixExceptDigitsOrLetters(lines)
    if (0 < commonPrefix.length) {
      val restlines = lines.map(_.substring(commonPrefix.length))
      return matchingRegexpStructures(restlines).map(FixedString(commonPrefix) :: _)
    } else {
      val regexpand = for ((name, regex) <- namedRegexps.toList) yield (name, lines.map(regex.matchStartOf(_)))
      val candidates = regexpand.filter(_._2.find(_.isEmpty).isEmpty)
        .filterNot(_._2.find(_.get.length > 0).isEmpty)
      val candidateToMatches = candidates.map {
        case (name, matches) => (name, matches.map(_.get))
      }
      val candidatesGrouped: Map[List[StartMatch], List[String]] = candidateToMatches.groupBy(_._2).mapValues(_.map(_._1))
      val candidatesSorted = candidatesGrouped.toList.sortBy(-_._1.map(_.length).sum)
      val res = for ((matches, names) <- candidatesSorted) yield {
        val restlines = matches.map(_.rest)
        matchingRegexpStructures(restlines).map(NamedRegex(names) :: _)
      }
      return res.fold(Iterator())(_ ++ _)
    }
  }

  /** The longest string that is a prefix of all lines. */
  def biggestCommonPrefixExceptDigitsOrLetters(lines: List[String]): String =
    if (lines.size != 1) lines.reduce(commonPrefixExceptDigitsOrLetters)
    else lines(0).takeWhile(!_.isLetterOrDigit)

  def commonPrefixExceptDigitsOrLetters(str1: String, str2: String) = str1.zip(str2).takeWhile(p => (p._1 == p._2 && !p._1.isLetterOrDigit)).map(_._1).mkString("")

}

object GrokDiscoveryToo {

  sealed trait RegexPart

  case class FixedString(str: String) extends RegexPart

  case class NamedRegex(regexps: List[String]) extends RegexPart

}



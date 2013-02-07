package net.stoerr.grokdiscoverytoo

/**
 * We try to find all sensible regular expressions consisting of grok patterns and fixed strings that
 * match all of a given collection of lines. The algorithm is roughly: in each step we look whether the first characters
 * of all rest-lines are equal. If they are, we take that for the regex. If they aren't we try to match all grok
 * regexes against the string. The regexes are partitioned into sets that match exactly the same prefixes of all
 * rest-lines, sort these according to the average length of the matches and try these.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 06.02.13
 */
class GrokDiscoveryToo(namedRegexps: Map[String, JoniRegex]) {

  def matchingRegexpStructures(lines: List[String]): Iterator[List[RegexPart]] = {
    val commonPrefix = biggestCommonPrefix(lines)
    if ("" != commonPrefix) {
      val restlines = lines.map(_.substring(commonPrefix.length))
      return matchingRegexpStructures(restlines).map(RegexPart(List(commonPrefix)) :: _)
    } else {
      val regexpand = for ((name, regex) <- namedRegexps.toList) yield (name, lines.map(regex.matchStartOf(_)))
      val candidates = regexpand.filter(_._2.find(_.isEmpty).isDefined)
      val candidateToMatches = candidates.map {
        case (name, matches) => (name, matches.map(_.get))
      }
      val candidatesGrouped: Map[List[StartMatch], List[String]] = candidateToMatches.groupBy(_._2).mapValues(_.map(_._1))
      val candidatesSorted = candidatesGrouped.toList.sortBy(-_._1.map(_.length).sum)
      val res = for ((matches, names) <- candidatesSorted) yield {
        val restlines = matches.map(_.rest)
        matchingRegexpStructures(restlines).map(RegexPart(names) :: _)
      }
      return res.sum
    }
  }

  /** The longest string that is a prefix of all lines. */
  def biggestCommonPrefix(lines: List[String]): String = lines.reduce(commonPrefix)

  def commonPrefix(str1: String, str2: String) = str1.zip(str2).takeWhile(p => (p._1 == p._2)).map(_._1).mkString("")

}

case class RegexPart(regexps: List[String])


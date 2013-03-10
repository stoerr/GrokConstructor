package net.stoerr.grokdiscoverytoo.automatic

import net.stoerr.grokdiscoverytoo.webframework.WebView
import xml.NodeSeq
import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.webframework.TableMaker._
import net.stoerr.grokdiscoverytoo.automatic.AutomaticDiscoveryView.{RegexPart, NamedRegex, FixedString}
import net.stoerr.grokdiscoverytoo.{GrokPatternLibrary, JoniRegex, StartMatch}

/**
 * We try to find all sensible regular expressions consisting of grok patterns and fixed strings that
 * match all of a given collection of lines. The algorithm is roughly: in each step we look whether the first characters
 * of all rest-lines are equal and are not letters/digits. If they are, we take that for the regex. If they aren't we try to match all grok
 * regexes against the string. The regexes are partitioned into sets that match exactly the same prefixes of all
 * rest-lines, sort these according to the average length of the matches and try these.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 08.03.13
 */
class AutomaticDiscoveryView(val request: HttpServletRequest) extends WebView {

  val form = AutomaticDiscoveryForm(request)

  override val title: String = "Automatic grok discovery"
  override val action: String = AutomaticDiscoveryView.path

  override def inputform: NodeSeq = row(<span>Please enter some loglines for which you want generate possible grok patterns and then press
    <input type="submit" value="Go!"/>
    You can also just try this out with a
    <input type="submit" name="randomize" value="random example."/>
  </span>) ++
    form.loglinesEntry ++
    form.grokpatternEntry

  override def result: NodeSeq = form.loglines.valueSplitToLines.map(_.toList).map(matchingRegexpStructures).map(resultTable).getOrElse(<span/>)

  def resultTable(results: Iterator[List[RegexPart]]): xml.Elem = <table>
    {rowheader("Possible grok regex combinations that match all lines")}{results.toList map {
      result =>
        row(result map {
          case FixedString(str) => <span>
            {'»' + str + '«'}
          </span>
          case NamedRegex(patterns) if (patterns.size == 1) => <span>
            {"%{" + patterns(0) + "}"}
          </span>
          case NamedRegex(patterns) => <select>
            {patterns map {
              pattern => <option>
                {"%{" + pattern + "}"}
              </option>
            }}
          </select>
        })
    }}
  </table>

  lazy val namedRegexps: Map[String, JoniRegex] = form.grokPatternLibrary.map {
    case (name, regex) => (name -> new JoniRegex(GrokPatternLibrary.replacePatterns(regex, form.grokPatternLibrary)))
  }
  lazy val namedRegexpsList: List[(String, JoniRegex)] = namedRegexps.toList

  /** We try at most this many calls to avoid endless loops because of
    * the combinatorical explosion */
  var callCountdown = 10000

  def matchingRegexpStructures(lines: List[String]): Iterator[List[RegexPart]] = {
    if (callCountdown <= 0) return Iterator(List(FixedString("SEARCH TRUNCATED")))
    callCountdown -= 1
    if (lines.find(!_.isEmpty).isEmpty) return Iterator(List())
    val commonPrefix = AutomaticDiscoveryView.biggestCommonPrefixExceptDigitsOrLetters(lines)
    if (0 < commonPrefix.length) {
      val restlines = lines.map(_.substring(commonPrefix.length))
      return matchingRegexpStructures(restlines).map(FixedString(commonPrefix) :: _)
    } else {
      val regexpand = for ((name, regex) <- namedRegexpsList) yield (name, lines.map(regex.matchStartOf(_)))
      val candidatesThatMatchAllLines = regexpand.filter(_._2.find(_.isEmpty).isEmpty)
      val candidates = candidatesThatMatchAllLines.filterNot(_._2.find(_.get.length > 0).isEmpty)
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

}

object AutomaticDiscoveryView {

  val path = "/web/automatic"

  sealed trait RegexPart

  case class FixedString(str: String) extends RegexPart

  case class NamedRegex(regexps: List[String]) extends RegexPart

  /** The longest string that is a prefix of all lines. */
  def biggestCommonPrefixExceptDigitsOrLetters(lines: List[String]): String =
    if (lines.size != 1) lines.reduce(commonPrefixExceptDigitsOrLetters)
    else wrapString(lines(0)).takeWhile(!_.isLetterOrDigit)

  def commonPrefixExceptDigitsOrLetters(str1: String, str2: String) =
    wrapString(str1).zip(wrapString(str2)).takeWhile(p => (p._1 == p._2 && !p._1.isLetterOrDigit)).map(_._1).mkString("")

}

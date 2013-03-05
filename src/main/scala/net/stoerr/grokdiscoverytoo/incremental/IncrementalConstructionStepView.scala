package net.stoerr.grokdiscoverytoo.incremental

import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.webframework.WebView
import xml.NodeSeq
import net.stoerr.grokdiscoverytoo.webframework.TableMaker._
import net.stoerr.grokdiscoverytoo.{JoniRegex, GrokPatternLibrary, RandomTryLibrary}
import collection.immutable.NumericRange

/**
 * Performs a step in the incremental construction of the grok pattern.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 02.03.13
 */
class IncrementalConstructionStepView(val request: HttpServletRequest) extends WebView {

  override val title: String = "Incremental Construction of Grok Patterns in progress"
  override val action: String = IncrementalConstructionStepView.path

  val form = IncrementalConstructionForm(request)

  override def doforward: Option[Either[String, WebView]] = if (null == request.getParameter("randomize")) None
  else Some(Left(IncrementalConstructionInputView.path + "?example=" + RandomTryLibrary.randomExampleNumber()))

  def inputform: NodeSeq =
    row(<span>
      <input type="submit" value="Go!"/>
    </span>) ++
      form.loglines.hiddenField ++
      form.constructedRegex.hiddenField ++
      form.grokhiddenfields ++
      form.multlinehiddenfields

  // TODO missing: add extra patterns by hand later

  val currentRegex = form.constructedRegex.value.getOrElse("\\A") + form.nextPart.value.getOrElse("")
  form.constructedRegex.value = Some(currentRegex)

  val currentJoniRegex = new JoniRegex(GrokPatternLibrary.replacePatterns(currentRegex, form.grokPatternLibrary))
  val loglinesSplitted: Array[(String, String)] = form.loglines.valueSplitToLines.get.map({
    line =>
      val jmatch = currentJoniRegex.matchStartOf(line)
      (jmatch.get.matched, jmatch.get.rest)
  })
  val loglineRests: Array[String] = loglinesSplitted.map(_._2)

  override def result: NodeSeq = {
    <table border="1">
      {rowheader2("Matched", "Rest") ++
      loglinesSplitted.map {
        case (start, rest) => row2(<code>
          {start}
        </code>, <code>
          {rest}
        </code>)
      }}
    </table> ++ <table border="1">
      {rowheader("Please choose one of the following continuations of your regular expression") ++
        commonprefixesOfLoglineRests.map(p => row(form.nextPart.radiobutton(p, <code>
          {'»' + p + '«'}
        </code>))) ++
        groknameListToMatchesCleanedup}
    </table>
  }

  private def commonprefixesOfLoglineRests: Iterator[String] = {
    val biggestprefix = biggestCommonPrefix(loglineRests)
    NumericRange.inclusive(1, biggestprefix.length, 1).toIterator
      .map(biggestprefix.substring(0, _))
  }

  // unfortunately wrapString collides with TableMaker.stringToNode , so we use it explicitly
  private def commonPrefix(str1: String, str2: String) = wrapString(str1).zip(wrapString(str2)).takeWhile(p => (p._1 == p._2)).map(_._1).mkString("")

  /** The longest string that is a prefix of all lines. */
  private def biggestCommonPrefix(lines: Seq[String]): String =
    if (lines.size > 1) lines.reduce(commonPrefix) else lines(0)

  val groknameToMatches: List[(String, List[String])] = for {
    grokname <- form.grokPatternLibrary.keys.toList
    regex = new JoniRegex(GrokPatternLibrary.replacePatterns("%{" + grokname + "}", form.grokPatternLibrary))
    restlinematchOptions = loglineRests.map(regex.matchStartOf(_))
    if (restlinematchOptions.find(_.isEmpty)).isEmpty
    restlinematches: List[String] = restlinematchOptions.map(_.get.matched).toList
  } yield (grokname, restlinematches)
  /** List of pairs of a list of groknames that have identical matches on the restlines to the list of matches. */
  val groknameListToMatches: List[(List[String], List[String])] = groknameToMatches.groupBy(_._2).map(p => (p._2.map(_._1), p._1)).toList
  /** groknameListToMatches that have at least one nonempty match, sorted by the sum of the lengths of the matches. */
  val groknameListToMatchesCleanedup = groknameListToMatches.filter(_._2.find(!_.isEmpty).isDefined).sortBy(-_._2.map(_.length).sum)

}

object IncrementalConstructionStepView {
  val path = "/web/constructionstep"
}

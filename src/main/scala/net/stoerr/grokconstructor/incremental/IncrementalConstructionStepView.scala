package net.stoerr.grokconstructor.incremental

import javax.servlet.http.HttpServletRequest
import net.stoerr.grokconstructor.webframework.{WebViewWithHeaderAndSidebox, WebView}
import scala.xml.{Text, NodeSeq}
import net.stoerr.grokconstructor.{JoniRegexQuoter, JoniRegex, GrokPatternLibrary, RandomTryLibrary}
import collection.immutable.NumericRange
import net.stoerr.grokconstructor.matcher.MatcherEntryView

/**
 * Performs a step in the incremental construction of the grok pattern.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 02.03.13
 */
class IncrementalConstructionStepView(val request: HttpServletRequest) extends WebViewWithHeaderAndSidebox {

  override val title: String = "Incremental Construction of Grok Patterns in progress"

  override def action: String =
    if (!constructionDone) IncrementalConstructionStepView.path
    else MatcherEntryView.path

  override def doforward: Option[Either[String, WebView]] =
    if (null != request.getParameter("randomize"))
      Some(Left(IncrementalConstructionInputView.path + "?example=" + RandomTryLibrary.randomExampleNumber()))
    else if (null != request.getParameter("matchrests")) {
      val view = new MatcherEntryView(request)
      view.form.loglines.value = Some(loglineRests.mkString("\n"))
      Some(Right(view))
    } else None

  def maintext: NodeSeq = if (!constructionDone) <p>Please select the next component for the grok pattern.
    You can select can either select a fixed string (e.g. a separator), a (possibly named) pattern from the grok
    pattern library, or a pattern you explicitly specify. Make your selection and press</p> ++ submit("Continue!")
  else <p>All log lines are successfully matched. You can copy the regular expression from the form field below.
    You can also try out the constructed regex by calling the matcher.</p> ++ submit("Go to matcher")

  def sidebox: NodeSeq = <p>To try out how regular expressions on the unmatched rests press</p> ++ submit("Match restlines!", "matchrests", "_blank")

  override def result: NodeSeq = <span/>


  val form = IncrementalConstructionForm(request)

  def formparts: NodeSeq = form.constructedRegex.inputText("Constructed regular expression so far: ", 180, false) ++
    form.loglines.hiddenField ++
    form.constructedRegex.hiddenField ++
    form.grokhiddenfields ++
    form.multlinehiddenfields ++ selectionPart

  // TODO missing: add extra patterns by hand later

  val currentRegex = form.constructedRegex.value.getOrElse("\\A") + getNamedNextPartOrEmpty
  form.constructedRegex.value = Some(currentRegex)

  private def getNamedNextPartOrEmpty = {
    val nextPart = form.nextPart.value.getOrElse("")
    if (nextPart == form.nextPartPerHandMarker) form.nextPartPerHand.value.getOrElse("")
    else form.nameOfNextPart.value match {
      case None => nextPart
      case Some(name) => nextPart.replaceFirst( """^%\{(\w+)}$""", """%{$1:""" + name + "}")
    }
  }

  val currentJoniRegex = new JoniRegex(GrokPatternLibrary.replacePatterns(currentRegex, form.grokPatternLibrary))
  val loglinesSplitted: Seq[(String, String)] = form.multlineFilter(form.loglines.valueSplitToLines).map({
    line =>
      val jmatch = currentJoniRegex.matchStartOf(line)
      (jmatch.get.matched, jmatch.get.rest)
  })
  val loglineRests: Seq[String] = loglinesSplitted.map(_._2)
  val constructionDone = loglineRests.forall(_.isEmpty)

  def selectionPart: NodeSeq = {
    val alreadymatchedtable = table(
      rowheader2("Already matched", "Unmatched rest of the loglines to match") ++
        loglinesSplitted.map {
          case (start, rest) => row2(<code>
            {start}
          </code>, <code>
            {visibleWhitespaces(rest)}
          </code>)
        }
    )
    if (!constructionDone) {
      alreadymatchedtable ++
        formsection("To choose a continuation of your regular expression you can either choose a fixed string that is common to all log file line rests as a separator:") ++
        <div class="ym-fbox-check">
          {commonprefixesOfLoglineRests.map(p => form.nextPart.radiobutton(JoniRegexQuoter.quote(p), <code>
          {'»' + visibleWhitespaces(p) + '«'}
        </code>)).reduceOption(_ ++ _).getOrElse(<span/>)}
        </div> ++
        formsection("or select one of the following expressions from the grok library that matches a segment of the log lines:") ++
        form.nameOfNextPart.inputText("Optional: give name for the grok expression to retrieve it's match value", 20) ++
        table(
          rowheader2("Grok expression", "Matches at the start of the rest of the loglines") ++
            groknameListToMatchesCleanedup.map(grokoption)) ++
        formsection("or you can input a regex that will match the next part of all logfile lines:") ++
        <div class="ym-fbox-check">
          {form.nextPart.radiobutton(form.nextPartPerHandMarker, "continue with handmade regex")}
        </div> ++
        form.nextPartPerHand.inputText("regular expression for next component:", 170)
    } else alreadymatchedtable
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

  def grokoption(grokopt: (List[String], List[String])) = grokopt match {
    case (groknames, restlinematches) =>
      row2(
        <div class="ym-fbox-check">
          {groknames.sorted.map(grokname =>
          form.nextPart.radiobutton("%{" + grokname + "}", <code/>.copy(child = new Text("%{" + grokname + "}"))))
          .reduce(_ ++ _)}
        </div>, <pre/>.copy(child = new Text(visibleWhitespaces(restlinematches.mkString("\n"))))
      )
  }

}

object IncrementalConstructionStepView {
  val path = "/constructionstep"
}

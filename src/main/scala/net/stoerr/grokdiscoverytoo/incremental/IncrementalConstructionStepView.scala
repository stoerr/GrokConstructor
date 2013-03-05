package net.stoerr.grokdiscoverytoo.incremental

import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.webframework.WebView
import xml.NodeSeq
import net.stoerr.grokdiscoverytoo.webframework.TableMaker._
import net.stoerr.grokdiscoverytoo.{JoniRegex, GrokPatternLibrary, RandomTryLibrary}
import collection.immutable.{NumericRange, WrappedString}

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
        </code>)))}
    </table>
  }

  private def commonprefixesOfLoglineRests: Iterator[String] = {
    val biggestprefix = biggestCommonPrefix(loglineRests)
    NumericRange.inclusive(1, biggestprefix.length, 1).toIterator
      .map(biggestprefix.substring(0, _))
  }

  // no idea why the implicit conversion to WrappedString does not work here. Somehow it collides with TableMaker.stringToNode .
  private def commonPrefix(str1: String, str2: String) = new WrappedString(str1).zip(new WrappedString(str2)).takeWhile(p => (p._1 == p._2)).map(_._1).mkString("")

  /** The longest string that is a prefix of all lines. */
  private def biggestCommonPrefix(lines: Seq[String]): String =
    if (lines.size > 1) lines.reduce(commonPrefix) else lines(0)

}

object IncrementalConstructionStepView {
  val path = "/web/constructionstep"
}

package net.stoerr.grokdiscoverytoo.incremental

import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.webframework.WebView
import xml.NodeSeq
import net.stoerr.grokdiscoverytoo.webframework.TableMaker._
import net.stoerr.grokdiscoverytoo.{JoniRegex, GrokPatternLibrary, RandomTryLibrary}

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
      form.grokhiddenfields ++
      form.multlinehiddenfields ++
      form.constructedRegex.inputText(80)               // TODO hidden

  // TODO missing: add extra patterns by hand later

  val currentRegex = form.constructedRegex.value.getOrElse("\\A")
  val currentJoniRegex = new JoniRegex(GrokPatternLibrary.replacePatterns(currentRegex, form.grokPatternLibrary))
  val loglinesSplitted: Array[(String, String)] = form.loglines.valueSplitToLines.get.map({
    line =>
      val jmatch = currentJoniRegex.matchStartOf(line)
      (jmatch.get.matched, jmatch.get.rest)
  })
  val loglineRests: Array[String] = loglinesSplitted.map(_._2)

  override def result: NodeSeq = {
    <table border="1">{
      rowheader2("Matched", "Rest") ++
      loglinesSplitted.map {
        case (start, rest) => row2(<code>{start}</code>, <code>{rest}</code>)
      }
    } </table>
  }

}

object IncrementalConstructionStepView {
  val path = "/web/constructionstep"
}

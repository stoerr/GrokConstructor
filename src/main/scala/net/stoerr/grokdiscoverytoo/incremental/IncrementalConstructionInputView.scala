package net.stoerr.grokdiscoverytoo.incremental

import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.webframework.{WebView, WebViewWithHeaderAndSidebox}
import xml.NodeSeq
import net.stoerr.grokdiscoverytoo.RandomTryLibrary

/**
 * Entry for the start parameters for the incremental construction of grok patterns.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 01.03.13
 */
class IncrementalConstructionInputView(val request: HttpServletRequest) extends WebViewWithHeaderAndSidebox {
  val title: String = "Incremental Construction of Grok Patterns"
  val action: String = IncrementalConstructionStepView.path

  val form = IncrementalConstructionForm(request)

  override def doforward: Option[Either[String, WebView]] = if (null == request.getParameter("randomize")) None
  else Some(Left(fullpath(IncrementalConstructionInputView.path) + "?example=" + RandomTryLibrary.randomExampleNumber()))

  if (null != request.getParameter("example")) {
    val trial = RandomTryLibrary.example(request.getParameter("example").toInt)
    form.loglines.value = Some(trial.loglines)
    form.multlineRegex.value = trial.multline
    form.multlineNegate.values = List(form.multlineNegate.name)
    form.groklibs.values = List("grok-patterns")
  }

  def maintext: NodeSeq = <p>You can provide a number of log file lines and step by step construct a grok pattern
    that matches all of these lines. In each step you select or input a pattern that matches the next logical segment
    of the log line. This can either be a fixed string (e.g. a separator), a (possibly named) pattern from the grok
    pattern library, or a pattern you explicitly specify.</p> ++
     <p>Please enter some loglines for which you want to create a grok pattern, mark the pattern libraries you want to
      draw your patterns from and then press</p> ++ submit("Go!")

  def sidebox: NodeSeq = <p>You can also just fill this with a</p> ++ buttonanchor(IncrementalConstructionInputView.path + "?randomize", "random example.")

  def formparts: NodeSeq = form.loglinesEntry ++ form.grokpatternEntry ++ form.multlineEntry

  // missing: extra patterns by hand

  def result: NodeSeq = <span/>

}

object IncrementalConstructionInputView {
  val path = "/construction"
}

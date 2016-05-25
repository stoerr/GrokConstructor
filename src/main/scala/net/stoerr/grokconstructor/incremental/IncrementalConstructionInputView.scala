package net.stoerr.grokconstructor.incremental

import javax.servlet.http.HttpServletRequest

import net.stoerr.grokconstructor.RandomTryLibrary
import net.stoerr.grokconstructor.webframework.{WebView, WebViewWithHeaderAndSidebox}

import scala.xml.NodeSeq

/**
 * Entry for the start parameters for the incremental construction of grok patterns.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 01.03.13
 */
class IncrementalConstructionInputView(val request: HttpServletRequest) extends WebViewWithHeaderAndSidebox {
  val title: String = "Incremental Construction of Grok Patterns"
  val form = IncrementalConstructionForm(request)

  def action: String = IncrementalConstructionStepView.path

  override def doforward: Option[Either[String, WebView]] = if (null == request.getParameter("randomize")) None
  else Some(Left(fullpath(IncrementalConstructionInputView.path) + "?example=" + RandomTryLibrary.randomExampleNumber()))

  if (null != request.getParameter("example")) {
    val trial = RandomTryLibrary.example(request.getParameter("example").toInt)
    form.loglines.value = Some(trial.loglines)
    form.multilineRegex.value = trial.multiline
    form.multilineNegate.values = List(form.multilineNegate.name)
    form.groklibs.values = List("grok-patterns", "java")
  }

  def maintext: NodeSeq = <p>You can provide a number of log file lines and step by step construct a
    <a href="http://logstash.net/docs/latest/filters/grok">grok pattern</a>
    that matches all of these lines. In each step you select or input a pattern that matches the next logical segment
    of the log line. This can either be a fixed string (e.g. a separator), a (possibly named) pattern from the grok
    pattern library, or a pattern you explicitly specify.
    You can also apply a <a href="http://logstash.net/docs/latest/filters/multiline">multiline filter</a> first.</p> ++
    <p>In the form below, please enter some loglines for which you want to create a grok pattern, mark the pattern
      libraries you want to draw your patterns from and then press</p> ++ submit("Go!")

  def sidebox: NodeSeq = <p>You can also just fill this with a</p> ++ buttonanchor(IncrementalConstructionInputView.path + "?randomize", "random example.")

  def formparts: NodeSeq = form.loglinesEntry ++ form.grokpatternEntry ++ form.multilineEntry

  // missing: extra patterns by hand

  def result: NodeSeq = <span/>

}

object IncrementalConstructionInputView {
  val path = "/construction"
}

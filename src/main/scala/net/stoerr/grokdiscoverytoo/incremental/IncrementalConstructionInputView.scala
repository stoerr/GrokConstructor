package net.stoerr.grokdiscoverytoo.incremental

import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.webframework.WebViewWithHeaderAndSidebox
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

  if (null != request.getParameter("example")) {
    val trial = RandomTryLibrary.example(request.getParameter("example").toInt)
    form.loglines.value = Some(trial.loglines)
    form.multlineRegex.value = trial.multline
    form.multlineNegate.values = List(form.multlineNegate.name)
    form.groklibs.values = List("grok-patterns")
  }

  def maintext: NodeSeq = <p>Please enter some loglines you want to construct a grok pattern for and then press</p> ++ submit("Go!")

  def sidebox: NodeSeq = <p>You can also just fill this with a</p> ++ buttonanchor(action + "?randomize", "random example.")

  def formparts: NodeSeq = form.loglinesEntry ++ form.grokpatternEntry ++ form.multlineEntry

  // missing: extra patterns by hand

  def result: NodeSeq = <span/>

}

object IncrementalConstructionInputView {
  val path = "/do/construction"
}

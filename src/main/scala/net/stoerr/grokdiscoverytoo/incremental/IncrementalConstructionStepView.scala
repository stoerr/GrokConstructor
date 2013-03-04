package net.stoerr.grokdiscoverytoo.incremental

import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.webframework.WebView
import xml.NodeSeq
import net.stoerr.grokdiscoverytoo.webframework.TableMaker._
import net.stoerr.grokdiscoverytoo.RandomTryLibrary

/**
 * Performs a step in the incremental construction of the grok pattern.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 02.03.13
 */
class IncrementalConstructionStepView(val request: HttpServletRequest) extends WebView {

  val title: String = "Incremental Construction of Grok Patterns in progress"
  val action: String = IncrementalConstructionStepView.path

  val form = IncrementalConstructionForm(request)

  override def doforward: Option[Either[String, WebView]] = if (null == request.getParameter("randomize")) None
  else Some(Left(IncrementalConstructionInputView.path + "?example=" + RandomTryLibrary.randomExampleNumber()))

  def inputform: NodeSeq =
    row(<span>
      <input type="submit" value="Go!"/>
    </span>) ++
      form.loglines.hiddenField ++
      form.grokhiddenfields ++
      form.multlinehiddenfields

  // missing: extra patterns by hand

  def result: NodeSeq = <span>TODO</span>

}

object IncrementalConstructionStepView {
  val path = "/web/constructionstep"
}

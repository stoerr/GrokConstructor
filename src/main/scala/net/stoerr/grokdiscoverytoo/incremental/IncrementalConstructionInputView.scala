package net.stoerr.grokdiscoverytoo.incremental

import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.webframework.WebView
import xml.NodeSeq
import net.stoerr.grokdiscoverytoo.webframework.TableMaker._

/**
 * Entry for the start parameters for the incremental construction of grok patterns.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 01.03.13
 */
class IncrementalConstructionInputView(val request: HttpServletRequest) extends WebView {
  val title: String = "Incremental Construction of Grok Patterns"
  val action: String = "/web/construction"

  val form = IncrementalConstructionForm(request)

  def inputform: NodeSeq =
    row(<span>Please enter some loglines you want to construct a grok pattern for and then press
      <input type="submit" value="Go!"/>
    </span>) ++
      form.loglinesEntry ++
      form.grokpatternEntry ++
      form.multlineEntry

  // missing: extra patterns by hand

  def result: NodeSeq = <span/>

}

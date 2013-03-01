package net.stoerr.grokdiscoverytoo.forms

import net.stoerr.grokdiscoverytoo.webframework.WebForm
import net.stoerr.grokdiscoverytoo.GrokPatternLibrary
import net.stoerr.grokdiscoverytoo.webframework.TableMaker._
import xml.NodeSeq

/**
 * Input(s) for grok patterns
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
trait GrokPatternFormPart extends WebForm {

  val groklibs = InputMultipleChoice("groklibs")

  val extralibs = InputMultipleChoice("grokextralibs")

  val grokadditionalinput = InputText("grokadditional")

  private def keyToGrokLink(key: String): (String, NodeSeq) =
    key -> <a href={"/service/grok/" + key}>
      {key}
    </a>

  def grokpatternEntry = row(<span>
    Grok Patterns from
    <a href="http://logstash.net/">logstash</a>
    v.1.19 :
    {groklibs.checkboxes(GrokPatternLibrary.grokpatternnames.map(keyToGrokLink).toMap)}
    , and some
    {extralibs.checkboxes(GrokPatternLibrary.extrapatternnames.map(keyToGrokLink).toMap)}
    from me
  </span>) ++
    row("Additional grok patterns:") ++
    row(grokadditionalinput.inputTextArea(5, 180))

  lazy val grokPatternLibrary =
    GrokPatternLibrary.mergePatternLibraries(groklibs.values ++ extralibs.values, grokadditionalinput.value)

}

package net.stoerr.grokdiscoverytoo.forms

import net.stoerr.grokdiscoverytoo.webframework.WebForm
import net.stoerr.grokdiscoverytoo.GrokPatternLibrary
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

  def grokpatternEntry =
    <div class="ym-fbox-text">
      <label>
        Grok Patterns from
        <a href="http://logstash.net/">logstash</a>
        v.1.19 :</label>
    </div> ++ groklibs.checkboxes(GrokPatternLibrary.grokpatternnames.map(keyToGrokLink).toMap) ++
      <div class="ym-fbox-text">
        <label>and some extras from me</label>
      </div> ++ extralibs.checkboxes(GrokPatternLibrary.extrapatternnames.map(keyToGrokLink).toMap) ++
      grokadditionalinput.inputTextArea("Additional grok patterns:", 5, 180)

  def grokhiddenfields: NodeSeq = groklibs.hiddenField ++ extralibs.hiddenField ++ grokadditionalinput.hiddenField

  lazy val grokPatternLibrary: Map[String, String] =
    GrokPatternLibrary.mergePatternLibraries(groklibs.values ++ extralibs.values, grokadditionalinput.value)

}

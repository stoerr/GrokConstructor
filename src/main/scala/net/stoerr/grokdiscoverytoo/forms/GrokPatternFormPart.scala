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

  val groklibs = InputMultipleChoice("groklibs", GrokPatternLibrary.grokpatternnames.map(keyToGrokLink).toMap)

  val extralibs = InputMultipleChoice("grokextralibs", GrokPatternLibrary.extrapatternnames.map(keyToGrokLink).toMap)

  val grokadditionalinput = InputText("grokadditional")

  private def keyToGrokLink(key: String): (String, NodeSeq) =
    key -> <a href={request.getContextPath + "/groklib/" + key}>
      {key}
    </a>

  def grokpatternEntry =
    <div class="ym-fbox-text">
      <label>
        Please mark the libraries of
        <a href="http://logstash.net/docs/1.1.9/filters/grok">grok Patterns</a>
        from
        <a href="http://logstash.net/">logstash</a>
        v.1.1.9 which you want to use:</label>
    </div> ++ groklibs.checkboxes ++
      <div class="ym-fbox-text">
        <label>and some extras from me</label>
      </div> ++ extralibs.checkboxes ++
      grokadditionalinput.inputTextArea("You can also provide some additional grok patterns in the same format:", 5, 180)

  def grokhiddenfields: NodeSeq = groklibs.hiddenField ++ extralibs.hiddenField ++ grokadditionalinput.hiddenField

  lazy val grokPatternLibrary: Map[String, String] =
    GrokPatternLibrary.mergePatternLibraries(groklibs.values ++ extralibs.values, grokadditionalinput.value)

}

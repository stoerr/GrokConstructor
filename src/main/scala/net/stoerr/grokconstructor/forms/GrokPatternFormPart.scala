package net.stoerr.grokconstructor.forms

import net.stoerr.grokconstructor.webframework.WebForm
import net.stoerr.grokconstructor.GrokPatternLibrary
import xml.NodeSeq

/**
 * Input(s) for grok patterns
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
trait GrokPatternFormPart extends WebForm {

  val groklibs = InputMultipleChoice("groklibs", GrokPatternLibrary.grokpatternnames.map(keyToGrokLink).toMap, GrokPatternLibrary.grokpatternnames)

  val grokadditionalinput = InputText("grokadditional")

  private def keyToGrokLink(key: String): (String, NodeSeq) =
    key -> <a href={request.getContextPath + "/groklib/" + key}>
      {key}
    </a>

  def grokpatternEntry =
    <div class="ym-fbox-text">
      <label>
        Please mark the libraries of
        <a href="http://logstash.net/docs/1.4.1/filters/grok">grok Patterns</a>
        from
        <a href="http://logstash.net/">logstash</a>
        v.1.4.1 which you want to use:</label>
    </div> ++ groklibs.checkboxes ++
      grokadditionalinput.inputTextArea("You can also provide some additional grok patterns in the same format:", 5, 180)

  def grokhiddenfields: NodeSeq = groklibs.hiddenField ++ grokadditionalinput.hiddenField

  lazy val grokPatternLibrary: Map[String, String] =
    GrokPatternLibrary.mergePatternLibraries(groklibs.values, grokadditionalinput.value)

}

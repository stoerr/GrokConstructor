package net.stoerr.grokconstructor.forms

import net.stoerr.grokconstructor.GrokPatternLibrary
import net.stoerr.grokconstructor.webframework.WebForm

import scala.xml.NodeSeq

/**
 * Input(s) for grok patterns
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
trait GrokPatternFormPart extends WebForm {

  lazy val grokPatternLibrary: Map[String, String] =
    GrokPatternLibrary.mergePatternLibraries(groklibs.values, grokadditionalinput.value)
  val groklibs = InputMultipleChoice("groklibs", GrokPatternLibrary.grokpatternnames.map(keyToGrokLink).toMap, GrokPatternLibrary.grokpatternnames)
  val grokadditionalinput = InputText("grokadditional")

  def grokpatternEntry =
    <div class="ym-fbox-text">
      <label>
        Please mark the libraries of
        <a href="http://logstash.net/docs/latest/filters/grok">grok Patterns</a>
        from
        <a href="http://logstash.net/">logstash</a>
        v.1.4.2 which you want to use:</label>
    </div> ++ groklibs.checkboxes ++
      grokadditionalinput.inputTextArea("You can also provide some additional grok patterns in the same format " +
        "as the pattern files linked above (i.e., on each line you give a pattern name, a space and the pattern):", 5, 180)

  def grokhiddenfields: NodeSeq = groklibs.hiddenField ++ grokadditionalinput.hiddenField

  private def keyToGrokLink(key: String): (String, NodeSeq) =
    key -> <a href={request.getContextPath + "/groklib/" + key}>
      {key}
    </a>

}

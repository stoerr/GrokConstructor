package net.stoerr.grokconstructor.forms

import java.util.logging.Logger

import net.stoerr.grokconstructor.GrokPatternLibrary
import net.stoerr.grokconstructor.webframework.WebForm

import scala.xml.NodeSeq

/**
 * Input(s) for grok patterns
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
trait GrokPatternFormPart extends WebForm {

  private val logger = Logger.getLogger("GrokPatternFormPart")

  lazy val grokPatternLibrary: Map[String, String] =
    GrokPatternLibrary.mergePatternLibraries(groklibs.values, grokadditionalinput.value)
  val groklibs = InputMultipleChoice("groklibs", GrokPatternLibrary.grokpatternnames.map(keyToGrokLink).toMap, GrokPatternLibrary.grokpatternnames)
  val grokadditionalinput = InputText("grokadditional")

  if (grokadditionalinput.value.isDefined) logger.fine("grokadditionalinput: " + grokadditionalinput.value)

  def grokpatternEntry =
    <div class="ym-fbox-text">
      <label>
        Please mark the libraries of
        <a href="http://logstash.net/docs/latest/filters/grok">grok Patterns</a>
        from
        <a href="http://logstash.net/">logstash</a>
        v.2.1 which you want to use. You probably want to use grok-patterns if you use any of the others, since they rely on the basic patterns defined there.</label>
    </div> ++ groklibs.checkboxes ++
      grokadditionalinput.inputTextArea("You can also provide a library of some additional grok patterns in the same format " +
              "as the pattern files linked above. On each line you give a pattern name, a space and the pattern. For example: WORD \\b\\w+\\b", 180, 5)

  def grokhiddenfields: NodeSeq = groklibs.hiddenField ++ grokadditionalinput.hiddenField

  private def keyToGrokLink(key: String): (String, NodeSeq) =
    key -> <a href={request.getContextPath + "/groklib/" + key}>
      {key}
    </a>

}

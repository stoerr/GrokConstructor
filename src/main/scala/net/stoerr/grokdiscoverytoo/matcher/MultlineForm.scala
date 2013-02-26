package net.stoerr.grokdiscoverytoo.matcher

import net.stoerr.grokdiscoverytoo.webframe.WebForm
import xml.NodeSeq
import net.stoerr.grokdiscoverytoo.JoniRegex

/**
 * Form-Part that simulates http://logstash.net/docs/1.1.9/filters/multiline .
 * We do only support what=>previous until someone asks for something different.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 26.02.13
 */
trait MultlineForm extends WebForm {

  /** If non empty, we will put the loglines through a
    * http://logstash.net/docs/1.1.9/filters/multiline filter */
  val multlineRegex = InputText("multline")

  private val negatekey = "negate"

  /** Whether to negate the multlineRegex: if false we will append
    * lines that do <em>not</em> match the filter, else we will append
    * lines that do match the filter. */
  val multlineNegate = InputMultipleChoice("multlinenegate")

  def multlinePart(): NodeSeq =
    multlineRegex.inputText(80) ++ multlineRegex.label("Multline Regex") ++
      multlineNegate.checkboxes(Map(negatekey -> "negate"))

  private def continuationLine(line: String) = {
    val ismatched = new JoniRegex(multlineRegex.value.get).findIn(line).isDefined
    if (multlineNegate.values.contains(negatekey)) !ismatched else ismatched
  }

  def multlineFilter[S <: Seq[String]](lines: S): Seq[String] = {
    if (multlineRegex.value.isEmpty || lines.isEmpty) return lines
    val builder = lines.repr.companion.newBuilder[String]
    var combinedmessage: String = null
    for (line <- lines) {
      if (continuationLine(line))
        if (null == combinedmessage) combinedmessage = line
        else combinedmessage += line
      else {
        if (null != combinedmessage) builder += combinedmessage
        combinedmessage = line
      }
    }
    if (!combinedmessage.isEmpty) combinedmessage
    return builder.result()
  }

}

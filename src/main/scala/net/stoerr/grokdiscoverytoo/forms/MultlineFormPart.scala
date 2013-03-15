package net.stoerr.grokdiscoverytoo.forms

import net.stoerr.grokdiscoverytoo.webframework.WebForm
import xml.NodeSeq
import net.stoerr.grokdiscoverytoo.JoniRegex

/**
 * Form-Part that simulates http://logstash.net/docs/1.1.9/filters/multiline .
 * We do only support what=>previous until someone asks for something different.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 26.02.13
 */
trait MultlineFormPart extends WebForm {

  /** If non empty, we will put the loglines through a
    * http://logstash.net/docs/1.1.9/filters/multiline filter */
  val multlineRegex = InputText("multline")

  private val negatekey = "negate"

  /** Whether to negate the multlineRegex: if false we will append
    * lines that do <em>not</em> match the filter, else we will append
    * lines that do match the filter. */
  val multlineNegate = InputMultipleChoice("multlinenegate")

  def multlineEntry: NodeSeq =
    multlineRegex.inputText("Multline Regex", 80) ++
      multlineNegate.checkboxes(Map(negatekey -> <span>"negate"</span>))

  def multlinehiddenfields: NodeSeq = multlineRegex.hiddenField ++ multlineNegate.hiddenField

  private def continuationLine(line: String) = {
    val ismatched = new JoniRegex(multlineRegex.value.get).findIn(line).isDefined
    if (multlineNegate.values.contains(negatekey)) !ismatched else ismatched
  }

  def multlineFilter(lines: Seq[String]): Seq[String] = {
    if (multlineRegex.value.isEmpty || multlineRegex.value.get.isEmpty || lines.isEmpty) return lines
    val lineswithmatch: Seq[(Boolean, String)] = lines.map(l => (continuationLine(l), l))
    /** Partition in groups where each group starts with an item where _1 is true. */
    def group(currentgroup: List[String], list: List[(Boolean, String)]): List[List[String]] = list match {
      case (false, l) :: Nil if currentgroup.isEmpty => List(List(l))
      case (false, l) :: Nil => List(currentgroup, List(l))
      case (true, l) :: Nil => List(currentgroup ++ List(l))
      case (false, l) :: rest if currentgroup.isEmpty => group(List(l), rest)
      case (false, l) :: rest => currentgroup :: group(List(l), rest)
      case (true, l) :: rest => group(currentgroup ++ List(l), rest)
      case _ => List.empty
    }
    val linesgrouped = group(List(), lineswithmatch.toList)
    linesgrouped.map(_.reduce(_ + "\n" + _))
  }

}

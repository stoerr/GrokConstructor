package net.stoerr.grokdiscoverytoo.webframework

import javax.servlet.http.HttpServletRequest
import xml.{Text, NodeSeq, Elem}

/**
 * Represents the data needed for a HTML form. Is used both to
 * generate the form elements and to retrieve the data from the
 * request after submitting the form.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 16.02.13
 */
trait WebForm extends TableMaker {

  val request: HttpServletRequest

  trait WebFormElement {
    val name: String
  }

  /** An input that has (at most) a single value, like a text field, a set of radio buttons, a drop down list. */
  case class InputText(name: String) extends WebFormElement {
    var value: Option[String] = Option(request.getParameter(name)).filterNot(_.isEmpty)

    def valueSplitToLines: Option[Array[String]] = value.map(_.split("\r?\n"))

    def inputText(label: String, cols: Int, enabled: Boolean = true): Elem =
      <div class="ym-fbox-text">
        <label for={name}>
          {new Text(label)}
        </label> <input
        type="text" name={name} id={name} size={cols.toString} value={value.orNull} disabled={if (enabled) null else "disabled"}/>
      </div>

    def inputTextArea(label: String, rows: Int, cols: Int): Elem =
      <div class="ym-fbox-text">
        <label for={name}>
          {new Text(label)}
        </label>{// we add the child explicitly since we must not include any additional whitespace:
          <textarea name={name} id={name} cols={cols.toString} rows={rows.toString}/>.copy(child = new Text(value.getOrElse("")))}
      </div>

    def hiddenField: Elem = <input type="hidden" name={name} id={name} value={value.orNull}/>

    def radiobutton(value: String, description: NodeSeq): NodeSeq = <div>
      <input type="radio" name={name} value={value}
             id={name}/> <label for={name}>
        {description}
      </label>
    </div>

  }

  /** An input that can have several values at once. That is, a set of checkboxes. */
  case class InputMultipleChoice(name: String, keysToText: Map[String, NodeSeq]) extends WebFormElement {
    var values: List[String] = Option(request.getParameterValues(name)).getOrElse(Array()).toList

    def checkboxes: Elem = <div class="ym-fbox-check">
      {for ((key, description) <- keysToText.toList) yield
          <input type="checkbox" checked={if (values.contains(key)) "checked" else null} name={name} id={name + "-" + key} value={key}/> ++
          <label for={name + "-" + key}>
            {description}
          </label>}
    </div>

    def hiddenField: NodeSeq = values.map(value => <input type="hidden" name={name} id={name} value={value}/>)
  }

}

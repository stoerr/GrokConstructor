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
trait WebForm {

  val request: HttpServletRequest

  trait WebFormElement {
    val name: String

    def label(text: String): Elem = label(new Text(text))

    def label(text: NodeSeq): Elem = <label for={name}>
      {text}
    </label>
  }

  /** An input that has (at most) a single value, like a text field, a set of radio buttons, a drop down list. */
  case class InputText(name: String) extends WebFormElement {
    var value: Option[String] = Option(request.getParameter(name))

    def valueSplitToLines: Option[Array[String]] = value.map(_.split("\r?\n"))

    def inputText(cols: Int, enabled: Boolean = true): Elem =
        <input type="text" name={name} id={name} value={value.orNull} size={cols.toString} disabled={if (enabled) null else "disabled"} />

    def inputTextArea(rows: Int, cols: Int): Elem =
    // we add the child explicitly since we must not include any additional whitespace
      (<textarea rows={rows.toString} cols={cols.toString} name={name}/>).copy(child = new Text(value.getOrElse("")))

    def hiddenField: Elem = <input type="hidden" name={name} id={name} value={value.orNull}/>

    def radiobutton(value: String, description: NodeSeq) = <input type="radio" name={name} id={name} value={value}/> ++ label(description)
  }

  /** An input that can have several values at once. That is, a set of checkboxes. */
  case class InputMultipleChoice(name: String) extends WebFormElement {
    var values: List[String] = Option(request.getParameterValues(name)).getOrElse(Array()).toList

    def checkboxes(keysToText: Map[String, NodeSeq]): Elem = <span>
      {for ((key, description) <- keysToText.toList) yield
          <input type="checkbox" checked={if (values.contains(key)) "checked" else null} name={name} id={name + "-" + key} value={key}/> ++
          <label for={name + "-" + key}>
            {description}
          </label>}
    </span>

    def hiddenField: NodeSeq = values.map(value => <input type="hidden" name={name} id={name} value={value}/>)
  }

}

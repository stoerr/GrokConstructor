package net.stoerr.grokdiscoverytoo.webframe

import javax.servlet.http.HttpServletRequest
import xml.Elem

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

    def label(text: String) = <label for={name}>
      {text}
    </label>
  }

  case class InputText(name: String) extends WebFormElement {
    var value: String = request.getParameter(name)

    def inputText = <input type="text" name={name} id={name} value={value}/>

    def inputTextArea(rows: Int, cols: Int, disabled: Boolean = false) =
        <textarea rows={rows} cols={cols} name={name} value={value} disabled={disabled}/>
  }

  case class InputMultipleChoice(name: String) extends WebFormElement {
    var values: Array[String] = request.getParameterValues(name)

    def checkboxes(values: Map[String, String]): List[Elem] =
      for ((key, description) <- values.toList) yield <span>
        <input type="checkbox" checked={if (values.contains(key)) "checked" else null} name={name} id={name + "-" + key} value={key}/>
        <label for={name + "-" + key}>
          {description}
        </label>
      </span>
  }

}

package net.stoerr.grokconstructor.webframework

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

    def valueSplitToLines: Array[String] = value.map(_.split("\r?\n")).getOrElse(Array())

    def inputText(label: String, cols: Int, rows: Int = 6, enabled: Boolean = true): Elem =
      inputTextArea(label, cols, rows, enabled)

    def inputTextArea(label: String, cols: Int, rows: Int = 6, enabled: Boolean = true): Elem =
      <div class="ym-fbox-text">
        <label for={name}>
          {new Text(label)}
        </label>{// we add the child explicitly since we must not include any additional whitespace:
          <textarea name={name} id={name} cols={cols.toString} rows={rows.toString}
                    readonly={if (enabled) null else "readonly"}/>.copy(child = new Text(value.getOrElse("")))}
      </div>

    def hiddenField: Elem = <input type="hidden" name={name} id={name} value={value.getOrElse("")}/>

    def radiobutton(value: String, description: NodeSeq, title:String = null): NodeSeq = <div title={title}>
      <input type="radio" name={name} value={value}
             id={"radio" + name.hashCode + value.hashCode}/> <label for={"radio" + name.hashCode + value.hashCode}>
        {description}
      </label>
    </div>

  }

  /** An input that can have several values at once. That is, a set of checkboxes. */
  case class InputMultipleChoice(name: String, keysToText: Map[String, NodeSeq], defaultKeys: Seq[String]) extends WebFormElement {
    var values: List[String] = Option(request.getParameterValues(name)).map(_.toList).getOrElse(defaultKeys.toList)

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

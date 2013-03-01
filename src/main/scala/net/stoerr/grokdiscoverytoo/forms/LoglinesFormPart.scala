package net.stoerr.grokdiscoverytoo.forms

import net.stoerr.grokdiscoverytoo.webframework.WebForm
import net.stoerr.grokdiscoverytoo.webframework.TableMaker._

/**
 * Form for input of some loglines.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 01.03.13
 */
trait LoglinesFormPart extends WebForm {

  val loglines = InputText("loglines")

  def loglinesEntry = row(loglines.label("Some log lines you want to match. Choose diversity.")) ++
    row(loglines.inputTextArea(10, 180))


}

package net.stoerr.grokdiscoverytoo.forms

import net.stoerr.grokdiscoverytoo.webframework.WebForm

/**
 * Form for input of some loglines.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 01.03.13
 */
trait LoglinesFormPart extends WebForm {

  val loglines = InputText("loglines")

  def loglinesEntry = loglines.inputTextArea("Some log lines you want to match. Choose lines that are as diverse as possible.", 20, 180)

}

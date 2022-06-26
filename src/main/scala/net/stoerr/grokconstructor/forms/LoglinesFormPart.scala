package net.stoerr.grokconstructor.forms

import net.stoerr.grokconstructor.webframework.WebForm

/**
 * Form for input of some loglines.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 01.03.13
 */
trait LoglinesFormPart extends WebForm {

  val loglines = InputText("loglines")

  def loglinesEntry = loglines.inputTextArea("Some log lines you want to match. PLEASE NOTE: For the construction algorithms you should use SEVERAL LINES that should match the pattern, and choose lines that are as diverse as possible. That reduces the search space. The more, the better (within reasonable limits, of course).", 180, 20)

}

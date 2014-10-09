package net.stoerr.grokconstructor.matcher

import javax.servlet.http.HttpServletRequest

import net.stoerr.grokconstructor.forms.{GrokPatternFormPart, LoglinesFormPart, MultilineFormPart}
import net.stoerr.grokconstructor.webframework.WebForm

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
case class MatcherForm(request: HttpServletRequest) extends WebForm with GrokPatternFormPart with MultilineFormPart with LoglinesFormPart {

  val pattern = InputText("pattern")

  def patternEntry = pattern.inputText("The pattern that should match all logfile lines:", 180)

}

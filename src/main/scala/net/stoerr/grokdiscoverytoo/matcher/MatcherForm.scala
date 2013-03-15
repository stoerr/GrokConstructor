package net.stoerr.grokdiscoverytoo.matcher

import net.stoerr.grokdiscoverytoo.webframework.WebForm
import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.forms.{MultlineFormPart, LoglinesFormPart, GrokPatternFormPart}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
case class MatcherForm(request: HttpServletRequest) extends WebForm with GrokPatternFormPart with MultlineFormPart with LoglinesFormPart {

  val pattern = InputText("pattern")

  def patternEntry = pattern.inputText("This pattern that should match all logfile lines:", 180)

}

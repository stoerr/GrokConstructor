package net.stoerr.grokdiscoverytoo.matcher

import net.stoerr.grokdiscoverytoo.webframe.WebForm
import javax.servlet.http.HttpServletRequest

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
case class MatcherForm(val request: HttpServletRequest) extends WebForm with GrokPatternForm with MultlineForm {

  val loglines = InputText("loglines")
  val pattern = InputText("pattern")

}

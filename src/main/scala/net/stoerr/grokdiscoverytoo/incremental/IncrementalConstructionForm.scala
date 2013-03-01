package net.stoerr.grokdiscoverytoo.incremental

import net.stoerr.grokdiscoverytoo.forms.{GrokPatternFormPart, MultlineFormPart, LoglinesFormPart}
import net.stoerr.grokdiscoverytoo.webframework.WebForm
import javax.servlet.http.HttpServletRequest

/**
 * Form for the entry of the basic data for the incremental finding of grok expressionsd
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 01.03.13
 */
case class IncrementalConstructionForm(val request: HttpServletRequest) extends WebForm with GrokPatternFormPart with MultlineFormPart with LoglinesFormPart {

}

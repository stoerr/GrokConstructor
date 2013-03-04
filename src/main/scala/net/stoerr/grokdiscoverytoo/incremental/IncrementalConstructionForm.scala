package net.stoerr.grokdiscoverytoo.incremental

import net.stoerr.grokdiscoverytoo.forms.{GrokPatternFormPart, MultlineFormPart, LoglinesFormPart}
import net.stoerr.grokdiscoverytoo.webframework.WebForm
import javax.servlet.http.HttpServletRequest

/**
 * Form for the entry of the basic data for the incremental finding of grok expressionsd
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 01.03.13
 */
case class IncrementalConstructionForm(request: HttpServletRequest) extends WebForm with GrokPatternFormPart with MultlineFormPart with LoglinesFormPart {

  /** Contains the part of the regular expression that is constructed so far. Starts with \A and matches all loglines. */
  val constructedRegex = InputText("constructedRegex")

  /** The next part of the regular expression. */
  val nextPart = InputText("nextPart")

}

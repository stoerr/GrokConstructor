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

  def patternEntry = pattern.inputText("The pattern that should match all logfile lines." +
    "(Please keep in mind that the whole log line / message is searched for this pattern; if you want this to match the " +
    "whole line, enclose it in ^ $ or \\A \\Z. This speeds up the search - especially if the pattern is not found.)", 180)

}

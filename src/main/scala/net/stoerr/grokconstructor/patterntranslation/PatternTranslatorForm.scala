package net.stoerr.grokconstructor.patterntranslation

import javax.servlet.http.HttpServletRequest

import net.stoerr.grokconstructor.forms.{GrokPatternFormPart, LoglinesFormPart, MultilineFormPart}
import net.stoerr.grokconstructor.webframework.WebForm

/**
  * Created by hps on 14.04.2016.
  */
case class PatternTranslatorForm(request: HttpServletRequest) extends WebForm {

  val format = InputText("format")

  def patternEntry = format.inputText("The log4j format (e.g. %d{ISO8601} %-14.14c{1}- %m%n) for which you want to create a grok pattern:", 180, 1)

  /** Used for output only. */
  val result = InputText("result")

}

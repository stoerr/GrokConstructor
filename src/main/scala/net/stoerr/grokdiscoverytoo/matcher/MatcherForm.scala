package net.stoerr.grokdiscoverytoo.matcher

import net.stoerr.grokdiscoverytoo.webframe.WebForm
import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.webframe.TableMaker._

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
case class MatcherForm(val request: HttpServletRequest) extends WebForm with GrokPatternForm with MultlineForm {

  val loglines = InputText("loglines")

  def loglinesEntry = row(loglines.label("Some log lines you want to match. Choose diversity.")) ++
    row(loglines.inputTextArea(10, 180))

  val pattern = InputText("pattern")

  def patternEntry = row(pattern.label("This pattern that should match all logfile lines:")) ++
    row(pattern.inputText(180))

}

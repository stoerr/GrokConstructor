package net.stoerr.grokdiscoverytoo.forms

import net.stoerr.grokdiscoverytoo.webframework.{TableMaker, WebForm}
import net.stoerr.grokdiscoverytoo.GrokPatternLibrary

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
trait GrokPatternFormPart extends WebForm {

  val groklibs = InputMultipleChoice("groklibs")

  val extralibs = InputMultipleChoice("grokextralibs")

  def grokpatternEntry = TableMaker.row(<span>
    Grok Patterns from
    <a href="http://logstash.net/">logstash</a>
    v.1.19 :
    {groklibs.checkboxes(GrokPatternLibrary.grokpatternKeys)}
    , and some
    {extralibs.checkboxes(GrokPatternLibrary.extrapatternKeys)}
    from me
  </span>)

}

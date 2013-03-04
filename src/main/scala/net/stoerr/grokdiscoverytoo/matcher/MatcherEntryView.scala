package net.stoerr.grokdiscoverytoo.matcher

import net.stoerr.grokdiscoverytoo.webframework.WebView
import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.{RandomTryLibrary, JoniRegex, GrokPatternLibrary}
import xml.NodeBuffer
import scala.collection.immutable.NumericRange
import net.stoerr.grokdiscoverytoo.webframework.TableMaker._

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
class MatcherEntryView(val request: HttpServletRequest) extends WebView {
  override val title: String = "Test grok patterns"
  override val action = MatcherEntryView.path

  val form = MatcherForm(request)

  override def doforward: Option[Either[String, WebView]] = if (null == request.getParameter("randomize")) None
  else Some(Left(MatcherEntryView.path + "?example=" + RandomTryLibrary.randomExampleNumber()))

  if (null != request.getParameter("example")) {
    val trial = RandomTryLibrary.example(request.getParameter("example").toInt)
    form.loglines.value = Some(trial.loglines)
    form.pattern.value = Some(trial.pattern)
    form.multlineRegex.value = trial.multline
    form.multlineNegate.values = List(form.multlineNegate.name)
    form.groklibs.values = List("grok-patterns")
  }

  private def ifNotEmpty[A](cond: String, value: A): Option[A] = if (null != cond && !cond.isEmpty) Some(value) else None

  def showResult(pat: String): NodeBuffer = {
    val patternGrokked = GrokPatternLibrary.replacePatterns(pat, form.grokPatternLibrary)
    val regex = new JoniRegex(patternGrokked)
    val lines: Seq[String] = form.multlineFilter(form.loglines.valueSplitToLines.get)
      <hr/>
      <table border="1">
        {for (line <- lines) yield {
        rowheader2(line) ++ {
          regex.findIn(line) match {
            case None =>
              val (jmatch, subregex) = longestMatchOfRegexPrefix(pat, line)
              row2(warn("NOT MATCHED")) ++
                row2("Longest prefix that matches", subregex) ++ {
                for ((name, nameResult) <- jmatch.namedgroups) yield row2(name, nameResult)
              } ++ ifNotEmpty(jmatch.before, row2("before match:", jmatch.before)) ++
                ifNotEmpty(jmatch.after, row2("after match: ", jmatch.after))
            case Some(jmatch) =>
              row2("MATCHED") ++ {
                for ((name, nameResult) <- jmatch.namedgroups) yield row2(name, nameResult)
              } ++ ifNotEmpty(jmatch.before, row2("before match:", jmatch.before)) ++
                ifNotEmpty(jmatch.after, row2("after match: ", jmatch.after))
          }
        }
      }}
      </table>
  }

  private def longestMatchOfRegexPrefix(pattern: String, line: String): (JoniRegex#JoniMatch, String) = {
    val found: (Option[JoniRegex#JoniMatch], String) = NumericRange.inclusive(pattern.length - 1, 0, -1).toIterator
      .map(pattern.substring(0, _))
      .map(safefind(_, line))
      .find(_._1.isDefined).get
    (found._1.get, found._2)
  }

  private def safefind(regex: String, line: String): (Option[JoniRegex#JoniMatch], String) =
    try {
      val regexGrokked = GrokPatternLibrary.replacePatterns(regex, form.grokPatternLibrary)
      (new JoniRegex(regexGrokked).findIn(line), regex)
    } catch {
      case _: Exception => (None, regex)
    }

  override def inputform =
    row(<span>Please enter some loglines for which you want to check a grok pattern and then press
      <input type="submit" value="Go!"/>  You can also just
      <input type="submit" name="randomize" value="try it with a random example."/>
    </span>) ++
      form.loglinesEntry ++
      form.patternEntry ++
      form.grokpatternEntry ++
      form.multlineEntry

  override def result = form.pattern.value.map(showResult(_)).getOrElse(<span/>)

}

object MatcherEntryView {
  val path = "/web/match"
}

package net.stoerr.grokconstructor.matcher

import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

import net.stoerr.grokconstructor.webframework.{WebView, WebViewWithHeaderAndSidebox}
import net.stoerr.grokconstructor.{GrokPatternLibrary, GrokPatternNameUnknownException, JoniRegex, RandomTryLibrary}
import org.joni.exception.SyntaxException

import scala.collection.immutable.NumericRange
import scala.collection.mutable
import scala.xml.NodeSeq

/**
  * View that allows checking for matches of grok regular expressions in logfile lines.
  * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
  * @since 17.02.13
  */
class MatcherEntryView(val request: HttpServletRequest) extends WebViewWithHeaderAndSidebox {
  private val logger = Logger.getLogger("MatcherEntryView")

  override val title: String = "Test grok patterns"
  val form = MatcherForm(request)

  override def action = MatcherEntryView.path + "#result"

  override def doforward: Option[Either[String, WebView]] = if (null == request.getParameter("randomize")) None
  else Some(Left(fullpath(MatcherEntryView.path) + "?example=" + RandomTryLibrary.randomExampleNumber()))

  override def maintext: NodeSeq = <p>This tries to parse a set of given logfile lines with a given
    <a href="http://logstash.net/docs/latest/filters/grok">grok regular expression</a>
    (based on
    <a href="/RegularExpressionSyntax.txt">Oniguruma regular expressions</a>
    )
    and prints
    the matches for named patterns for each log line. You can also apply a
    <a href="http://logstash.net/docs/latest/filters/multiline">multiline filter</a>
    first.</p> ++
    <p>Please enter some loglines for which you want to check a grok pattern,
      the grok expression that should match these, mark the pattern libraries you draw your patterns from and then press
    </p> ++ submit("Go!")

  override def sidebox: NodeSeq = <p>
    You can also just try this out with a</p> ++ buttonanchor(MatcherEntryView.path + "?randomize", "random example")

  override def formparts: NodeSeq = form.loglinesEntry ++
    form.patternEntry ++
    form.grokpatternEntry ++
    form.multilineEntry

  if (null != request.getParameter("example")) {
    val trial = RandomTryLibrary.example(request.getParameter("example").toInt)
    form.loglines.value = Some(trial.loglines)
    form.pattern.value = Some(trial.pattern)
    form.multilineRegex.value = trial.multiline
    form.multilineNegate.values = List()
    form.groklibs.values = List("grok-patterns", "java")
  }

  override def result = form.pattern.value.map(showResult).getOrElse(<span/>)

  def showResult(pat: String): NodeSeq = {
    try {
      val patternGrokked = GrokPatternLibrary.replacePatterns(pat, form.grokPatternLibrary)
      val regex = new JoniRegex(patternGrokked)
      lazy val regexPrefixes = compilablePrefixes(pat)
      try {
        val lines: Seq[String] = form.multilineFilter(form.loglines.valueSplitToLines)
        return <hr/> ++ <table class="bordertable narrow">
          {for (line <- lines) yield {
            rowheader2(line) ++ {
              regex.findIn(line) match {
                case None =>
                  val (jmatch, subregex) = longestMatchOfRegexPrefix(regexPrefixes, line)
                  row2(warn("NOT MATCHED")) ++
                    row2("Longest prefix that matches", subregex) ++ {
                    for ((name, nameResult) <- jmatch.namedgroups) yield row2(name, visibleWhitespaces(nameResult))
                  } ++ ifNotEmpty(jmatch.before, row2("before match:", jmatch.before)) ++
                    ifNotEmpty(jmatch.after, row2("after match: ", jmatch.after))
                case Some(jmatch) =>
                  row2(<b>MATCHED</b>) ++ {
                    for ((name, nameResult) <- jmatch.namedgroups) yield row2(name, visibleWhitespaces(nameResult))
                  } ++ ifNotEmpty(jmatch.before, row2("before match:", jmatch.before)) ++
                    ifNotEmpty(jmatch.after, row2("after match: ", jmatch.after))
              }
            }
          }}
        </table>
      } catch {
        case multilineSyntaxException: SyntaxException =>
          return <hr/> ++ <p class="box error">Syntaxfehler in the pattern for the multiline filter
            {form.multilineRegex.value.get}
            :
            <br/>{multilineSyntaxException.getMessage}
          </p>
      }
    } catch {
      case patternSyntaxException: SyntaxException =>
        return <hr/> ++ <p class="box error">Syntaxfehler in the given pattern
          {pat}
          :
          <br/>{patternSyntaxException.getMessage}
        </p>
      case patternUnknownException: GrokPatternNameUnknownException =>
        return <hr/> ++ <p class="box error">This grok pattern has an unknown name
          {patternUnknownException.patternname}
          :
          {patternUnknownException.pattern}
        </p>
    }
  }

  private def ifNotEmpty[A](cond: String, value: A): Option[A] = if (null != cond && !cond.isEmpty) Some(value) else None

  private def longestMatchOfRegexPrefix(patterns: Stream[(JoniRegex, String)], line: String): (JoniRegex#JoniMatch, String) =
    patterns.map(t => (t._1.findIn(line), t._2)).filter(_._1.isDefined).map(t => (t._1.get, t._2)).head

  private def compilablePrefixes(pat: String): Stream[(JoniRegex, String)] = {
    val prefixes = NumericRange.inclusive(pat.length - 1, 0, -1).toStream.map(pat.substring(0, _))
    prefixes.flatMap { regex =>
      try {
        Some((new JoniRegex(GrokPatternLibrary.replacePatterns(regex, form.grokPatternLibrary)), regex))
      } catch {
        case e: Exception =>
          None
      }
    }
  }

}

object MatcherEntryView {
  val path = "/match"
}

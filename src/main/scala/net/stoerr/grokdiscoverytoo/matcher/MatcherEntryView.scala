package net.stoerr.grokdiscoverytoo.matcher

import net.stoerr.grokdiscoverytoo.webframe.WebView
import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.{JoniRegex, GrokPatternLibrary}
import xml.NodeBuffer
import scala.collection.immutable.NumericRange
import net.stoerr.grokdiscoverytoo.webframe.TableMaker._

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
class MatcherEntryView(val request: HttpServletRequest) extends WebView {
  def title: String = "Test grok patterns"

  val form = MatcherForm(request)

  private def ifNotEmpty[A](cond: String, value: A): Option[A] = if (null != cond && !cond.isEmpty) Some(value) else None

  lazy val groklib = GrokPatternLibrary.mergePatternLibraries(form.groklibs.values, None)

  def showResult(pat: String): NodeBuffer = {
    val patternGrokked = GrokPatternLibrary.replacePatterns(pat, groklib)
    val regex = new JoniRegex(patternGrokked)
    val lines: Seq[String] = form.multlineFilter(form.loglines.valueSplitToLines.get)
      <hr/>
      <table border="1">
        {for (line <- lines) yield {
        rowheader2(line) ++ {
          regex.findIn(line) match {
            case None =>
              val jmatch = longestMatchOfRegexPrefix(patternGrokked, line)
              row2(warn("NOT MATCHED")) ++
                row2("Longest prefix that matches", jmatch.regex) ++ {
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

  def longestMatchOfRegexPrefix(pattern: String, line: String) =
    NumericRange.inclusive(pattern.length - 1, 0, -1).toIterator
      .map(pattern.substring(0, _))
      .map(new JoniRegex(_).findIn(line))
      .find(_.isDefined).get.get

  def body: AnyRef = <body>
    <h1>Test grok patterns</h1>
    <form action="/web/match" method="post">
      <table>
        {row(<span>Please enter some loglines and then press
        <input type="submit" value="Go!"/>
      </span>) ++
        form.loglinesEntry ++
        form.patternEntry ++
        form.grokpatternEntry ++
        form.multlineEntry}
      </table>
    </form>{form.pattern.value.map(showResult(_)).getOrElse(<span/>)}
  </body>
}

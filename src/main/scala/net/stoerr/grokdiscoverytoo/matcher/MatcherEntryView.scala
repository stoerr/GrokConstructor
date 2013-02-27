package net.stoerr.grokdiscoverytoo.matcher

import net.stoerr.grokdiscoverytoo.webframe.WebView
import javax.servlet.http.HttpServletRequest
import net.stoerr.grokdiscoverytoo.{JoniRegex, GrokPatternLibrary}
import xml.NodeBuffer

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
class MatcherEntryView(val request: HttpServletRequest) extends WebView {
  def title: String = "Test grok patterns"

  val form = MatcherForm(request)

  def showResult(pat: String): NodeBuffer = {
    val regex = new JoniRegex(pat)
    val lines: Seq[String] = form.multlineFilter(form.loglines.valueSplitToLines.get)
      <hr/>
      <table border="1">
        {for (line <- lines) yield {
        <tr>
          <th colspan="2">
            {line}
          </th>
        </tr> ++ {
          regex.findIn(line) match {
            case None =>
              <tr>
                <td>NOT MATCHED</td>
              </tr>
            case Some(jmatch) =>
              <tr>
                <td>before match:
                </td> <td>
                {jmatch.before}
              </td>
              </tr> ++ {
                for ((name, nameResult) <- jmatch.namedgroups) yield {
                  <tr>
                    <td>
                      {name}
                    </td> <td>
                    {nameResult}
                  </td>
                  </tr>
                }
              } ++
                <tr>
                  <td>after match:
                  </td> <td>
                  {jmatch.after}
                </td>
                </tr>
          }
        }
      }}
      </table>
  }

  def body: AnyRef = <body>
    <h1>Test grok patterns</h1>
    <form action="/web/match" method="post">
      <table>
        <tr>
          <td>Please enter some loglines and then press
            <input type="submit" value="Go!"/>
          </td>
        </tr>
        <tr>
          <td>
            {form.loglines.label("Some log lines you want to match. Choose diversity.")}
          </td>
        </tr>
        <tr>
          <td>
            {form.loglines.inputTextArea(10, 180)}
          </td>
        </tr>
        <tr>
          <td>Grok Patterns from
            <a href="http://logstash.net/">logstash</a>
            v.1.19 :
            {form.groklibs.checkboxes(GrokPatternLibrary.grokpatternKeys)}
            and some
            {form.extralibs.checkboxes(GrokPatternLibrary.extrapatternKeys)}
            from me
          </td>
        </tr>
        <tr>
          <td>
            {form.multlinePart()}
          </td>
        </tr>
        <tr>
          <td>
            {form.pattern.label("The pattern that should match all of them")}
          </td>
        </tr>
        <tr>
          <td>
            {form.pattern.inputText(180)}
          </td>
        </tr>
      </table>
    </form>{form.pattern.value.map(showResult(_)).getOrElse(<span/>)}
  </body>
}

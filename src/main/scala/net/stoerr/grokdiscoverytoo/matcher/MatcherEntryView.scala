package net.stoerr.grokdiscoverytoo.matcher

import net.stoerr.grokdiscoverytoo.webframe.WebView
import javax.servlet.http.HttpServletRequest

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
class MatcherEntryView(val request: HttpServletRequest) extends WebView {
  def title: String = "Test grok patterns"

  val form = MatcherForm(request)

  def body: AnyRef = <body>
    <h1>Test grok patterns</h1>

  </body>
}

package net.stoerr.grokdiscoverytoo.webframe

import javax.servlet.http.HttpServletRequest

/**
 * Basic trait for a web controller.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 15.02.13
 */
trait WebController {

  /** Does some processing and returns the view to present to the user. */
  def process(req: HttpServletRequest): WebView
}

/** Pseudo-Controller that does nothing but just forwards the view. */
class EmptyController(view: WebView) extends WebController {
  def process(req: HttpServletRequest) = view
}

package net.stoerr.grokdiscoverytoo.webframe

/**
 * Basis for a View-Class that displays a page.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 15.02.13
 */
trait WebView {

  def title: String

  def body: AnyRef

}

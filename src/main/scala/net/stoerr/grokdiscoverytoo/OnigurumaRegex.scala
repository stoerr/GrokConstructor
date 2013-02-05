package net.stoerr.grokdiscoverytoo

import org.joni.Regex

/**
 * Wrapper for org.joni.Regex since there seems to be absolutely no documentation. ARGH!!!
 * Unfortunately I don't know any library that implements the same regex syntax.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 05.02.13
 */
case class OnigurumaRegex(regex: String) {

  val compiledRegex = new Regex(regex)

}

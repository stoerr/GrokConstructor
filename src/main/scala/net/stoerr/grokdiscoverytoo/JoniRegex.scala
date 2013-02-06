package net.stoerr.grokdiscoverytoo

import org.joni.Regex
import org.jcodings.specific.UTF8Encoding

/**
 * Very rudimentary just-what-I-need wrapper for org.joni.Regex. I am deeply sorry to use something
 * that its author deems as unworthy of any documentation, but I need something
 * that groks the regex syntax http://www.geocities.jp/kosako3/oniguruma/doc/RE.txt
 * that is used by grok.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 05.02.13
 */
case class JoniRegex(regex: String) {

  private val compiledRegex = new Regex(regex, UTF8Encoding.INSTANCE)

  def matchStartOf(str: String): Option[StartMatch] = {
    val bytes = str.getBytes("UTF-8")
    val matcher = compiledRegex.matcher(bytes)
    val matchedLength = matcher.`match`(0, bytes.length, 0)
    if (0 > matchedLength) None
    else Some(StartMatch(matchedLength, new String(bytes, 0, matchedLength, "UTF-8"), new String(bytes, matchedLength, bytes.length - matchedLength, "UTF-8")))
  }

}

/** Represents a match at the beginning of a string.
  * @param length length of the match
  * @param matched the part of the string that was matched by the regex
  * @param rest the part of the string after the match */
case class StartMatch(length: Int, matched: String, rest: String)


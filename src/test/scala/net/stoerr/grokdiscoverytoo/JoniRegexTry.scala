package net.stoerr.grokdiscoverytoo

import org.joni.Regex

/**
 * We try to find out what that library actually does.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 05.02.13
 */
object JoniRegexTry extends App {

  val r1 = new Regex("a.c")

  def startsWith(s: String, r: Regex) = {
    val bytes = s.getBytes
    val matcher = r.matcher(bytes)
    matcher.`match`(0, bytes.length, 0)
  }

  println(startsWith("abc", r1))
  println(startsWith("abcd", r1))
  println(startsWith("bcd", r1))
  println(startsWith("cabcd", r1))

  val rn = JoniRegex("a.c")

  println(rn.matchStartOf(""))
  println(rn.matchStartOf("abc"))
  println(rn.matchStartOf("abcd"))
  println(rn.matchStartOf("cabcd"))

  println(JoniRegex("(?<bla>blu)"))

}

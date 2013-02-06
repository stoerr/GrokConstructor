package net.stoerr.grokdiscoverytoo

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import org.joni.Regex

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 06.02.13
 */
class JoniRegexTest extends FlatSpec with ShouldMatchers {

  def startsWith(s: String, r: Regex) = {
    val bytes = s.getBytes
    val matcher = r.matcher(bytes)
    matcher.`match`(0, bytes.length, 0)
  }

  val r1 = new Regex("a.*c")

  "A org.joni.Regex" should "find regex at the start and return the matched length" in {
    startsWith("abc", r1) should equal(3)
    startsWith("abcd", r1) should equal(3)
    startsWith("abblacdxxx", r1) should equal(6)
  }

  it should "not find a regex that matches somewhere else or not at all" in {
    startsWith("bcd", r1) should equal(-1)
    startsWith("cabcd", r1) should equal(-1)
  }

  val rn = JoniRegex("a.*c")

  "JoniRegex.matchStartOf" should "return None if the regex is not found at the start" in {
    for (str <- List("cabcd", "bcd", ""))
      rn.matchStartOf(str) should equal(None)
  }

  it should "return the appropriate length, match and rest if the regex matches the start" in {
    rn.matchStartOf("abc") should equal (Some(StartMatch(length = 3, matched="abc", rest = "")))
    rn.matchStartOf("acdfsdf") should equal (Some(StartMatch(2, "ac", "dfsdf")))
  }

}

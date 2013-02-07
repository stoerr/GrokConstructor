package net.stoerr.grokdiscoverytoo

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import net.stoerr.grokdiscoverytoo.GrokDiscoveryToo.{NamedRegex, FixedString, RegexPart}

/**
 * Guess what: verifies algorithms in GrokDiscoveryToo.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 07.02.13
 */
class GrokDiscoveryTooTest extends FlatSpec with ShouldMatchers {

  "GrokDiscoveryToo.commonPrefix" should "return the common prefix of two strings" in {
    val d = new GrokDiscoveryToo(null)
    d.commonPrefix("blabber", "blubber") should equal("bl")
    d.commonPrefix("foo", "bar") should equal("")
    d.commonPrefix("", "bla") should equal("")
    d.commonPrefix("", "") should equal("")
  }

  "GrokDiscoveryToo.biggestCommonPrefix" should "return the biggest common prefix" in {
    val d = new GrokDiscoveryToo(null)
    d.biggestCommonPrefix(List("foobar", "fooling", "footoo")) should equal("foo")
    d.biggestCommonPrefix(List("foo", "bar")) should equal("")
  }

  "GrokDiscoveryToo.matchingRegexpStructures" should "suggest all pattern combinations" in {
    val regexes = Map("A" -> JoniRegex("a|b"), "B" -> JoniRegex("b|c"), "S" -> JoniRegex("\\s*"))
    val d = new GrokDiscoveryToo(regexes);
    d.matchingRegexpStructures(List("a")).toList should equal (List(List(FixedString("a"))))
    d.matchingRegexpStructures(List("a", "b")).toList should equal (List(List(NamedRegex(List("A")))))
    d.matchingRegexpStructures(List("ca", "cb")).toList should equal (List(List(FixedString("c"), NamedRegex(List("A")))))
    d.matchingRegexpStructures(List("ca ", "cb")).toList should equal (List(List(FixedString("c"), NamedRegex(List("A")), NamedRegex(List("S")))))
  }

}

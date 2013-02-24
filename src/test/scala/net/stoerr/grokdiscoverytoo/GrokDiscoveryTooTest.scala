package net.stoerr.grokdiscoverytoo

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import net.stoerr.grokdiscoverytoo.GrokDiscoveryToo.{NamedRegex, FixedString}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Guess what: verifies algorithms in GrokDiscoveryToo.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 07.02.13
 */
@RunWith(classOf[JUnitRunner])
class GrokDiscoveryTooTest extends FlatSpec with ShouldMatchers {

  "GrokDiscoveryToo.commonPrefixExceptDigitsOrLetters" should "return the common prefix of two strings" in {
    val d = new GrokDiscoveryToo(Map())
    d.commonPrefixExceptDigitsOrLetters("$#@@#$@", "$#%@#$") should equal("$#")
    d.commonPrefixExceptDigitsOrLetters("!@#", "#@!") should equal("")
    d.commonPrefixExceptDigitsOrLetters("", "$#$") should equal("")
    d.commonPrefixExceptDigitsOrLetters("", "") should equal("")
  }

  "GrokDiscoveryToo.biggestCommonPrefixExceptDigitsOrLetters" should "return the biggest common prefix" in {
    val d = new GrokDiscoveryToo(Map())
    d.biggestCommonPrefixExceptDigitsOrLetters(List("#@!%$%", "#@!&$*", "#@!*#&")) should equal("#@!")
    d.biggestCommonPrefixExceptDigitsOrLetters(List("!@#", "$#@")) should equal("")
    d.biggestCommonPrefixExceptDigitsOrLetters(List("$#@$bla")) should equal("$#@$")
  }

  "GrokDiscoveryToo.matchingRegexpStructures" should "suggest all pattern combinations" in {
    val regexes = Map("A" -> "a|b", "B" -> "b|c", "S" -> "\\s*")
    val d = new GrokDiscoveryToo(regexes)
    d.matchingRegexpStructures(List("-")).toList should equal(List(List(FixedString("-"))))
    d.matchingRegexpStructures(List("a", "b")).toList should equal(List(List(NamedRegex(List("A")))))
    d.matchingRegexpStructures(List("-a", "-b")).toList should equal(List(List(FixedString("-"), NamedRegex(List("A")))))
    d.matchingRegexpStructures(List("-a ", "-b")).toList should equal(List(List(FixedString("-"), NamedRegex(List("A")), NamedRegex(List("S")))))
    d.matchingRegexpStructures(List(" -a ", "-b")).toList should equal(List(List(NamedRegex(List("S")), FixedString("-"), NamedRegex(List("A")), NamedRegex(List("S")))))
  }

}

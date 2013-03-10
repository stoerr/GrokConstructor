package net.stoerr.grokdiscoverytoo

import automatic.AutomaticDiscoveryView
import automatic.AutomaticDiscoveryView.{NamedRegex, FixedString}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import AutomaticDiscoveryView._

/**
 * Guess what: verifies algorithms in AutomaticDiscoveryView.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 07.02.13
 */
@RunWith(classOf[JUnitRunner])
class AutomaticDiscoveryViewTest extends FlatSpec with ShouldMatchers {

  "AutomaticDiscoveryView.commonPrefixExceptDigitsOrLetters" should "return the common prefix of two strings" in {
    commonPrefixExceptDigitsOrLetters("$#@@#$@", "$#%@#$") should equal("$#")
    commonPrefixExceptDigitsOrLetters("!@#", "#@!") should equal("")
    commonPrefixExceptDigitsOrLetters("", "$#$") should equal("")
    commonPrefixExceptDigitsOrLetters("", "") should equal("")
  }

  "AutomaticDiscoveryView.biggestCommonPrefixExceptDigitsOrLetters" should "return the biggest common prefix" in {
    biggestCommonPrefixExceptDigitsOrLetters(List("#@!%$%", "#@!&$*", "#@!*#&")) should equal("#@!")
    biggestCommonPrefixExceptDigitsOrLetters(List("!@#", "$#@")) should equal("")
    biggestCommonPrefixExceptDigitsOrLetters(List("$#@$bla")) should equal("$#@$")
  }

  "AutomaticDiscoveryView.matchingRegexpStructures" should "suggest all pattern combinations" in {
    /* val regexes = Map("A" -> "a|b", "B" -> "b|c", "S" -> "\\s*")
    val d = new AutomaticDiscoveryView(regexes)
    d.matchingRegexpStructures(List("-")).toList should equal(List(List(FixedString("-"))))
    d.matchingRegexpStructures(List("a", "b")).toList should equal(List(List(NamedRegex(List("A")))))
    d.matchingRegexpStructures(List("-a", "-b")).toList should equal(List(List(FixedString("-"), NamedRegex(List("A")))))
    d.matchingRegexpStructures(List("-a ", "-b")).toList should equal(List(List(FixedString("-"), NamedRegex(List("A")), NamedRegex(List("S")))))
    d.matchingRegexpStructures(List(" -a ", "-b")).toList should equal(List(List(NamedRegex(List("S")), FixedString("-"), NamedRegex(List("A")), NamedRegex(List("S")))))
    */
  }

}

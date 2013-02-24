package net.stoerr.grokdiscoverytoo

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import io.Source
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 06.02.13
 */
@RunWith(classOf[JUnitRunner])
class GrokPatternLibraryTest extends FlatSpec with ShouldMatchers {

  "GrokPatternLibrary.readGrokPatterns" should "read a pattern file and replace patterns" in {
    val src = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("grok/grok-patterns"))
    val patterns = GrokPatternLibrary.readGrokPatterns(src.getLines())
    patterns.size should equal(66)
    patterns("HOUR") should equal("(?:2[0123]|[01][0-9])")
    patterns("ISO8601_TIMEZONE") should equal("(?:Z|[+-]%{HOUR}(?::?%{MINUTE}))")
  }

  "GrokPatternLibrary.replacePatterns" should "replace groks patterns" in {
    val grokReference = """%\{(\w+)(:\w+)?\}""".r
    grokReference.pattern.matcher("%{BU}").matches() should equal(true)
    grokReference.pattern.matcher("%{BLA:name}").matches() should equal(true)
    GrokPatternLibrary.replacePatterns("bla%{BU}bu%{BLA}hu", Map("BU" -> "XYZ", "BLA" -> "HU%{BU}HA")) should equal("bla(?:XYZ)bu(?:HU(?:XYZ)HA)hu")
    GrokPatternLibrary.replacePatterns("%{BU:foo}%{BLA:bar}", Map("BU" -> "XYZ", "BLA" -> "HU%{BU}HA")) should equal("(?<foo>XYZ)(?<bar>HU(?:XYZ)HA)")
    evaluating {
      GrokPatternLibrary.replacePatterns("%{NIX}", Map())
    } should produce[NoSuchElementException]
  }

}

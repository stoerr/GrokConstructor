package net.stoerr.grokdiscoverytoo

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import io.Source

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 06.02.13
 */
class GrokPatternReaderTest extends FlatSpec with ShouldMatchers with GrokPatternReader {

  "GrokPatternReader.replacePatterns" should "replace groks patterns" in {
    val grokReference = """%\{(\w+)(:\w+)?\}""".r
    grokReference.pattern.matcher("%{BU}").matches() should  equal(true)
    grokReference.pattern.matcher("%{BLA:name}").matches() should  equal(true)
    replacePatterns("bla%{BU}bu%{BLA:name}hu", Map("BU" -> "XYZ", "BLA" -> "HU%{BU}HA")) should equal("blaXYZbuHUXYZHAhu")
    evaluating { replacePatterns("%{NIX}", Map()) } should produce [NoSuchElementException]
  }

  "GrokPatternReader.readGrokPatterns" should "read a pattern file and replace patterns" in {
    val src = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("grok/grok-patterns"))
    val patterns = readGrokPatterns(src)
    patterns.size should equal(66)
    patterns("HOUR") should equal(JoniRegex("(?:2[0123]|[01][0-9])"))
    patterns("ISO8601_TIMEZONE") should equal(JoniRegex("(?:Z|[+-](?:2[0123]|[01][0-9])(?::?(?:[0-5][0-9])))"))
  }

}

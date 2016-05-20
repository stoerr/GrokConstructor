package net.stoerr.grokconstructor

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

import scala.io.Source

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 06.02.13
 */
@RunWith(classOf[JUnitRunner])
class GrokPatternLibraryTest extends FlatSpec with ShouldMatchers {

  "GrokPatternLibrary.readGrokPatterns" should "read a pattern file and replace patterns" in {
    val src = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("grok/grok-patterns"))
    val patterns = GrokPatternLibrary.readGrokPatterns(src.getLines())
    patterns.size should equal(76)
    patterns("HOUR") should equal("(?:2[0123]|[01]?[0-9])")
    patterns("ISO8601_TIMEZONE") should equal("(?:Z|[+-]%{HOUR}(?::?%{MINUTE}))")
    patterns.foreach{ case (k,v) =>
        val replaced = GrokPatternLibrary.replacePatterns(v, patterns)
        // println(k + "\t" + replaced)
        // make sure all patterns are actually compileable
        new JoniRegex(v).oldMatchStartOf("bla")
    }
  }

  "GrokPatternLibrary.replacePatterns" should "replace groks patterns" in {
    val grokReference = """%\{(\w+)(?::(\w+)(?::(?:int|float))?)?\}""".r
    grokReference.pattern.matcher("%{BU}").matches() should equal(true)
    grokReference.pattern.matcher("%{BLA:name}").matches() should equal(true)
    grokReference.pattern.matcher("%{BLA:name:int}").matches() should equal(true)
    grokReference.pattern.matcher("%{BLA:name:float}").matches() should equal(true)
    GrokPatternLibrary.replacePatterns("bla%{BU}bu%{BLA}hu", Map("BU" -> "XYZ", "BLA" -> "HU%{BU}HA")) should equal("bla(?:XYZ)bu(?:HU(?:XYZ)HA)hu")
    GrokPatternLibrary.replacePatterns("%{BU:foo}%{BLA:bar}", Map("BU" -> "XYZ", "BLA" -> "HU%{BU}HA")) should equal("(?<foo>XYZ)(?<bar>HU(?:XYZ)HA)")
    evaluating {
      GrokPatternLibrary.replacePatterns("%{NIX}", Map())
    } should produce[GrokPatternNameUnknownException]
  }

}

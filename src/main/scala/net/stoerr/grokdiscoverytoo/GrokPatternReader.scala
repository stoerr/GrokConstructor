package net.stoerr.grokdiscoverytoo

import util.matching.Regex

/**
 * Reads a pattern file for grok and replaces the %{...} references
 * to yield plain regexps
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 06.02.13
 */
trait GrokPatternReader {

  /** Reads the patterns from a source */
  def readGrokPatterns(src: Iterator[String]): Map[String, JoniRegex] = {
    val cleanedupLines = src.filterNot(_.trim.isEmpty).filterNot(_.startsWith("#"))
    val grokLine = "(\\w+) (.*)".r
    val grokRegexMap: Map[String, String] = cleanedupLines.map {
      case grokLine(name, grokregex) => (name -> grokregex)
    }.toMap
    grokRegexMap map {
      case (name, grokregex) => {
        (name -> JoniRegex(replacePatterns(grokregex, grokRegexMap)))
      }
    }
  }

  /** We replace patterns like %{BLA:name} with the definition of bla. This is done
    * (arbitrarily) 10 times to allow recursions but to not allow infinite loops. */
  protected def replacePatterns(grokregex: String, grokMap: Map[String, String]): String = {
    var substituted = grokregex
    val grokReference = """%\{(\w+)(:\w+)?\}""".r
    0 until 10 foreach {
      _ =>
        substituted = grokReference replaceAllIn(substituted, m => Regex.quoteReplacement(grokMap(m.group(1))))
    }
    substituted
  }

}

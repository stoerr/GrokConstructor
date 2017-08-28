package net.stoerr.grokconstructor

import java.io.InputStream

import scala.io.{Codec, Source}
import scala.util.matching.Regex

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
object GrokPatternLibrary {

  val grokPatternLibraryNames = List("aws", "bacula", "bind", "bro", "exim", "firewalls", "grok-patterns", "haproxy",
    "httpd", "java", "junos", "linux-syslog", "maven", "mcollective", "mcollective-patterns", "mongodb",
    "nagios", "postgresql", "rails", "redis", "ruby", "squid").sorted

  def mergePatternLibraries(libraries: List[String], extrapatterns: Option[String]): Map[String, String] = {
    val extrapatternlines: Iterator[String] = extrapatterns.map(Source.fromString(_).getLines()).getOrElse(Iterator())
    val grokPatternSources = for (grokfile <- libraries) yield grokSource(grokfile).getLines()
    val allPatternLines = grokPatternSources.fold(extrapatternlines)(_ ++ _)
    readGrokPatterns(allPatternLines)
  }

  def grokSource(location: String): Source = {
    if (!location.matches("^[a-z-]+$")) throw new IllegalArgumentException("Invalid fullpath " + location)
    val inputStream: InputStream = getClass.getResourceAsStream("/grok/" + location)
    if (null == inputStream) throw new IllegalArgumentException("Could not find " + location)
    Source.fromInputStream(inputStream)(Codec.UTF8)
  }

  /** Reads the patterns from a source */
  def readGrokPatterns(src: Iterator[String]): Map[String, String] = {
    val cleanedupLines = src.filterNot(_.trim.isEmpty).filterNot(_.startsWith("#"))
    val grokLine = "(\\w+) (.*)".r
    cleanedupLines.map {
      case grokLine(name, grokregex) => name -> grokregex
      case other => sys.error("Can't understand the following line in the additional grok patterns - \n" +
        "it doesn't seem to be a normal line for a grok pattern file consisting of " +
        "a key, a space and a definition. For example, this would be correct:\n# Some comment\nUSERNAME [a-zA-Z0-9._-]+\nUSER %{USERNAME}\n" +
        "\nThe troublesome line is:\n\n" + other)
    }.toMap
  }

  private val grokReference = """%\{([^}>':]+)(?::([^}>':@\[\]]+)(?::(?:int|float))?)?\}""".r

  /** We replace patterns like %{BLA:name} with the definition of bla. This is done
    * (arbitrarily) 10 times to allow recursions but to not allow infinite loops. */
  def replacePatterns(grokregex: String, grokMap: Map[String, String]): String = {
    var substituted = grokregex
    var haveReplacement = true
    0 until 10 foreach {
      _ =>
        if (haveReplacement) {
          haveReplacement = false
          substituted = grokReference replaceAllIn(substituted, {
            m => {
              haveReplacement = true
              val patternName = m.group(1)
              if (!grokMap.contains(patternName)) throw new GrokPatternNameUnknownException(patternName, m.group(0))
              "(?" + Option(m.group(2)).map(Regex.quoteReplacement).map("<" + _ + ">").getOrElse(":") +
                Regex.quoteReplacement(grokMap(patternName)) + ")"
            }
          })
        }
    }
    substituted
  }

}

class GrokPatternNameUnknownException(val patternname: String, val pattern: String) extends RuntimeException {
  override def toString: String = "Grok pattern name " + patternname + " unknown at " + pattern
}

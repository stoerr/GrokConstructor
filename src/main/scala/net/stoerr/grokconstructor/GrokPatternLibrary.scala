package net.stoerr.grokconstructor

import util.matching.Regex
import scala.io.{BufferedSource, Source}
import java.io.InputStream

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
object GrokPatternLibrary {

  val grokpatternnames = List("firewalls", "grok-patterns", "haproxy", "java", "junos", "linux-syslog", "mcollective",
    "mcollective-patterns", "mongodb", "nagios", "postgresql", "redis", "ruby")

  val extrapatternnames = List("extras")

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
    val stream: BufferedSource = Source.fromInputStream(inputStream)
    return stream
  }

  /** Reads the patterns from a source */
  def readGrokPatterns(src: Iterator[String]): Map[String, String] = {
    val cleanedupLines = src.filterNot(_.trim.isEmpty).filterNot(_.startsWith("#"))
    val grokLine = "(\\w+) (.*)".r
    cleanedupLines.map {
      case grokLine(name, grokregex) => (name -> grokregex)
    }.toMap
  }

  /** We replace patterns like %{BLA:name} with the definition of bla. This is done
    * (arbitrarily) 10 times to allow recursions but to not allow infinite loops. */
  def replacePatterns(grokregex: String, grokMap: Map[String, String]): String = {
    var substituted = grokregex
    val grokReference = """%\{(\w+)(:(\w+))?\}""".r
    0 until 10 foreach {
      _ =>
        substituted = grokReference replaceAllIn(substituted, {
          m =>
            "(?" + Option(m.group(3)).map(Regex.quoteReplacement).map("<" + _ + ">").getOrElse(":") +
              Regex.quoteReplacement(grokMap(m.group(1))) + ")"
        })
    }
    substituted
  }

}

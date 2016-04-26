package net.stoerr.grokconstructor.patterntranslation

import scala.util.matching.Regex

/**
  * Translates a log4j conversation pattern into a grok pattern for parsing the log4j output
  *
  * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
  * @since 16.02.2015
  * @see "https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html"
  */
object Log4jTranslator {

  /** Matches log4j conversion specifiers - group 1 = left justify if -, group 2 = minimum width,
    * group 3 = maximum width, group 4 = argument in case of %d etc. */
  val conversionSpecifier: Regex =
    """%(?:(-)?(\d+))?(?:\.(\d+))?([a-zA-Z])(?:\{([^}]+)\})?""".r

  def translate(conversionpattern: String): String =
    replaceMatchesAndInbetween(conversionpattern, conversionSpecifier, translateConversionSpecifier, quoteAsRegex)

  private def translateConversionSpecifier(thematch: Regex.Match): String = {
    val List(leftjust, minwidth, maxwidth, conversionchar, argument) = thematch.subgroups
    val baseRegex = conversionchar match {
      case "c" => "(?<logger>[A-Za-z0-9$_.]+)" // "%{JAVACLASS:logger}" does not work for abbreviated patterns
      case "C" => "(?<class>[A-Za-z0-9$_.]+)"
      case "F" => "%{JAVAFILE:class}"
      case "l" => "%{JAVASTACKTRACEPART:location}"
      case "L" => "%{NONNEGINT:line}"
      case "m" => "%{GREEDYDATA:message}"
      case "n" => "$" // possibly also "\\r?\\n"
      case "M" => "%{NOTSPACE:method}"
      case "p" => "%{LOGLEVEL:loglevel}"
      case "r" => "%{INT:relativetime}"
      case "t" => "%{NOTSPACE:thread}"
      case "x" => "(%{NOTSPACE:ndc})?"
      case "X" => if (null == argument) """\{(?<mdc>(?:\{[^\}]*,[^\}]*\})*)\}""" else "(%{NOTSPACE:" + argument + "})?"
      case "d" => translateDate(argument)
      case other => throw new TranslationException("Unknown conversion specifier " + other)
    }
    align(baseRegex, leftjust, minwidth, maxwidth)
  }

  private def translateDate(argument: String): String = {
    val format = argument match {
      case null | "ISO8601" => "%{TIMESTAMP_ISO8601}"
      case "ABSOLUTE" => "HH:mm:ss,SSS"
      case "DATE" => "dd MMM yyyy HH:mm:ss,SSS"
      case explicitFormat => translateExplicitDateFormat(explicitFormat)
    }
    "(?<timestamp>" + format + ")"
  }

  val dateFormatComponent = "(([a-zA-Z])\\2*)(.*)".r
  // fullcomponent, componentchar, rest
  val dateFormatLiteral = "'([^']+)(.*)".r
  // literal, rest
  val otherChar = "([^a-zA-Z])(.*)".r // char, rest

  private def translateExplicitDateFormat(dateFormat: String): String = dateFormat match {
    case null | "" => dateFormat
    case dateFormatLiteral(literal, rest) => quoteAsRegex(literal) + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "d", rest) => "%{MONTHDAY}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "y", rest) => "%{YEAR}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "Y", rest) => "%{YEAR}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "E", rest) => "%{DAY}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "a", rest) => "(AM|PM)" + translateExplicitDateFormat(rest)
    case dateFormatComponent("MMM", _, rest) => "%{MONTH}" + translateExplicitDateFormat(rest)
    case dateFormatComponent("MM", _, rest) => "%{MONTHNUM2}" + translateExplicitDateFormat(rest)
    case dateFormatComponent("EEE", _, rest) => "%{DAY}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "H", rest) => "%{HOUR}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "m", rest) => "%{MINUTE}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "s", rest) => "%{SECOND}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "S", rest) => "%{NONNEGINT}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "D", rest) => "%{NONNEGINT}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "X", rest) => "%{ISO8601_TIMEZONE}" + translateExplicitDateFormat(rest)
    case dateFormatComponent(_, "z" | "Z", rest) => "%{TZ}" + translateExplicitDateFormat(rest)
    case otherChar(char, rest) => quoteAsRegex(char) + translateExplicitDateFormat(rest)
  }

  // format_modifiers = [left_justification_flag][minimum_field_width][.][maximum_field_width]
  // left_justification_flag = - for left justification (pad on the right) , not present -> right justification (pad on the left)
  // Bsp: %20c, %-20c , %.30c, %20.30c, %-20.30c
  private def align(baseRegex: String, leftjust: String, minwidth: String, maxwidth: String): String =
    if (null == minwidth || minwidth.isEmpty) baseRegex
    else leftjust match {
      // widths are ignored for now - that'd be hard in regexes
      case "-" => baseRegex + " *"
      case "" | null => " *" + baseRegex
    }

  private def quoteAsRegex(literalchars: String): String = literalchars.replaceAll("%%", "%").replaceAll("%n", "\\n")
    .replaceAll( """([(){}|\\\[\]])""", """\\$1""")

  def replaceMatchesAndInbetween(source: String, regex: Regex, matchfunc: Regex.Match => String, betweenfunc: String => String): String = {
    val res = new StringBuilder
    var lastend = 0
    regex findAllMatchIn source foreach { thematch =>
      res ++= betweenfunc(source.substring(lastend, thematch.start))
      res ++= matchfunc(thematch)
      lastend = thematch.end
    }
    res ++= betweenfunc(source.substring(lastend, source.length))
    res.toString()
  }

}

case class TranslationException(reason: String) extends Exception(reason)

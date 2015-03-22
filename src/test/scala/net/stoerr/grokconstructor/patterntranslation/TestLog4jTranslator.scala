package net.stoerr.grokconstructor.patterntranslation

import java.util

import org.apache.log4j.spi.{LoggingEvent, ThrowableInformation}
import org.apache.log4j.{Level, Logger, PatternLayout}
import org.scalatest.FlatSpec

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 16.02.2015
 */
class TestLog4jTranslator extends FlatSpec {

  "Log4jTranslator" should "recognize conversion specifiers" in {
    def m(p: String) = p match {
      case Log4jTranslator.conversionSpecifier(leftjust, minwidth, maxwidth, name, argument) =>
        (leftjust, minwidth, maxwidth, name, argument)
    }
    assert(m("%c") ==(null, null, null, "c", null))
    assert(m("%20c") ==(null, "20", null, "c", null))
    assert(m("%-20c") ==("-", "20", null, "c", null))
    assert(m("%.30c") ==(null, null, "30", "c", null))
    assert(m("%20.30c") ==(null, "20", "30", "c", null))
    assert(m("%-20.30c") ==("-", "20", "30", "c", null))
    assert(m("%d{bla}") ==(null, null, null, "d", "bla"))
  }

  it should "translate patterns" in {
    assert( """bla%{LOGLEVEL:loglevel}blu\{\}\[\]\(\)\|""" == Log4jTranslator.translate("bla%pblu{}[]()|"))
    assert( """(?<date>%{TIMESTAMP_ISO8601})  *%{LOGLEVEL:loglevel} \[ *%{JAVACLASS:logger}\]  *${WORD:sessionId}? ${WORD:requestId}? - %{GREEDYDATA:message}\r?\n""" == Log4jTranslator.translate("%d{ISO8601} %-5.5p [%-30c{1}] %-32X{sessionId} %X{requestId} - %m%n"))
  }

  "log4j" should "format messages" in {
    val layout = new PatternLayout("%d{dd.MM.yyyy HH:mm:ss,SSS} - [%-5p] %c %X{sid} %m")
    val mdc = new util.HashMap[String, String]()
    mdc.put("sid", "83k238d2")
    mdc.put("rid", "83482")
    val event = new LoggingEvent(getClass().toString(), Logger.getLogger(getClass), 1424339008197L, Level.ERROR, "this is the message",
      "main", new ThrowableInformation(new Exception("whatever")), "theNdc", null, mdc)
    val formatted = layout.format(event)
    assert("19.02.2015 10:43:28,197 - [ERROR] net.stoerr.grokconstructor.patterntranslation.TestLog4jTranslator 83k238d2 this is the message" == formatted)
  }

}

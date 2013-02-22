package net.stoerr.grokdiscoverytoo

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 17.02.13
 */
object GrokPatternLibrary {

  val grokpatternnames = List("firewalls", "grok-patterns", "haproxy", "java", "linux-syslog", "nagios", "ruby")
  val grokpatternKeys = grokpatternnames.map(p => (p -> p)).toMap

  val extrapatternnames = List("extras")
  val extrapatternKeys = extrapatternnames.map(p => (p -> p)).toMap

}

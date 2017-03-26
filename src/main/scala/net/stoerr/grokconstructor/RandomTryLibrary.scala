package net.stoerr.grokconstructor

import scala.util.Random


/**
 * A collection of values to make it easy to explore the functionality.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 02.03.13
 */
object RandomTryLibrary {

  private val trials = List(
    Trial( """2013-02-28 09:57:56,662 WARN  CreateSomethingActivationKey - WhateverException for User 49-123-345678 {{rid,US8cFAp5eZgAABwUItEAAAAI_dev01_443}{realsid,60A9772A136B9912B6FF0C3627A47090.dev1-a}}
             |2013-02-28 09:57:56,663 INFO  LMLogger - ERR1700 - u:null failures: 0  - Technical error {{rid,US8cFAp5eZgAABwUItEAAAAI_dev01_443}{realsid,60A9772A136B9912B6FF0C3627A47090.dev1-a}}
             |2013-02-28 09:57:56,668 ERROR SomeCallLogger - ESS10005 Cpc portalservices: Exception caught while writing log messege to MEA Call:  {}
             |java.sql.SQLSyntaxErrorException: ORA-00942: table or view does not exist
             |
             |	at oracle.jdbc.driver.T4CTTIoer.processError(T4CTTIoer.java:445)
             |	at oracle.jdbc.driver.T4CTTIoer.processError(T4CTTIoer.java:396)
             |2013-02-28 10:04:35,723 INFO  EntryFilter - Fresh on request /portalservices/foobarwhatever {{rid,US8dogp5eZgAABwXPGEAAAAL_dev01_443}{realsid,56BA2AD41D9BB28AFCEEEFF927EE61C2.dev1-a}}""".stripMargin,
      """\A%{TIMESTAMP_ISO8601:timestamp}\s+%{LOGLEVEL:loglevel}\s+(?<logger>(?:[a-zA-Z0-9-]+\.)*[A-Za-z0-9$]+)\s+(-\s+)?(?=(?<msgnr>[A-Z]+[0-9]{4,5}))*%{DATA:message}({({[^}]+},?\s*)*})?\s*$(?<stacktrace>(?m:.*))?""",
      Some( """^(?!201)""")),
    Trial( """"uRzbUwp5eZgAAAAaqIAAAAAa" | 5.3.2.1 - - - [24/Feb/2013:13:40:51 +0100] "GET /cpc HTTP/1.1" 302 -
             |"URzbTwp5eZgAAAAWlbUAAAAV" | 4.3.2.7 - - - [14/Feb/2013:13:40:47 +0100] "GET /cpc/finish.do?cd=true&mea_d=0&targetPage=%2Fcpc%2F HTTP/1.1" 200 5264
             |"URzbUwp5eZgAAAAaqIEAAAAa" | 4.3.2.1 - - - [14/Feb/2013:13:40:51 +0100] "GET /cpc/ HTTP/1.1" 402 -
             |"URzbUwp5eZgAAAAWlbYAAAAV" | 4.3.2.1 - - - [14/Feb/2013:13:40:51 +0100] "POST /cpc/ HTTP/1.1" 305 - """.stripMargin,
      """^\"(?<rid>[^\"]+)\" \| %{IPORHOST:clientip} (?:-|%{IPORHOST:forwardedfor}) (?:-|%{USER:ident}) (?:-|%{USER:auth}) \[%{HTTPDATE:timestamp}\] \"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|-)\" %{NUMBER:response} (?:-|%{NUMBER:bytes})""", None),
    Trial( """10.121.123.104 - - [01/Nov/2012:21:01:04 +0100] "GET /cluster HTTP/1.1" 200 1272
             |10.121.123.104 - - [01/Nov/2012:21:01:17 +0100] "GET /cpc/auth.do?loginsetup=true&targetPage=%2Fcpc%2F HTTP/1.1" 302 466
             |10.121.123.104 - - [01/Nov/2012:21:01:18 +0100] "GET /cpc?loginsetup=true&targetPage=%252Fcpc%252F HTTP/1.1" 302 -
             |10.121.123.104 - - [01/Nov/2012:21:01:18 +0100] "GET /cpc/auth.do?loginsetup=true&targetPage=%25252Fcpc%25252F&loginsetup=true HTTP/1.1" 302 494""".stripMargin,
      """^%{IPORHOST:clientip} (?:-|%{USER:ident}) (?:-|%{USER:auth}) \[%{HTTPDATE:timestamp}\] \"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|-)\" %{NUMBER:response} (?:-|%{NUMBER:bytes})""", None),
    Trial( """[Thu Nov 01 21:54:03 2012] [error] [client 1.2.3.4] File does not exist: /usr/local/apache2/htdocs/default/cpc
             |[Thu Nov 01 21:56:32 2012] [error] (146)Connection refused: proxy: AJP: attempt to connect to 1.2.3.4:8080 (dev1) failed
             |[Thu Nov 01 21:56:32 2012] [error] ap_proxy_connect_backend disabling worker for (dev1)
             |[Thu Nov 01 21:56:32 2012] [error] proxy: AJP: failed to make connection to backend: dev1
             |[Thu Nov 01 21:56:35 2012] [error] (146)Connection refused: proxy: AJP: attempt to connect to 1.2.3.4:8012 (dev1) failed
             |[Thu Nov 01 21:56:35 2012] [error] ap_proxy_connect_backend disabling worker for (dev1)
             |[Thu Nov 01 21:56:35 2012] [error] proxy: AJP: failed to make connection to backend: dev1""".stripMargin,
      """^\[(?<timestamp>%{DAY} %{MONTH} %{MONTHDAY} %{TIME} %{YEAR})\]\s+(\[%{WORD:loglevel}\]\s+)?%{GREEDYDATA:message}""", None)
  )

  def example(i: Int): Trial = trials(i)

  def randomExampleNumber() = Random.nextInt(trials.length)

  case class Trial(loglines: String, pattern: String, multiline: Option[String])

}

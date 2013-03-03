package net.stoerr.grokdiscoverytoo

import util.Random

/**
 * A collection of values to make it easy to explore the functionality.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 02.03.13
 */
object RandomTryLibrary {

  case class Trial(loglines: String, pattern: String, multline: Option[String])

  private val trials = List(
    Trial( """2013-02-28 09:57:56,662 WARN  PortalServiceRecreateTNVActivationKey - SMSSendingFailureException for MSISDN 49-175-4226253 {{rid,US8cFAp5eZgAABwUItEAAAAI_dev01_443}{realsid,60A9772A136B9912B6FF0C3627A47090.dev01-a}}
             |2013-02-28 09:57:56,663 INFO  LMLogger - PS1700 - u:null/bp:null/mp:null/customerNr: null/contractNr: null failures: 0 zeroSignOnMSISDN:  - Technical error {{rid,US8cFAp5eZgAABwUItEAAAAI_dev01_443}{realsid,60A9772A136B9912B6FF0C3627A47090.dev01-a}}
             |2013-02-28 09:57:56,668 ERROR MeaCallLogger - EKP10005 Cpc MessageConsumerThread-126 portalservices: Exception caught while writing log messege to MEA Call:  {}
             |java.sql.SQLSyntaxErrorException: ORA-00942: table or view does not exist
             |
             |	at oracle.jdbc.driver.T4CTTIoer.processError(T4CTTIoer.java:445)
             |	at oracle.jdbc.driver.T4CTTIoer.processError(T4CTTIoer.java:396)
             |2013-02-28 10:04:35,723 INFO  FirstEntryFilter - Fresh on request /portalservices/services/RecreateTNVActivationKeySoap {{rid,US8dogp5eZgAABwXPGEAAAAL_dev01_443}{realsid,56BA2AD41D9BB28AFCEEEFF927EE61C2.dev01-a}}
             | """, """\A%{TIMESTAMP_ISO8601:timestamp}\s+%{LOGLEVEL:loglevel}\s+(?<logger>(?:[a-zA-Z0-9-]+\.)*[A-Za-z0-9$]+)\s+(-\s+)?(?=(?<msgnr>[A-Z]+[0-9]{4,5}))*%{DATA:message}({({[^}]+},?\s*)*})?\s*$(?<stacktrace>(?m:.*))?""",
      Some( """^201""")),
    Trial( """10.121.123.104 - - [01/Nov/2012:21:01:04 +0100] "GET /cluster HTTP/1.1" 200 1272
             |10.121.123.104 - - [01/Nov/2012:21:01:17 +0100] "GET /cpc-sp/msisdnauth.do?loginsetup=true&mea_d=44&ekp.targetPage=%2Fcpc%2F HTTP/1.1" 302 466
             |10.121.123.104 - - [01/Nov/2012:21:01:18 +0100] "GET /cpc?loginsetup=true&mea_d=44&ekp.targetPage=%252Fcpc%252F HTTP/1.1" 302 -
             |10.121.123.104 - - [01/Nov/2012:21:01:18 +0100] "GET /cpc/?loginsetup=true&mea_d=44&ekp.targetPage=%252Fcpc%252F HTTP/1.1" 302 -
             |10.121.123.104 - - [01/Nov/2012:21:01:18 +0100] "GET /cpc-sp/logon?ekp.targetPage=%2Fcpc%2F%3Floginsetup%3Dtrue%26mea_d%3D44%26ekp.targetPage%3D%25252Fcpc%25252F&mea_d=49 HTTP/1.1" 302 -
             |10.121.123.104 - - [01/Nov/2012:21:01:18 +0100] "GET /cpc-sp/msisdnauth.do?loginsetup=true&mea_d=44&ekp.targetPage=%25252Fcpc%25252F&loginsetup=true HTTP/1.1" 302 494
             | """, """^%{IPORHOST:clientip} (?:-|%{USER:ident}) (?:-|%{USER:auth}) \[%{HTTPDATE:timestamp}\] \"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|-)\" %{NUMBER:response} (?:-|%{NUMBER:bytes})""", None),
    Trial( """[Thu Nov 01 21:54:03 2012] [error] [client 10.121.123.104] File does not exist: /usr/local/apache2/htdocs/default/cpc
             |[Thu Nov 01 21:56:32 2012] [error] (146)Connection refused: proxy: AJP: attempt to connect to 10.121.121.152:8012 (dev01.vkc.mms-dresden.telekom.de) failed
             |[Thu Nov 01 21:56:32 2012] [error] ap_proxy_connect_backend disabling worker for (dev01.vkc.mms-dresden.telekom.de)
             |[Thu Nov 01 21:56:32 2012] [error] proxy: AJP: failed to make connection to backend: dev01.vkc.mms-dresden.telekom.de
             |[Thu Nov 01 21:56:35 2012] [error] (146)Connection refused: proxy: AJP: attempt to connect to 10.121.121.152:8012 (dev01.vkc.mms-dresden.telekom.de) failed
             |[Thu Nov 01 21:56:35 2012] [error] ap_proxy_connect_backend disabling worker for (dev01.vkc.mms-dresden.telekom.de)
             |[Thu Nov 01 21:56:35 2012] [error] proxy: AJP: failed to make connection to backend: dev01.vkc.mms-dresden.telekom.de
             | """, """^\[(?<timestamp>%{DAY} %{MONTH} %{MONTHDAY} %{TIME} %{YEAR})\]\s+(\[%{WORD:loglevel}\]\s+)?%{GREEDYDATA:message}""", None)
  )

  def example(i: Int): Trial = trials(i)

  def randomExampleNumber() = 0; // Random.nextInt(trials.length)

}

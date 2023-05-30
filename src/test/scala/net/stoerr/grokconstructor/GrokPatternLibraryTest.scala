package net.stoerr.grokconstructor

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

import scala.io.{Codec, Source}

/**
  * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
  * @since 06.02.13
  */
@RunWith(classOf[JUnitRunner])
class GrokPatternLibraryTest extends FlatSpec with ShouldMatchers {

  "GrokPatternLibrary.readGrokPatterns" should "read a pattern file and replace patterns" in {
    val src = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream("grok/grok-patterns"))(Codec.UTF8)
    val patterns: Map[String, String] = GrokPatternLibrary.readGrokPatterns(src.getLines())
    patterns.size should equal(70)
    patterns("HOUR") should equal("(?:2[0123]|[01]?[0-9])")
    patterns("ISO8601_TIMEZONE") should equal("(?:Z|[+-]%{HOUR}(?::?%{MINUTE}))")
    patterns.foreach { case (k, v) =>
      val replaced = GrokPatternLibrary.replacePatterns(v, patterns)
      // println(k + "\t" + replaced)
      // make sure all patterns are actually compileable
      new JoniRegex(v).matchStartOf("bla")
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
    GrokPatternLibrary.replacePatterns("%{BU:foo:int}%{BLA:bar:float}", Map("BU" -> "XYZ", "BLA" -> "HU%{BU}HA")) should equal("(?<foo>XYZ)(?<bar>HU(?:XYZ)HA)")
    // joni is allergic to group names starting with [ , but not if it starts with a _ , so we make a hack and add a _ .
    GrokPatternLibrary.replacePatterns("%{BU:[nested][field][test]}%{BLA:[@metadata][timestamp]}", Map("BU" -> "XYZ", "BLA" -> "HU%{BU}HA")) should equal("" +
      "(?<_[nested][field][test]>XYZ)(?<_[@metadata][timestamp]>HU(?:XYZ)HA)")
    evaluating {
      GrokPatternLibrary.replacePatterns("%{NIX}", Map())
    } should produce[GrokPatternNameUnknownException]
  }

  "GrokPatternLibrary" should "read all patterns" in {
    val lib = GrokPatternLibrary.mergePatternLibraries(GrokPatternLibrary.grokPatternLibraryNames, None)
    lib.keys.size should equal(313)
    lib.keys.toList.sorted.mkString(",") should equal("BACULA_CAPACITY,BACULA_DEVICE,BACULA_DEVICEPATH,BACULA_HOST,BACULA_JOB,BACULA_LOGLINE,BACULA_LOG_ALL_RECORDS_PRUNED,BACULA_LOG_BEGIN_PRUNE_FILES,BACULA_LOG_BEGIN_PRUNE_JOBS,BACULA_LOG_CANCELLING,BACULA_LOG_CLIENT_RBJ,BACULA_LOG_DIFF_FS,BACULA_LOG_DUPLICATE,BACULA_LOG_ENDPRUNE,BACULA_LOG_END_VOLUME,BACULA_LOG_FATAL_CONN,BACULA_LOG_JOB,BACULA_LOG_JOBEND,BACULA_LOG_MARKCANCEL,BACULA_LOG_MAXSTART,BACULA_LOG_MAX_CAPACITY,BACULA_LOG_NEW_LABEL,BACULA_LOG_NEW_MOUNT,BACULA_LOG_NEW_VOLUME,BACULA_LOG_NOJOBS,BACULA_LOG_NOJOBSTAT,BACULA_LOG_NOOPEN,BACULA_LOG_NOOPENDIR,BACULA_LOG_NOPRIOR,BACULA_LOG_NOPRUNE_FILES,BACULA_LOG_NOPRUNE_JOBS,BACULA_LOG_NOSTAT,BACULA_LOG_NOSUIT,BACULA_LOG_NO_AUTH,BACULA_LOG_NO_CONNECT,BACULA_LOG_PRUNED_FILES,BACULA_LOG_PRUNED_JOBS,BACULA_LOG_READYAPPEND,BACULA_LOG_STARTJOB,BACULA_LOG_STARTRESTORE,BACULA_LOG_USEDEVICE,BACULA_LOG_VOLUME_PREVWRITTEN,BACULA_LOG_VSS,BACULA_LOG_WROTE_LABEL,BACULA_TIMESTAMP,BACULA_VERSION,BACULA_VOLUME,BASE10NUM,BASE16FLOAT,BASE16NUM,BIND9,BIND9_TIMESTAMP,BRO_CONN,BRO_DNS,BRO_FILES,BRO_HTTP,CATALINALOG,CATALINA_DATESTAMP,CISCOFW104001,CISCOFW104002,CISCOFW104003,CISCOFW104004,CISCOFW105003,CISCOFW105004,CISCOFW105005,CISCOFW105008,CISCOFW105009,CISCOFW106001,CISCOFW106006_106007_106010,CISCOFW106014,CISCOFW106015,CISCOFW106021,CISCOFW106023,CISCOFW106100,CISCOFW106100_2_3,CISCOFW110002,CISCOFW302010,CISCOFW302013_302014_302015_302016,CISCOFW302020_302021,CISCOFW304001,CISCOFW305011,CISCOFW313001_313004_313008,CISCOFW313005,CISCOFW321001,CISCOFW402117,CISCOFW402119,CISCOFW419001,CISCOFW419002,CISCOFW500004,CISCOFW602303_602304,CISCOFW710001_710002_710003_710005_710006,CISCOFW713172,CISCOFW733100,CISCOMAC,CISCOTAG,CISCOTIMESTAMP,CISCO_ACTION,CISCO_DIRECTION,CISCO_INTERVAL,CISCO_REASON,CISCO_TAGGED_SYSLOG,CISCO_XLATE_TYPE,CLOUDFRONT_ACCESS_LOG,COMBINEDAPACHELOG,COMMONAPACHELOG,COMMONMAC,CRONLOG,CRON_ACTION,DATA,DATE,DATESTAMP,DATESTAMP_EVENTLOG,DATESTAMP_OTHER,DATESTAMP_RFC2822,DATESTAMP_RFC822,DATE_EU,DATE_US,DAY,ELB_ACCESS_LOG,ELB_REQUEST_LINE,ELB_URI,ELB_URIPATHPARAM,EMAILADDRESS,EMAILLOCALPART,EXIM_DATE,EXIM_EXCLUDE_TERMS,EXIM_FLAGS,EXIM_HEADER_ID,EXIM_INTERFACE,EXIM_MSGID,EXIM_MSG_SIZE,EXIM_PID,EXIM_PROTOCOL,EXIM_QT,EXIM_REMOTE_HOST,EXIM_SUBJECT,GREEDYDATA,HAPROXYCAPTUREDREQUESTHEADERS,HAPROXYCAPTUREDRESPONSEHEADERS,HAPROXYDATE,HAPROXYHTTP,HAPROXYHTTPBASE,HAPROXYTCP,HAPROXYTIME,HOSTNAME,HOSTPORT,HOUR,HTTPD20_ERRORLOG,HTTPD24_ERRORLOG,HTTPDATE,HTTPDERROR_DATE,HTTPDUSER,HTTPD_COMBINEDLOG,HTTPD_COMMONLOG,HTTPD_ERRORLOG,INT,IP,IPORHOST,IPV4,IPV6,ISO8601_SECOND,ISO8601_TIMEZONE,JAVACLASS,JAVAFILE,JAVALOGMESSAGE,JAVAMETHOD,JAVASTACKTRACEPART,JAVATHREAD,LOGLEVEL,MAC,MAVEN_VERSION,MCOLLECTIVE,MCOLLECTIVEAUDIT,MINUTE,MONGO3_COMPONENT,MONGO3_LOG,MONGO3_SEVERITY,MONGO_LOG,MONGO_QUERY,MONGO_SLOWQUERY,MONGO_WORDDASH,MONTH,MONTHDAY,MONTHNUM,MONTHNUM2,NAGIOSLOGLINE,NAGIOSTIME,NAGIOS_CURRENT_HOST_STATE,NAGIOS_CURRENT_SERVICE_STATE,NAGIOS_EC_DISABLE_HOST_CHECK,NAGIOS_EC_DISABLE_HOST_NOTIFICATIONS,NAGIOS_EC_DISABLE_HOST_SVC_NOTIFICATIONS,NAGIOS_EC_DISABLE_SVC_CHECK,NAGIOS_EC_DISABLE_SVC_NOTIFICATIONS,NAGIOS_EC_ENABLE_HOST_CHECK,NAGIOS_EC_ENABLE_HOST_NOTIFICATIONS,NAGIOS_EC_ENABLE_HOST_SVC_NOTIFICATIONS,NAGIOS_EC_ENABLE_SVC_CHECK,NAGIOS_EC_ENABLE_SVC_NOTIFICATIONS,NAGIOS_EC_LINE_DISABLE_HOST_CHECK,NAGIOS_EC_LINE_DISABLE_HOST_NOTIFICATIONS,NAGIOS_EC_LINE_DISABLE_HOST_SVC_NOTIFICATIONS,NAGIOS_EC_LINE_DISABLE_SVC_CHECK,NAGIOS_EC_LINE_DISABLE_SVC_NOTIFICATIONS,NAGIOS_EC_LINE_ENABLE_HOST_CHECK,NAGIOS_EC_LINE_ENABLE_HOST_NOTIFICATIONS,NAGIOS_EC_LINE_ENABLE_HOST_SVC_NOTIFICATIONS,NAGIOS_EC_LINE_ENABLE_SVC_CHECK,NAGIOS_EC_LINE_ENABLE_SVC_NOTIFICATIONS,NAGIOS_EC_LINE_PROCESS_HOST_CHECK_RESULT,NAGIOS_EC_LINE_PROCESS_SERVICE_CHECK_RESULT,NAGIOS_EC_LINE_SCHEDULE_HOST_DOWNTIME,NAGIOS_EC_PROCESS_HOST_CHECK_RESULT,NAGIOS_EC_PROCESS_SERVICE_CHECK_RESULT,NAGIOS_EC_SCHEDULE_HOST_DOWNTIME,NAGIOS_EC_SCHEDULE_SERVICE_DOWNTIME,NAGIOS_HOST_ALERT,NAGIOS_HOST_DOWNTIME_ALERT,NAGIOS_HOST_EVENT_HANDLER,NAGIOS_HOST_FLAPPING_ALERT,NAGIOS_HOST_NOTIFICATION,NAGIOS_PASSIVE_HOST_CHECK,NAGIOS_PASSIVE_SERVICE_CHECK,NAGIOS_SERVICE_ALERT,NAGIOS_SERVICE_DOWNTIME_ALERT,NAGIOS_SERVICE_EVENT_HANDLER,NAGIOS_SERVICE_FLAPPING_ALERT,NAGIOS_SERVICE_NOTIFICATION,NAGIOS_TIMEPERIOD_TRANSITION,NAGIOS_TYPE_CURRENT_HOST_STATE,NAGIOS_TYPE_CURRENT_SERVICE_STATE,NAGIOS_TYPE_EXTERNAL_COMMAND,NAGIOS_TYPE_HOST_ALERT,NAGIOS_TYPE_HOST_DOWNTIME_ALERT,NAGIOS_TYPE_HOST_EVENT_HANDLER,NAGIOS_TYPE_HOST_FLAPPING_ALERT,NAGIOS_TYPE_HOST_NOTIFICATION,NAGIOS_TYPE_PASSIVE_HOST_CHECK,NAGIOS_TYPE_PASSIVE_SERVICE_CHECK,NAGIOS_TYPE_SERVICE_ALERT,NAGIOS_TYPE_SERVICE_DOWNTIME_ALERT,NAGIOS_TYPE_SERVICE_EVENT_HANDLER,NAGIOS_TYPE_SERVICE_FLAPPING_ALERT,NAGIOS_TYPE_SERVICE_NOTIFICATION,NAGIOS_TYPE_TIMEPERIOD_TRANSITION,NAGIOS_WARNING,NETSCREENSESSIONLOG,NONNEGINT,NOTSPACE,NUMBER,PATH,POSINT,POSTGRESQL,PROG,QS,QUOTEDSTRING,RAILS3,RAILS3FOOT,RAILS3HEAD,RAILS3PROFILE,RCONTROLLER,REDISLOG,REDISMONLOG,REDISTIMESTAMP,RPROCESSING,RT_FLOW1,RT_FLOW2,RT_FLOW3,RT_FLOW_EVENT,RUBY_LOGGER,RUBY_LOGLEVEL,RUUID,S3_ACCESS_LOG,S3_REQUEST_LINE,SECOND,SFW2,SHOREWALL,SPACE,SQUID3,SYSLOG5424BASE,SYSLOG5424LINE,SYSLOG5424PRI,SYSLOG5424PRINTASCII,SYSLOG5424SD,SYSLOGBASE,SYSLOGBASE2,SYSLOGFACILITY,SYSLOGHOST,SYSLOGLINE,SYSLOGPAMSESSION,SYSLOGPROG,SYSLOGTIMESTAMP,TIME,TIMESTAMP_ISO8601,TOMCATLOG,TOMCAT_DATESTAMP,TTY,TZ,UNIXPATH,URI,URIHOST,URIPARAM,URIPATH,URIPATHPARAM,URIPROTO,URN,USER,USERNAME,UUID,WINDOWSMAC,WINPATH,WORD,YEAR")
  }

}

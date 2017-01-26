grokconstructor
================

AVAILABLE ON http://grokconstructor.appspot.com/

Grok is a collection of named regular expressions that can be used - for instance with logstash http://logstash.net/ -
to parse logfiles. GrokDiscovery http://grokdebug.herokuapp.com/ can somewhat help you by suggesting regular
expressions. GrokConstructor goes beyond that by finding many possible regular expressions
that match a whole set of logfile lines by using groks patterns and fixed strings. This can be done automatically
(which is of limited use only for small stuff), or in a incremental process.

Use it on http://grokconstructor.appspot.com/ - there is also a good description, and you can use it on
some examples or for your own log lines you want to match.

To run locally, build with
mvn clean install
and start with
mvn appengine:devserver
. It runs on http://localhost:9090/

If you want to run it on a system without internet connection or that has an application server, anyway,
you can also deploy the created target/GrokConstructor-*-SNAPSHOT.war e.g. on a Tomcat.

Status: beta.

TODO:
- Better error handling in incremental matching
- Give definition of grok patterns as title on pattern selections.
- Send mail for bugreports

TODO eventually (aka probably never, unless someone asks):
- Move error box on top of page
- AutomaticDiscoveryView: use simpler examples such that the output makes sense.
- Quoting and unquoting of patterns for logstash.conf

https://cloud.google.com/appengine/docs/java/tools/maven :
mvn help:describe -DgroupId=com.google.appengine -DartifactId=appengine-maven-plugin -Ddetail=true


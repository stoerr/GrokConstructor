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

Status: beta.

TODO:
- Error handling in incremental matching

TODO eventually:
- Move error box on top of page
- AutomaticDiscoveryView: use simpler examples such that the output makes sense.
- Quoting and unquoting of patterns for logstash.conf
- Describe how to run it locally.
- Clarify licence.


<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html
        PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>Grok Discovery Too Results</title>
</head>
<body>
<h1>Grok Discovery<sup>2</sup> Results</h1>
<p>For the loglines<p>
<textarea rows="10" cols="120" name="loglines" disabled="disabled"><%= request.getParameter("loglines")%></textarea>
<p>We have the following possible regexps:</p>
<table>
<% for(java.util.Iterator it = (java.util.Iterator)request.getAttribute("results"); it.hasNext(); ) { %>
<%= it.next() %>
<% } %>
</table>
</body>
</html>

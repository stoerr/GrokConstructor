<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%-- Shows detailled information about the request and the session. Call with /admintool/secure/toolsuite/requestproperties.do . --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="java.util.Iterator"
%><%@page import="java.util.List"
%><%@page import="java.util.Enumeration"
%><%@page import="java.util.ArrayList"
%><%@page import="java.util.Collections"
%><html
    xmlns="http://www.w3.org/1999/xhtml">
<%!public List sortedValues(Enumeration en) {
        ArrayList vals = new ArrayList();
        while (en.hasMoreElements()) {
            vals.add(en.nextElement());
        }
        Collections.sort(vals, String.CASE_INSENSITIVE_ORDER);
        return vals;
    }

    public List sortedValues(Iterator it) {
        ArrayList vals = new ArrayList();
        while (it.hasNext()) {
            vals.add(it.next());
        }
        Collections.sort(vals, String.CASE_INSENSITIVE_ORDER);
        return vals;
    }

    public String escape(Object obj) {
        return (""+String.valueOf(obj)).replace("&", "&amp;").replaceAll("<", "&lt;");
    }
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Request properties</title>
</head>
<body>
<%-- ============================================================  --%>
<h2>Misc. Request properties:</h2>
Session Timeout : <%=session.getMaxInactiveInterval()%>
<br/>
Session Id : <%=session.getId()%>
<br/>
Session Creation Time : <%=new java.util.Date(session.getCreationTime())%>
<br/>
Request URI:
<%=escape(request.getRequestURI())%>
<br />
Request URL:
<%=escape(request.getRequestURL())%>
<br />
Request Method:
<%=request.getMethod()%>
<br />
Request Protocol:
<%=request.getProtocol()%>
<br />
Server Name:
<%=request.getServerName()%>
<br />
Server Port:
<%=request.getServerPort()%>
<br />
Remote host:
<%=request.getRemoteHost()%>
<br />
Remote port:
<%=request.getRemotePort()%>
<br />
Remote addr:
<%=request.getRemoteAddr()%>
<br />
Remote user:
<%=request.getRemoteUser()%>
<br />
Request Scheme:
<%=request.getScheme()%>
<br />
Secure:
<%=request.isSecure()%>
<br />
LocalAddr:
<%=request.getLocalAddr()%>
<br />
LocalName:
<%=request.getLocalName()%>
<br />
LocalPort:
<%=request.getLocalPort()%>
<br />
Locale:
<%=request.getLocale()%>
<br />
CharacterEncoding:
<%=request.getCharacterEncoding()%>
<br />
app.clusterNodeId / tomcat.jvmroute
<%=System.getProperty("app.clusterNodeId")%> / <%=System.getProperty("tomcat.jvmRoute")%>
<%-- ============================================================  --%>
<h2>Request headers:</h2>
<table border="1" width="100%">
    <%
        Iterator it = sortedValues(request.getHeaderNames()).iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            String headers = "";
            for (Enumeration enval = request.getHeaders(name); enval.hasMoreElements();) {
                headers = headers + escape(enval.nextElement()) + "<br />";
            }
    %>
    <tr>
        <td><%=name%></td>
        <td><%=headers%></td>
    </tr>
    <%
        }
    %>
</table>
<%-- ============================================================  --%>
<h2>Request parameters:</h2>
<table border="1" width="100%">
    <%
        it = sortedValues(request.getParameterNames()).iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            String values = "";
            String[] vals = request.getParameterValues(name);
            for (int i = 0; i < vals.length; ++i) {
                values = values + escape(vals[i]) + "<br />";
            }
    %>
    <tr>
        <td><%=name%></td>
        <td><%=values%></td>
    </tr>
    <%
        }
    %>
</table>
<%-- ============================================================  --%>
<h2>Request attributes:</h2>
<table border="1" width="100%">
    <%
        it = sortedValues(request.getAttributeNames()).iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
    %>
    <tr>
        <td><%=name%></td>
        <td><%=escape(request.getAttribute(name))%></td>
    </tr>
    <%
        }
    %>
</table>
<%-- ============================================================  --%>
<h2>Session attributes:</h2>
<table border="1" width="100%">
    <%
        it = sortedValues(session.getAttributeNames()).iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            Object obj = session.getAttribute(name);
    %>
    <tr>
        <td><%=name%></td>
        <td><%=escape(obj)%></td>
    </tr>
    <%
        }
    %>
</table>
<%-- ============================================================  --%>
<h2>System properties:</h2>
<table border="1" width="100%">
    <%
        it = sortedValues(System.getProperties().keys()).iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            Object obj = System.getProperty(name);
    %>
    <tr>
        <td><%=name%></td>
        <td><%=escape(obj)%></td>
    </tr>
    <%
        }
    %>
</table>
</p>
</body>
</html>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html
        PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="language" content="en" />
<meta name="author" content="Hans-Peter St&ouml;rr, www.stoerr.net" />
<meta name="robots" content="all, index, follow" />
<meta name="allow-search" content="yes" />
<meta name="content-language" content="en" />
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<meta name="revisit-after" content="31 days" />
<meta name="robots" content="index, nofollow" />
<!-- <link href="../css/styles.css" rel="stylesheet" type="text/css" /> -->
<link href="../css/combinedstyles.min.css" rel="stylesheet" type="text/css"/>
<!--[if lte IE 7]>
    <link href="yaml/core/iehacks.min.css" rel="stylesheet" type="text/css"/>
    <![endif]-->

<!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

<title><%=request.getAttribute("title")%></title>
</head>
<body>
  <div class="ym-wrapper">
    <header>
    <div class="ym-wbox">
      <h1>
        Grok Discovery<sup>2</sup>
      </h1>
    </div>
    </header>
    <nav id="nav">
    <div class="ym-hlist"><ul>
      <%=request.getAttribute("navigation")%>
    </ul></div>
    </nav>
    <div id="main">
      <div class="ym-wbox">
         <section class="ym-grid linearize-level-1">
            <article class="ym-gl content">
                <%=request.getAttribute("body")%>
            </article>
         </section>
      </div>
    </div>
    <footer>
    <div class="ym-wbox">
      <p>
        <a href="http://www.stoerr.net">Hans-Peter St&ouml;rr</a>
        &ndash; Layout based on <a href="http://www.yaml.de">YAML</a>
      </p>
    </div>
    </footer>
  </div>
</html>

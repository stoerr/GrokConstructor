package net.stoerr.grokconstructor.webframework

import java.util.logging.{Level, Logger}
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import net.stoerr.grokconstructor.automatic.AutomaticDiscoveryView
import net.stoerr.grokconstructor.incremental.{IncrementalConstructionInputView, IncrementalConstructionStepView}
import net.stoerr.grokconstructor.matcher.MatcherEntryView

import scala.collection.JavaConversions._
import scala.xml.{Elem, NodeSeq}

/**
 * Servlet that forwards the request to a controller and displays the view.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 15.02.13
 */
class WebDispatcher extends HttpServlet {

  val logger = Logger.getLogger("WebDispatcher")

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {
    doGet(req, resp)
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    logger.fine("Processing request " + reqInfo(req))
    try {
      val vieworredirect: Either[String, WebView] = giveView(req)
      vieworredirect match {
        case Left(url) =>
          resp.sendRedirect(url)
        case Right(view) =>
          req.setAttribute("title", view.title)
          req.setAttribute("body", view.body)
          req.setAttribute("navigation", navigation(req))
          resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
          resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
          resp.setDateHeader("Expires", 0);
          getServletContext.getRequestDispatcher("/jsp/frame.jsp").forward(req, resp)
      }
    } catch {
      case e: Exception =>
        logger.log(Level.SEVERE, reqInfo(req), e)
        errorPage(req, resp, e);
    }
  }

  def giveView(request: HttpServletRequest): Either[String, WebView] = {
    val view = (request.getPathInfo) match {
      case MatcherEntryView.path => new MatcherEntryView(request)
      case IncrementalConstructionInputView.path => new IncrementalConstructionInputView(request)
      case IncrementalConstructionStepView.path => new IncrementalConstructionStepView(request)
      case AutomaticDiscoveryView.path => new AutomaticDiscoveryView(request)
    }
    val forward: Option[Either[String, WebView]] = view.doforward
    forward match {
      case Some(res) => res
      case None => Right(view)
    }
  }

  def navigation(request: HttpServletRequest): NodeSeq = {
    def navlink(currentpath: String, descr: String): Elem =
      if (request.getPathInfo == currentpath) <li class="active">
        <strong>
          {descr}
        </strong>
      </li>
      else <li>
        <a href={request.getContextPath + request.getServletPath + currentpath}>
          {descr}
        </a>
      </li>

    navlink("/../", "About") ++ navlink(IncrementalConstructionInputView.path, "Incremental Construction") ++
      navlink(MatcherEntryView.path, "Matcher") ++ navlink(AutomaticDiscoveryView.path, "Automatic Construction")
  }

  def reqInfo(req: HttpServletRequest): String = {
    req.getRequestURL + "?" + req.getQueryString + ":" +
      req.getParameterMap.mapValues(_.asInstanceOf[Array[String]].mkString("\n"))
  }

  def errorPage(req: HttpServletRequest, resp: HttpServletResponse, e: Exception): Unit = {
    val writer = resp.getWriter
    writer.println(
      """
        | <h1>OUCH!</h1>
        | <p>I'm sorry, but you have encountered a bug or missing nice display of an error message in the application.
        | Please contact Hans-Peter St&ouml;rr (<a href="http://www.stoerr.net/">www.stoerr.net</a>
        |  <script type="text/javascript" language="javascript">
        |<!--
        |// Email obfuscator script 2.1 by Tim Williams, University of Arizona
        |// Random encryption key feature by Andrew Moulden, Site Engineering Ltd
        |// This code is freeware provided these four comment lines remain intact
        |// A wizard to generate this code is at http://www.jottings.com/obfuscator/
        |{ coded = "wTyLbnnN@vnLb40.NS"
        |  key = "TVMD2qg4GEz6JIjNKbkX1sCcByd9AW8FieUftQlSRLowu3Opx5anZYvH7rPm0h"
        |  shift=coded.length
        |  link=""
        |  for (i=0; i<coded.length; i++) {
        |    if (key.indexOf(coded.charAt(i))==-1) {
        |      ltr = coded.charAt(i)
        |      link += (ltr)
        |    }
        |    else {
        |      ltr = (key.indexOf(coded.charAt(i))-shift+key.length) % key.length
        |      link += (key.charAt(ltr))
        |    }
        |  }
        |document.write("<a href='mailto:"+link+"'>"+link+"</a>")
        |}
        |//-->
        |</script>)
        | with a copy of this page, or open an issue on
        | <a href="https://github.com/stoerr/GrokConstructor/issues">https://github.com/stoerr/GrokConstructor/issues</a> .
        | </p><pre>
      """.stripMargin)
    writer.println("\nError message: " + e + "\n")
    writer.println("\nRequest Info:\n" + reqInfo(req) + "\n\n")
    e.printStackTrace(writer)
    writer.println("\n</pre>")
  }

}

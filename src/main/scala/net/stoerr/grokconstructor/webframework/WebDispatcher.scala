package net.stoerr.grokconstructor.webframework

import java.util.logging.{Level, Logger}
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import net.stoerr.grokconstructor.automatic.AutomaticDiscoveryView
import net.stoerr.grokconstructor.incremental.{IncrementalConstructionInputView, IncrementalConstructionStepView}
import net.stoerr.grokconstructor.matcher.MatcherEntryView

import scala.collection.JavaConversions
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
        val writer = resp.getWriter
        writer.println("OUCH! AGH! AAAH! BUG! Please contact Hans-Peter Stoerr www.stoerr.net with the following, or "
          + "open an issue on https://github.com/stoerr/GrokConstructor/issues:\n\n")
        e.printStackTrace(writer)
        writer.println("\n\nRequest Info:\n" + reqInfo(req))
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

}

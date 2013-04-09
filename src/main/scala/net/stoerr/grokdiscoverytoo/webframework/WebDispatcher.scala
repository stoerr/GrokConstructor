package net.stoerr.grokdiscoverytoo.webframework

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import net.stoerr.grokdiscoverytoo.matcher.MatcherEntryView
import net.stoerr.grokdiscoverytoo.incremental.{IncrementalConstructionStepView, IncrementalConstructionInputView}
import net.stoerr.grokdiscoverytoo.automatic.AutomaticDiscoveryView
import scala.xml.{Elem, NodeSeq}
import org.slf4j.LoggerFactory

/**
 * Servlet that forwards the request to a controller and displays the view.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 15.02.13
 */
class WebDispatcher extends HttpServlet {

  val logger = LoggerFactory.getLogger(getClass)

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {
    doGet(req, resp)
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    logger.info("Processing request {}", reqInfo(req))
    try {
      val vieworredirect: Either[String, WebView] = giveView(req)
      vieworredirect match {
        case Left(url) =>
          resp.sendRedirect(url)
        case Right(view) =>
          req.setAttribute("title", view.title)
          req.setAttribute("body", view.body)
          req.setAttribute("navigation", navigation(req))
          getServletContext.getRequestDispatcher("/jsp/frame.jsp").forward(req, resp)
      }
    } catch {
      case e: Exception =>
        logger.error(reqInfo(req), e)
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

  def reqInfo(req: HttpServletRequest): String = req.getRequestURL + "?" + req.getQueryString + ":" + req.getParameterMap

}

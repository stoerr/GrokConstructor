package net.stoerr.grokdiscoverytoo.webframework

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import net.stoerr.grokdiscoverytoo.matcher.MatcherEntryView
import net.stoerr.grokdiscoverytoo.incremental.{IncrementalConstructionStepView, IncrementalConstructionInputView}
import net.stoerr.grokdiscoverytoo.automatic.AutomaticDiscoveryView
import scala.xml.{Elem, NodeSeq}

/**
 * Servlet that forwards the request to a controller and displays the view.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 15.02.13
 */
class WebDispatcher extends HttpServlet {

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {
    doGet(req, resp)
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
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
  }

  def giveView(request: HttpServletRequest): Either[String, WebView] = {
    val view = (request.getServletPath + request.getPathInfo) match {
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
    def navlink(path: String, descr: String): Elem =
      if (request.getServletPath + request.getPathInfo == path) <li class="active"><strong>{descr}</strong></li>
      else  <li><a href={path}>{descr}</a></li>

    navlink("/", "About") ++ navlink(IncrementalConstructionInputView.path, "Incremental Construction") ++
      navlink(MatcherEntryView.path, "Matcher") ++ navlink(AutomaticDiscoveryView.path, "Automatic Construction")
  }

}

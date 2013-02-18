package net.stoerr.grokdiscoverytoo.webframe

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import net.stoerr.grokdiscoverytoo.matcher.MatcherEntryView

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
    val controller = giveController(req.getPathInfo, req)
    val view = controller.process(req)
    req.setAttribute("title", view.title)
    req.setAttribute("body", view.body)
    getServletContext.getRequestDispatcher("/frame.jsp").forward(req, resp)
  }

  def giveController(path: String, request: HttpServletRequest): WebController = path match {
    case "/match" => new EmptyController(new MatcherEntryView(request))
  }
}

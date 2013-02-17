package net.stoerr.grokdiscoverytoo.webframe

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

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
    req.setAttribute("title", "My Title")
    val body = <body>
      <h1>Hallo!</h1>
    </body>
    req.setAttribute("body", body)
    getServletContext.getRequestDispatcher("/frame.jsp").forward(req, resp)
  }
}

package net.stoerr.grokdiscoverytoo

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 07.02.13
 */
class GrokDiscoveryTooServlet extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    response.setContentType("text/html")
    response.setStatus(HttpServletResponse.SC_OK)
    response.getWriter.println("<h1>Hello Servlet</h1>")
    response.getWriter.println("session=" + request.getSession(true).getId)
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {
    super.doPost(req, resp)
  }
}

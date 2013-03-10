package net.stoerr.grokdiscoverytoo

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 07.02.13
 */
class GrokLibraryServlet extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val path = request.getPathInfo
    if (path.startsWith("/grok/")) {
      response.setContentType("text/plain")
      response.setStatus(HttpServletResponse.SC_OK)
      GrokPatternLibrary.grokSource(path.substring(6)).getLines().foreach(response.getWriter.println(_))
    }
  }

}

package net.stoerr.grokdiscoverytoo

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import io.Source

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 07.02.13
 */
class GrokDiscoveryTooServlet extends HttpServlet with GrokPatternReader {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    response.setContentType("text/html")
    response.setStatus(HttpServletResponse.SC_OK)
    response.getWriter.println("<h1>Hello Servlet</h1>")
    response.getWriter.println("session=" + request.getSession(true).getId)
  }

  override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
    val patternSource = Source.fromString(request.getParameter("patterns"))
    val loglines = Source.fromString(request.getParameter("loglines")).getLines().toList
    val patterns = readGrokPatterns(patternSource)
    val lines = new GrokDiscoveryToo(patterns).matchingRegexpStructures(loglines)
    request.setAttribute("results", lines.toList.toString())
    getServletContext.getRequestDispatcher("/result.jsp").forward(request, response)
  }
}

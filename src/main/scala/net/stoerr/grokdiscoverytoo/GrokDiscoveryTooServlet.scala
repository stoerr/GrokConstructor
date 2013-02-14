package net.stoerr.grokdiscoverytoo

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import io.Source
import net.stoerr.grokdiscoverytoo.GrokDiscoveryToo.{NamedRegex, FixedString, RegexPart}
import xml.Elem
import concurrent.duration.span

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 07.02.13
 */
class GrokDiscoveryTooServlet extends HttpServlet with GrokPatternReader {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val path = request.getPathInfo
    if (!path.matches("^[a-z/-]+$")) throw new IllegalArgumentException("Invalid path " + path)
    if (path.startsWith("/grok/")) {
      response.setContentType("text/plain")
      response.setStatus(HttpServletResponse.SC_OK)
      val src = Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(path))
      src.getLines().foreach(response.getWriter.println(_))
    }
  }

  override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
    val patternSource = Source.fromString(request.getParameter("patterns"))
    val loglines = Source.fromString(request.getParameter("loglines")).getLines().toList
    val patterns = readGrokPatterns(patternSource)
    val lines = new GrokDiscoveryToo(patterns).matchingRegexpStructures(loglines)
    request.setAttribute("results", resultTable(lines).mkString("\n"))
    getServletContext.getRequestDispatcher("/result.jsp").forward(request, response)
  }

  def resultTable(results: Iterator[List[RegexPart]]): Iterator[xml.Elem] = {
    results map { result =>
      <tr><td>{
        result map {
          case FixedString(str) => <span>{str}</span>
          case NamedRegex(patterns) => <select> {
              patterns map {pattern => <option>{pattern}</option>}
            }</select>
        }
      }</td></tr>
    }
  }

}

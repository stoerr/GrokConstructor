package net.stoerr.grokdiscoverytoo

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import io.Source
import net.stoerr.grokdiscoverytoo.GrokDiscoveryToo.{NamedRegex, FixedString, RegexPart}
import java.util

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 07.02.13
 */
class GrokDiscoveryTooServlet extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val path = request.getPathInfo
    if (path.startsWith("/grok/")) {
      response.setContentType("text/plain")
      response.setStatus(HttpServletResponse.SC_OK)
      GrokPatternLibrary.grokSource(path.substring(6)).getLines().foreach(response.getWriter.println(_))
    }
  }

  override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
    val loglines = Source.fromString(request.getParameter("loglines")).getLines().filter(!_.isEmpty).toList
    val patterns = getPatternLines(request)
    val lines = new GrokDiscoveryToo(patterns).matchingRegexpStructures(loglines)
    request.setAttribute("results", toJavaIterator(resultTable(lines)))
    getServletContext.getRequestDispatcher("/result.jsp").forward(request, response)
  }


  def getPatternLines(request: HttpServletRequest): Map[String, String] = {
    val grokParameters = Option(request.getParameterValues("grok")).getOrElse(Array()).toList
    GrokPatternLibrary.mergePatternLibraries(grokParameters, Option(request.getParameter("patterns")))
  }

  def resultTable(results: Iterator[List[RegexPart]]): Iterator[xml.Elem] = {
    results map {
      result =>
        <tr>
          <td>
            {result map {
            case FixedString(str) => <span>
              {str}
            </span>
            case NamedRegex(patterns) if (patterns.size == 1) => <span>
              {"%{" + patterns(0) + "}"}
            </span>
            case NamedRegex(patterns) => <select>
              {patterns map {
                pattern => <option>
                  {"%{" + pattern + "}"}
                </option>
              }}
            </select>
          }}
          </td>
        </tr>
    }
  }

  def toJavaIterator[T](scalaIterator: Iterator[T]): java.util.Iterator[T] = new util.Iterator[T] {
    def next(): T = scalaIterator.next()

    def remove() {
      throw new UnsupportedOperationException
    }

    def hasNext: Boolean = scalaIterator.hasNext
  }

}

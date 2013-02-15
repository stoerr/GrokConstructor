package net.stoerr.grokdiscoverytoo

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import io.Source
import net.stoerr.grokdiscoverytoo.GrokDiscoveryToo.{NamedRegex, FixedString, RegexPart}
import java.util

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 07.02.13
 */
class GrokDiscoveryTooServlet extends HttpServlet with GrokPatternReader {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val path = request.getPathInfo
    if (path.startsWith("/grok/")) {
      response.setContentType("text/plain")
      response.setStatus(HttpServletResponse.SC_OK)
      grokSource(path).getLines().foreach(response.getWriter.println(_))
    }
  }

  def grokSource(location: String): Source = {
    if (!location.matches("^[a-z/-]+$")) throw new IllegalArgumentException("Invalid path " + location)
    return Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(location))
  }

  override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
    val loglines = Source.fromString(request.getParameter("loglines")).getLines().filter(!_.isEmpty).toList
    val patterns = readGrokPatterns(getPatternLines(request))
    val lines = new GrokDiscoveryToo(patterns).matchingRegexpStructures(loglines)
    request.setAttribute("results", toJavaIterator(resultTable(lines)))
    getServletContext.getRequestDispatcher("/result.jsp").forward(request, response)
  }


  def getPatternLines(request: HttpServletRequest): Iterator[String] = {
    val patternLines = Source.fromString(request.getParameter("patterns")).getLines()
    val grokParameters = Option(request.getParameterValues("grok")).getOrElse(Array())
    val grokPatternSources = for (grokfile <- grokParameters) yield grokSource("/grok/" + grokfile)
    val allPatternLines = grokPatternSources.map(_.getLines()).fold(patternLines)(_ ++ _)
    allPatternLines
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

package net.stoerr.grokconstructor

import java.util.Date
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

/**
  * Just a servlet to return some fake Javascript code to enable warmup calls from cached static pages.
  */
class WarmupServlet extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    response.setContentType("text/javascript")
    response.setStatus(HttpServletResponse.SC_OK)
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate") // HTTP 1.1
    response.setHeader("Pragma", "no-cache") // HTTP 1.0
    response.setDateHeader("Expires", 0)
    val writer = response.getWriter()
    writer.println("// Warmup called on " + new Date())
    writer.close()
  }

}

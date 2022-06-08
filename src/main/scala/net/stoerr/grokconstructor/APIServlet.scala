package net.stoerr.grokconstructor

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

/**
 * @author <a href="https://github.com/vinodpahuja">Vinod Pahuja</a>
 */
class APIServlet extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    
    val path = request.getPathInfo
    if (null == path || path.length == 1) {
        throw new IllegalArgumentException("invaid path")
    }
    
    response.setContentType("application/json")
    
    val api = path.substring(1)
    if(api.equals("match")) {

        var exampleNo = -1
        
        if(null != request.getParameter("randomize") ) {
            exampleNo = RandomTryLibrary.randomExampleNumber()
        } else if (null != request.getParameter("example")) {
            exampleNo = request.getParameter("example").toInt
        }

        if (-1 != exampleNo) {
        
            val trial = RandomTryLibrary.example(exampleNo)

            val result = (
                ( "loglines" -> Some(trial.loglines) ) ~ 
                ( "pattern"-> Some(trial.pattern) ) ~
                ( "multilineRegex" -> trial.multiline ) ~
                ( "multilineNegate" -> List() ) ~
                ( "groklibs" -> List("grok-patterns", "java") )
            )

            response.getWriter.write(compact(render(result)))
        }
        
    }
    
  }

}
package net.stoerr.grokdiscoverytoo

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 19.03.2014
 */
object HerokuStarter extends App {
  val server = new Server(Integer.valueOf(System.getenv("PORT")));
  val context = new ServletContextHandler(ServletContextHandler.SESSIONS);
  context.setContextPath("/");
  server.setHandler(context);
  context.addServlet(new ServletHolder(new GrokLibraryServlet()), "/*");
  server.start();
  server.join();
}

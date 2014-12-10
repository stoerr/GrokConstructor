package net.stoerr.grokconstructor

import java.util.logging.Logger
import javax.servlet.{ServletContextEvent, ServletContextListener}

import net.stoerr.grokconstructor.automatic.AutomaticDiscoveryView
import net.stoerr.grokconstructor.incremental.{IncrementalConstructionInputView, IncrementalConstructionStepView}
import net.stoerr.grokconstructor.matcher.MatcherEntryView

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 10.12.2014
 */
class WarmupListener extends ServletContextListener {
  val logger = Logger.getLogger("WarmupListener")

  override def contextInitialized(sce: ServletContextEvent) = {
    logger.info("Warming up")
    warmup(new IncrementalConstructionInputView(null))
    warmup(new IncrementalConstructionStepView(null))
    warmup(new MatcherEntryView(null))
    warmup(new AutomaticDiscoveryView(null))
    GrokPatternLibrary.mergePatternLibraries(GrokPatternLibrary.grokpatternnames, None)
    logger.info("Warmed up")
  }

  private def warmup(func: => Unit): Unit = {
    try {
      func
    } catch {
      case _: NullPointerException => // ignore; we actually expect NPE here.
    }
  }

  override def contextDestroyed(sce: ServletContextEvent) = {}
}

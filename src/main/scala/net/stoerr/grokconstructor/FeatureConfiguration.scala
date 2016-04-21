package net.stoerr.grokconstructor

/**
  * Switchable features - enables us to switch off alpha features for production without
  * having to maintain branches.
  */
object FeatureConfiguration {

  /** If started with environment variable feature.log4jtrans set to true we display the log4j translator */
  val patternTranslation: Boolean = Option(System.getenv("feature.patterntranslation")).getOrElse("false").toBoolean

}

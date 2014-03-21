/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 25.04.13
 *        We suggest combinations of packages that could be moved from a library into another artefact.
 */
object SuggestPackagesToSeparate extends App {

  import java.io.File
  import java.nio.charset.MalformedInputException
  import scala.io.{Codec, Source}

  def nodesReachableBy[N](start: N, successors: N => Traversable[N]): Set[N] = {
    def reach(visited: Set[N], start: N): Set[N] =
      successors(start).toSet.diff(visited).foldLeft(visited + start)(reach)
    reach(Set(), start)
  }

  def fileChildren(f: File) = Option(f.listFiles()).getOrElse(Array())

  val javafiles = nodesReachableBy(new File("."), fileChildren).filter(_.getName.endsWith(".java"))

  // regex tuned to find our usual java class references; group 1 is package name
  val fullyQualifiedJavaClass = """((?:[a-z_][a-z\d_]+)(?:\.[a-z_][a-z\d_]+)+)\.[A-Z][a-zA-Z\d_$]*[a-z\d_$][a-zA-Z\d_$]*""".r

  def stringFromFile(file: File): String =
    try {
      Source.fromFile(file)(Codec.UTF8).mkString
    } catch {
      case _: MalformedInputException => Source.fromFile(file)(Codec.ISO8859).mkString
    }

  def packageRefsOfAJavaFile(javafile: File): Iterator[(String, String)] = {
    val packagerefs = fullyQualifiedJavaClass.findAllMatchIn(stringFromFile(javafile)).map(_.group(1))
    val ourpackage = javafile.getPath.replaceFirst("^.[\\\\/]", "").replace(".java", "").replaceAll("[\\\\/]", ".").replaceFirst("\\.[^.]+$", "")
    packagerefs.map((ourpackage, _)) // TODO: perhaps dependency package to super-package? or vice versa?
  }

  val packageRefs: Map[String, Set[String]] = javafiles.par.flatMap(packageRefsOfAJavaFile).seq.groupBy(_._1).mapValues(_.map(_._2))
  val packages = packageRefs.keySet
  val dependencyGraph: Map[String, Set[String]] = packageRefs.mapValues(_.filter(packages.contains(_)))

  val upperbounds = packages.map(nodesReachableBy(_, dependencyGraph))
  upperbounds.toList.sortBy(_.toList.sorted.toString).sortBy(_.size) foreach {
    bound =>
      println(bound.toList.sortBy(_.toList.sorted.toString))
      println
  }

}

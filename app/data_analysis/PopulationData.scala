package data_analysis

import scala.io.Source
import scala.collection.mutable.ListBuffer
import play.api.Configuration
import javax.inject._

/** This class represents population data and provides methods to retrieve
  * information about it.
  * @param config
  *   Play Framework configuration object.
  * @param filename
  *   Name of the CSV file containing the population data.
  */
class PopulationData @Inject() (config: Configuration, filename: String) {

  /** A list buffer containing the parsed population data as `PopulationEntry`
    * objects.
    */
  private val populationEntryList = ListBuffer[PopulationEntry]()

  /** The path to the directory containing the CSV file with the population
    * data.
    */
  private val csvPath: String = config.get[String]("csv.path")

  /** Reads the CSV file with the population data and fills the
    * `populationEntryList` with `PopulationEntry` objects.
    */
  try {
    val source = Source.fromFile(s"$csvPath/$filename")
    source.getLines().drop(1).foreach { line =>
      if (line.trim.nonEmpty) {
        val parts = line.split(";")
        val code = parts(0).substring(1, parts(0).length - 1)
        val population = parts(3).substring(1, parts(3).length - 1).toDouble
        populationEntryList += new PopulationEntry(code, population)
      } 
    }
    source.close()
  } catch {
    case e: Exception => println(s"Error reading file: ${e.getMessage}")
  }

  /** Returns the population for the given country code.
    * @param code
    *   A 2-letter country code.
    * @return
    *   The population of the country with the given code, in thousands.
    */
  def getPopulationForCode(code: String): Double =
    populationEntryList
      .find(_.code == code)
      .map(_.population * 1000)
      .getOrElse(-1)
}

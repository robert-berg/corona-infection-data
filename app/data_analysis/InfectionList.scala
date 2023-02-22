package data_analysis

import scala.io.Source
import play.api.Configuration
import javax.inject._

/** This class represents a list of infection data for a specific country,
  * parsed from a CSV file.
  * @param config
  *   Play Framework configuration object.
  * @param filename
  *   Name of the CSV file containing the infection data.
  * @param iso2
  *   A 2-letter country code.
  */
class InfectionList @Inject() (
    config: Configuration,
    filename: String,
    iso2: String
) {

  /** A list of infection data, where each element represents the number of
    * infections for a specific day.
    */
  private var infectionsDataList = List[Int]()

  /** The path to the directory containing the CSV file with the infection data.
    */
  private val csvPath: String = config.get[String]("csv.path")

  /** Reads the CSV file with the infection data and fills the
    * `infectionsDataList` with integers representing the daily number of
    * infections.
    */
  try {
    val source = Source.fromFile(s"$csvPath/$filename")
    source.getLines().drop(1).foreach { line =>
      val parts = line.split(",")
      if (parts(1) == iso2) {
        infectionsDataList = infectionsDataList :+ parts(4).toInt
      }
    }
    source.close()
  } catch {
    case e: Exception => println(s"Error reading file: ${e.getMessage}")
  }

  /** Returns the list of infection data.
    * @return
    *   The list of integers representing the daily number of infections.
    */
  def getInfections: List[Int] = infectionsDataList

  /** Returns the number of data points in the infection data list.
    * @return
    *   The number of elements in the `infectionsDataList`.
    */
  def getNumDatapoints: Int = infectionsDataList.size

  /** Returns the 2-letter country code for which the infection data is stored.
    * @return
    *   The ISO 3166-1 alpha-2 code for the country.
    */
  def getCountryISO2: String = iso2
}

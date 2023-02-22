package data_analysis

import scala.io.Source
import play.api.Configuration
import javax.inject._
import scala.collection.mutable

/** Represents a map of 7-day incidence of new cases per 100,000 population for
  * each country in a given CSV file.
  *
  * @param config
  *   A Play `Configuration` instance that contains the configuration for the
  *   application.
  * @param filename
  *   The name of the CSV file containing the data.
  * @param pd
  *   A `PopulationData` instance that contains the population data for each
  *   country.
  * @param cc
  *   A `CountryCodes` instance that contains the ISO codes for each country.
  */
class CountryIncidenceMap @Inject() (
    config: Configuration,
    filename: String,
    pd: PopulationData,
    cc: CountryCodes
) {

  // The path to the directory containing the CSV file
  private val csvPath: String = config.get[String]("csv.path")

  // A map that will store the 7-day incidence of new cases for each country
  private val iso2ToCurrent7DayIncidenceMap: Map[String, Int] = (() => {

    val ccToinfectionSumMap = mutable.Map[String, Int]()
    try {
      // Open the CSV file for reading
      val source = Source.fromFile(s"$csvPath/$filename")

      // Read all lines of the CSV file into a list
      val lines = source.getLines().toList
      // Close the source file
      source.close()
      // Create a map that maps column names to their indices
      val dateToIndexMap = lines.head.split(",").zipWithIndex.toMap

      // Find the most recent date in the CSV file
      val lastDate = lines.tail.map { line =>
        val parts = line.split(",")
        parts(dateToIndexMap("Date_reported"))
      }.max

      // Find the date 7 days ago
      val sevenDaysAgo =
        java.time.LocalDate.parse(lastDate).minusDays(7).toString

      // Iterate over each line of the CSV file and update the incidence map for each country
      lines.tail.foreach { line =>
        val parts = line.split(",")
        val date = parts(dateToIndexMap("Date_reported"))
        if (date >= sevenDaysAgo) {
          val iso2 = parts(dateToIndexMap("Country_code"))
          val newInfections = parts(dateToIndexMap("New_cases")).toInt
          ccToinfectionSumMap += iso2 -> (ccToinfectionSumMap
            .getOrElse(iso2, 0) + newInfections)
        }
      }

      ccToinfectionSumMap.view.map { case (iso2, incidence) =>
        val population = pd.getPopulationForCode(cc.getCodeForISO2(iso2))
        val incidencePer100k: Int =
          ((incidence.toDouble / population) * 100000).toInt
        iso2 -> incidencePer100k
      }.toMap

    } catch {
      case e: Exception =>
        println(s"Error reading file: ${e.getMessage}")
        Map[String, Int]()
    }

  })()

  /** Returns an immutable map of ISO codes to the current 7-day incidence of
    * new cases per 100,000 population.
    *
    * @return
    *   An immutable map of ISO codes to the current 7-day incidence of new
    *   cases per 100,000 population.
    */
  def getIso2ToCurrent7DayIncidenceMap: Map[String, Int] =
    iso2ToCurrent7DayIncidenceMap.toMap

}

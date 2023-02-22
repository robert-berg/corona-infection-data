package data_analysis

import scala.io.Source
import play.api.Configuration
import javax.inject._

/** A class representing a list of country codes, with methods to retrieve the
  * country code given an ISO2 code, or to retrieve the ISO2 code given a
  * country code.
  *
  * @param config
  *   The Play configuration object
  * @param filename
  *   The name of the file containing the country codes
  */
class CountryCodes @Inject() (config: Configuration, filename: String) {

  private val csvPath: String = config.get[String]("csv.path")

  // Create a list of CountryCode objects from the contents of the file
  private val countryCodesList: List[CountryCode] = Source
    .fromFile(s"$csvPath/$filename")
    .getLines()
    .drop(1)
    .map(_.split(","))
    .filter(_.length <= 16)
    .flatMap[CountryCode] { parts =>
      try {
        Some(new CountryCode(parts(9).dropWhile(_ == '0'), parts(10)))
      } catch {
        case e: Exception =>
          println("Error adding country code"); None
      }
    }
    .toList

  /** Get the country code for a given ISO2 code
    *
    * @param iso2
    *   The ISO2 code of the country
    * @return
    *   The country code of the country, or "N/A" if the ISO2 code is not found
    */
  def getCodeForISO2(iso2: String): String =
    countryCodesList
      .find(_.iso2 == iso2)
      .map(_.code)
      .getOrElse("N/A")

  /** Get the ISO2 code for a given country code
    *
    * @param code
    *   The country code
    * @return
    *   The ISO2 code of the country, or "N/A" if the country code is not found
    */
  def getISO2ForCode(code: String): String =
    countryCodesList
      .find(_.code == code)
      .map(_.iso2)
      .getOrElse("N/A")
}

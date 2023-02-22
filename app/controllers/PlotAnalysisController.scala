package controllers

import play.api.libs.json._
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.Configuration

import data_analysis._

/** This controller creates an `Action` to handle HTTP requests to the plot
  * analysis API
  */
@Singleton
class PlotAnalysisController @Inject() (
    config: Configuration,
    cc: ControllerComponents
) extends AbstractController(cc) {

  val populationData = new PopulationData(config, "UN.csv")
  val countryCodes = new CountryCodes(config, "CountryCodes.csv")

  /** A function that takes a country code as a parameter and returns a JSON
    * object.
    */
  def plotAnalysis(countryCode: String) = Action {

    val infectionAnalysis =
      new InfectionAnalysis(
        new InfectionList(config, "WHO.csv", countryCode.toUpperCase),
        populationData,
        countryCodes
      )

    val json = Json.obj(
      "countryISO2" -> infectionAnalysis.getCountryISO2,
      "numDatapoints" -> infectionAnalysis.getNumDatapoints,
      "dailyInfections" -> infectionAnalysis.getDailyInfections,
      "incidenceSevenDays" -> infectionAnalysis.get7di
    )

    Ok(json)
  }

  /** A function that returns a JSON object containing the current 7-day
    * incidence rate for each country in the dataset.
    */
  def countryIncidenceMap() = Action {
    val incidenceMap = new CountryIncidenceMap(
      config,
      "WHO.csv",
      populationData,
      countryCodes
    )
    val json = Json.toJson(incidenceMap.getIso2ToCurrent7DayIncidenceMap)
    Ok(json)
  }
}

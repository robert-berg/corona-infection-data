package data_analysis

/** This class represents a tool for analyzing infection data for a specific
  * country.
  * @param il
  *   An instance of `InfectionList` containing the infection data.
  * @param pd
  *   An instance of `PopulationData` containing the population data.
  * @param cc
  *   An instance of `CountryCodes` containing the country code data.
  */
class InfectionAnalysis(
    il: InfectionList,
    pd: PopulationData,
    cc: CountryCodes
) {

  /** Returns the ISO 3166-1 alpha-2 code for the country being analyzed.
    * @return
    *   A 2-letter country code.
    */
  def getCountryISO2: String = il.getCountryISO2

  /** Returns the number of data points in the infection data.
    * @return
    *   The number of elements in the `InfectionList`.
    */
  def getNumDatapoints: Int = il.getNumDatapoints

  /** Returns an array of daily infection data for the country being analyzed.
    * @return
    *   An array of integers representing the daily number of infections.
    */
  def getDailyInfections: Array[Int] = il.getInfections.toArray.map(_.toInt)

  /** Returns an array of 7-day incidences of infection data per 100,000
    * people.
    * @return
    *   An array of doubles representing the 7-day incidences.
    */
  def get7di: Array[Double] = {
    val numDatapoints = getNumDatapoints
    val iso2 = il.getCountryISO2
    val population = pd.getPopulationForCode(cc.getCodeForISO2(iso2))
    val dailyInfections = getDailyInfections

    val code = cc.getCodeForISO2(iso2);

    (0 until numDatapoints).map { i =>
      val data7daySum = (i - 6 to i).filter(_ >= 0).map(dailyInfections).sum
      100000 * (data7daySum / population)
    }.toArray
  }
}

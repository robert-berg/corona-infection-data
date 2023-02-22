# data_analysis

The data_analysis package contains the implementation of the data analysis of the Covid19 pandemic.

## Usage

To use the data_analysis package, you must first import the necessary classes:

```
import data_analysis._
```

### CountryCode

To create a `CountryCode` object, simply provide the numeric code and ISO2 code as strings:

```
val usa = new CountryCode("840", "US")
```

### CountryCodes

To create a `CountryCodes` object, you must first have a Play Configuration object and the name of the file containing the country codes. Then, simply call the constructor with these parameters:


```
val cc = new CountryCodes(config, "country_codes.csv")
```

You can then use the `getCodeForISO2` and `getISO2ForCode` methods to retrieve the numeric country code or ISO2 code for a given ISO2 code or country code, respectively:


```
val code = cc.getCodeForISO2("US") // "840"
val iso2 = cc.getISO2ForCode("840") // "US"
```

### PopulationData

To create a `PopulationData` object, you must first have a Play Configuration object and the name of the file containing the population data. Then, simply call the constructor with these parameters:

```
val pd = new PopulationData(config, "population_data.csv")
```

You can then use the `getPopulationForCode` method to retrieve the population for a given country code:

```
val population = pd.getPopulationForCode("840")
```
### CountryIncidenceMap

To create a `CountryIncidenceMap` object, you must first have a Play Configuration object, the name of the file containing the incidence data, a `PopulationData` object, and a `CountryCodes` object. Then, simply call the constructor with these parameters:

```
val countryIncidenceMap = new CountryIncidenceMap(config, "incidence_data.csv", pd, cc)
```

You can then use the `getIso2ToCurrent7DayIncidenceMap` method to retrieve  an immutable map of ISO codes to the current 7-day incidence of new cases per 100,000 population:

```
val iso2ToCurrent7DayIncidenceMap = countryIncidenceMap.getIso2ToCurrent7DayIncidenceMap
```

## Contributors

Robert Berg - [GitHub](https://github.com/robert-berg)
# Controllers

This package contains the implementation of the controllers for the web application.

## HomeController.scala

This controller creates an `Action` to handle HTTP requests to the application's home page. It returns an HTML page defined in the `views.html.index()` view.

## PlotAnalysisController.scala

This controller creates an Action to handle HTTP requests to the plot analysis API. It uses the `data_analysis` module to perform data analysis on the WHO dataset and return the results as a JSON object.

The `plotAnalysis` function takes a country code as a parameter and returns a JSON object that contains the following fields:

`countryISO2`: the ISO2 code of the country
`numDatapoints`: the number of data points in the dataset
`dailyInfections`: an array of daily infection counts
`incidenceSevenDays`: an array of 7-day incidence rates

The `countryIncidenceMap` function returns a JSON object containing the current 7-day incidence rate for each country in the dataset.


## Contributors

Robert Berg - [GitHub](https://github.com/robert-berg)
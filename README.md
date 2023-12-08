# Corona Infection Data

This project is a web application written in Scala that analyzes official COVID-19 infections data from the World Health Organization.

## Live Demo

<a href="https://corona-infection-data.herokuapp.com/" 
target="_blank" >
    <button>Live demo</button>
</a>

## Screenshots

Worldmap

![/screenshots/worldmap.png](/screenshots/worldmap.png)


7 day incidence

![/screenshots/7di.png](/screenshots/7di.png)

Daily infections

![/screenshots/dailyInfections.png](/screenshots/dailyInfections.png)

## Project overview

**Data Analysis**  [/app/data_analysis/](/app/data_analysis/) The `data_analysis` package provides classes for analyzing Covid19 pandemic data, including `CountryCode`, `CountryCodes`, `PopulationData`, and `CountryIncidenceMap`, which allow users to retrieve information such as population, incidence rates, and ISO codes for different countries.

**Controllers** [/app/controllers/](/app/controllers/) The `controllers` package contains the implementation of controllers for the application, including `HomeController` which returns the application's home page as an HTML page, and `PlotAnalysisController` which analyzes WHO dataset using the `data_analysis` modules and returns the results as a JSON object.


**Views** [/app/views/](/app/views/) The Views folder contains views for the Corona Infection Data application, including `index.scala.html`, which displays corona infection data on a world map using JavaScript libraries `d3.js` and `topojson.js`.



## Running it locally

The project was developed with:


`java version: "19.0.1"`
`sbt version: "1.7.2"`
`sbt script version: "1.8.2"`


To check your Java version, enter the following command:

```bash
java -version
```

To check your sbt version, enter the following command:
```bash
sbt --version
```

If you do not have Java and sbt installed follow these links to obtain them:

* [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [sbt](http://www.scala-sbt.org/download.html)

### Build and run the project

This Play project was created from a seed template. It includes all Play components and an Akka HTTP server.

To build and run the project:

1. Use a command window to change into the project directory

2. Build the project. Enter: `sbt run`. The project builds and starts the embedded HTTP server. Since this downloads libraries and dependencies, the amount of time required depends partly on your connection's speed.

3. After the message `Server started, ...` displays, enter the following URL in a browser: <http://localhost:9000>


## Deployment

The root directory contains the `Procfile` for deploying the application on Heroku

/**
 * A class for retrieving country codes.
 */
const CountryCodes = new (class {
  /** @private */
  #data;

  /**
   * Creates a new CountryCodes instance.
   *
   * @throws {Error} If there was an error fetching the data.
   */
  constructor() {
    const loadDataPromise = fetch('/assets/json/cc.json').then((response) =>
      response.json()
    );

    loadDataPromise
      .then((data) => {
        this.#data = data;
      })
      .catch((error) => {
        console.error('Error:', error);
        throw new Error('Error fetching data');
      });

    this.#data = loadDataPromise;
  }

  /**
   * Converts a numeric country code to its ISO code.
   *
   * @param {number} numericCode - The numeric country code to convert.
   * @returns {string} The ISO code corresponding to the numeric code, or "Invalid code" if the code is not found.
   */
  async convertISOCode(numericCode) {
    await this.#data;
    return this.#data[numericCode] ? this.#data[numericCode] : 'Invalid code';
  }
})();

/**
 * A class for retrieving the country incidence map data.
 */
const CountryIncidenceMap = new (class {
  /** @private */
  #map;
  /** @private */
  #largestValue;
  /** @private */
  #loadDataPromise;

  /**
   * Creates a new CountryIncidenceMap instance and loads the data.
   */
  constructor() {
    this.#loadDataPromise = fetch('/country-incidence-map')
      .then((response) => response.json())
      .then((data) => {
        this.#map = new Map(Object.entries(data));
        this.#largestValue = Math.max(...this.#map.values());
      })
      .catch((error) => {
        console.error('Error:', error);
      });
  }

  /**
   * Returns a Promise that resolves when the data has been loaded.
   *
   * @returns {Promise<boolean>} A Promise that resolves to true when the data has been loaded.
   */
  async isLoaded() {
    await this.#loadDataPromise;
    return !!this.#map;
  }

  /**
   * Gets the incidence value for a country code.
   *
   * @param {string} countryCode - The country code to get the incidence value for.
   * @returns {number|undefined} The incidence value for the country code, or undefined if the data has not been loaded or the country code is not found.
   */
  async getValue(countryCode) {
    await this.#loadDataPromise;
    return this.#map.get(countryCode);
  }

  /**
   * Calculates the percentage of the largest incidence value that a given value represents.
   *
   * @param {number} value - The value to calculate the percentage for.
   * @returns {number|undefined} The percentage of the largest incidence value that the value represents, or undefined if the data has not been loaded.
   */
  async calculatePercentage(value) {
    await this.#loadDataPromise;
    return value / this.#largestValue;
  }
})();

// Select the map container and get its width and height
const mapContainer = document.querySelector('body > div.map-container');
const width = mapContainer.clientWidth;
const height = mapContainer.clientHeight;

// Select the SVG element and set its width and height to match the container
const svg = d3.select('svg').attr('width', width).attr('height', height);

// Append a group element for the markers
const markerGroup = svg.append('g');

// Define the projection to be a natural earth projection
const projection = d3.geoNaturalEarth1();

// Get the initial scale of the projection
const initialScale = projection.scale();

// Define the path generator to use the projection
const path = d3.geoPath().projection(projection);

// Define the center of the map as the center of the container
const center = [width / 2, height / 2];

// Draw the graticule lines on the map
drawGraticule();

// Draw the countries on the map
drawMap();

// Enable zooming on the map
enableZoom();

// Set the center of the projection to be the center of the container
projection.center();

// Generate a line graph for the 7-day incidence container
generateLineGraph(
  document.querySelector('.incidence-seven-days-container'),
  [],
  '7-day incidence'
);

// Generate a line graph for the daily infections container
generateLineGraph(
  document.querySelector('.daily-infections-container'),
  [],
  'Daily infections'
);

/**
Function to draw a map of the world with countries color-coded by incidence rate.
@async
@function drawMap
*/
async function drawMap() {
  // Load the world map data

  d3.json(
    'https://gist.githubusercontent.com/mbostock/4090846/raw/d534aba169207548a8a3d670c9c2cc719ff05c47/world-110m.json',
    async function (error, data) {
      let countries = await Promise.all(
        topojson
          .feature(data, data.objects.countries)
          .features.map(async (d) => ({
            ...d,
            color: rgba2rgbString({
              r: 70,
              g: 130,
              b: 180,
              a:
                (await CountryIncidenceMap.calculatePercentage(
                  await CountryIncidenceMap.getValue(
                    await CountryCodes.convertISOCode(d.id)
                  )
                )) * 3 || 0,
            }),
          }))
      );

      // Select all path elements and bind data
      svg
        .selectAll('.segment')
        .data(countries)
        .enter()
        .append('path')
        // Set class, path, and style for each element
        .attr('class', 'segment')
        .attr('d', path)
        .style('stroke', '#888')
        .style('stroke-width', '1px')
        .style('fill', (d) => d.color)
        .style('cursor', 'pointer')
        .on('click', async function (d) {
          const data = await fetchPlotData(
            await CountryCodes.convertISOCode(d.id)
          );
          document.querySelector('.sidebar-heading').innerText =
            'Infection data for ' + (await CountryCodes.convertISOCode(d.id));
          plotAnalysis(data);
        })
        .on('mouseover', function (d) {
          d3.select(this).style('fill', 'orange');
        })
        .on('mouseout', function (d) {
          d3.select(this).style('fill', (d) => d.color);
        });
    }
  );
}

/**
 * Enables zoom behavior on the SVG element and updates the projection
 * with the current zoom transform.
 *
 * @function
 * @returns*/
function enableZoom() {
  // Define the zoom behavior
  const zoom = d3
    .zoom()
    .scaleExtent([2, 10])
    .on('zoom', () => {
      // Update the projection's scale and translate based on the current zoom transform
      const transform = d3.event.transform;
      projection.scale(initialScale * transform.k);
      projection.translate([transform.x, transform.y]);

      // Redraw the map and graticule with the updated projection
      svg.selectAll('.segment').attr('d', path);
      svg.select('.graticule').attr('d', path);
    });

  // Set the initial zoom level of the map
  const initialTransform = d3.zoomIdentity.scale(3);
  svg.call(zoom.transform, initialTransform);

  // Apply the zoom behavior to the SVG element
  svg.call(zoom);
}

/**
 * Draws a graticule using D3.js.
 */
function drawGraticule() {
  // Define the graticule generator
  const graticule = d3.geoGraticule().step([10, 10]);

  // Append a path element for the graticule
  svg
    .append('path')
    .datum(graticule)
    .attr('class', 'graticule')
    .attr('d', path)
    .style('fill', '#fff')
    .style('stroke', '#ccc');
}

/**
 * Plot the incidence and daily infections line graphs.
 *
 * @param {Object} ia - The incidence analysis object containing data to plot.
 * @param {number[]} ia.incidenceSevenDays - An array of incidence data over the past 7 days.
 * @param {number[]} ia.dailyInfections - An array of daily infection data.
 * @returns {void}
 */
function plotAnalysis(ia) {
  /* Clearing the div with the class `incidence-seven-days-container` */
  document.querySelector('.incidence-seven-days-container').innerHTML = '';
  /* Clearing the div with the class `daily-infections-container` */
  document.querySelector('.daily-infections-container').innerHTML = '';

  generateLineGraph(
    document.querySelector('.incidence-seven-days-container'),
    ia.incidenceSevenDays,
    '7-day incidence'
  );

  generateLineGraph(
    document.querySelector('.daily-infections-container'),
    ia.dailyInfections,
    'Daily infections'
  );
  return;
}

/**
Fetches plot data for a given country code.
@async
@function
@param {string} countryCode - The country code to fetch the plot data for.
@returns {Promise} A Promise that resolves with the fetched data.
@throws {Error} Throws an error if the request fails.
*/
async function fetchPlotData(countryCode) {
  return fetch(`/plot/${countryCode}`)
    .then((response) => response.json())
    .then((data) => {
      return data;
    })
    .catch((error) => console.error(error));
}

/**

Generates a line graph using the D3.js library and appends it to a specified HTML element.
@param {string} htmlElement - The ID or class of the HTML element to append the graph to.
@param {Array<number>} numbers - An array of numbers to plot on the graph.
@param {string} text - The text to display as the legend for the line graph.
@return {void}
*/
function generateLineGraph(htmlElement, numbers, text) {
  // Set the dimensions and margins of the graph
  var margin = {top: 20, right: 30, bottom: 30, left: 50},
    originalWidth = 370,
    originalHeight = 274,
    windowHeight = window.innerHeight;

  // Calculate the scaling factor based on the window height
  var scalingFactor = windowHeight / 776;

  // Scale the chart height based on the scaling factor
  var height =
    Math.round(originalHeight * scalingFactor) - margin.top - margin.bottom;

  // Use the original width and apply the margins to get the actual width of the chart
  var width = originalWidth - margin.left - margin.right;

  // Create the SVG element
  var svg = d3
    .select(htmlElement)
    .append('svg')
    .attr('width', width + margin.left + margin.right)
    .attr('height', height + margin.top + margin.bottom)
    .append('g')
    .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

  // Define the x and y scales
  var x = d3
    .scaleLinear()
    .domain([0, numbers.length - 1])
    .range([0, width]);
  var y = d3
    .scaleLinear()
    .domain([d3.min(numbers), d3.max(numbers)])
    .range([height, 0]);

  // Define the x and y axis
  var xAxis = d3.axisBottom(x);
  var yAxis = d3.axisLeft(y);

  // Add the x axis
  svg
    .append('g')
    .attr('transform', 'translate(0,' + height + ')')
    .call(xAxis);

  // Add the y axis
  svg.append('g').call(yAxis);

  // Define the line function
  var line = d3
    .line()
    .x(function (d, i) {
      return x(i);
    })
    .y(function (d) {
      return y(d);
    });

  // Add the line to the graph for each value in the array
  svg
    .append('path')
    .datum(numbers)
    .attr('fill', 'none')
    .attr('stroke', 'steelblue')
    .attr('stroke-width', 1.5)
    .attr('d', line);

  // Add the legend for each line
  svg
    .append('text')
    .attr('x', width - 100)
    .attr('y', 10)
    .attr('fill', 'steelblue')
    .attr('opacity', 0.75)
    .text(text);
}

/**
 * Converts an RGBA color to an RGB color with a white background.
 *
 * @param {object} RGBA_color - An object representing an RGBA color, with properties `r`, `g`, `b`, and `a`.
 * @param {number} RGBA_color.r - The red component of the RGBA color, in the range 0-255.
 * @param {number} RGBA_color.g - The green component of the RGBA color, in the range 0-255.
 * @param {number} RGBA_color.b - The blue component of the RGBA color, in the range 0-255.
 * @param {number} RGBA_color.a - The alpha component of the RGBA color, in the range 0-1.
 *
 * @returns {string} A string representing an RGB color with a white background, in the format "rgb(r, g, b)".
 */
function rgba2rgbString(RGBA_color) {
  const RGB_background = {
    r: 255,
    g: 255,
    b: 255,
  };
  const alpha = RGBA_color.a;

  return `rgb(${(1 - alpha) * RGB_background.r + alpha * RGBA_color.r}, ${
    (1 - alpha) * RGB_background.g + alpha * RGBA_color.g
  }, ${(1 - alpha) * RGB_background.b + alpha * RGBA_color.b})`;
}

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

// Above function can be used parse Parameters in both single-movie.js and single-star.js

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");
    // jQuery the movie information element
    let movieInfoElement = jQuery("#movie_info");

    // Get the genres from the resultData
    let genreString = "";
    for (let i = 0; i < resultData[0]["movie_genre"].length; i++) {
        genreString += resultData[0]["movie_genre"][i];
        genreString += "; ";
    }
    genreString = genreString.slice(0, -2);

    let starString = "";
    for (let i = 0; i < resultData[0]["movie_star"].length; i++) {
        starString += '<a href="single-star.html?id='
            + resultData[0]['star_id_Array'][i]
            + '">'
            + resultData[0]["movie_star"][i]
            + "</a>; ";
    }
    starString = starString.slice(0, -2);

    // MODIFIED!!!
    movieInfoElement.append("<dt class=\"col-sm-3\">Movie Title" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["movie_title"] + "</dd>" +
        "<dt class=\"col-sm-3\">Release Year" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["movie_year"]  + "</dd>" +
        "<dt class=\"col-sm-3\">Director" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["movie_director"]  + "</dd>" +
        "<dt class=\"col-sm-3\">Genres" + "</dt>" + "<dd class=\"col-sm-9\">" + genreString  + "</dd>" +
        "<dt class=\"col-sm-3\">Stars" + "</dt>" + "<dd class=\"col-sm-9\">" + starString + "</dd>" +
        "<dt class=\"col-sm-3\">Rating" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["movie_rating"]  + "</dd>" );
        // "<p>Director: "     + resultData[0]["movie_director"] + "</p>" +
        // "<p>Genres: "       + genreString   + "</p>" +
        // "<p>Stars: "       + starString   + "</p>" +
        // "<p>Rating: "       + resultData[0]["movie_rating"]   + "</p>" );
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",
    method: "GET", // Setting request method GET
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by MoviesServlet in Movies.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});
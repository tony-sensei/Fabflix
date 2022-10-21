/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleStarResult(resultData) {
    console.log("handleStarResult: populating movieList table from resultData");
    // Populate the star table
    // Find the empty table body by id "movieList_table_body"
    let movieTableBodyElement = jQuery("#movieList_table_body");


    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Get the genres
        let genreArray = resultData[i]["movie_genre"].split(',');
        let genreString = "";
        for (let j = 0; j < Math.min(3, genreArray.length); j++) {
            genreString += genreArray[j];
            genreString += "; ";
        }
        genreString = genreString.slice(0, -2);

        // Get the stars
        let starArray = resultData[i]["movie_star"].split(',');
        let starIdArray = resultData[i]["movie_star_id"].split(',');
        let starString = "";
        for (let j = 0; j < Math.min(3, starArray.length); j++) {
            starString += '<a href="single-star.html?id='
                            + starIdArray[j]
                            + '">'
                            + starArray[j]
                            + "</a>; ";
        }
        starString = starString.slice(0, -2);


        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_title for the link text
            '</a>' +
            "</th>";

        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + genreString + "</th>";
        rowHTML += "<th>" + starString + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method GET
    url: "api/movieList", // Setting request url, which is mapped by MovieListServletServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
});
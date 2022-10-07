/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


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
        let genreString = "";
        for (let j = 0; j < Math.min(3, resultData[i]["movie_genre"].length); j++) {
            genreString += resultData[i]["movie_genre"][j];
            genreString += "; ";
        }

        let starString = "";
        for (let j = 0; j < Math.min(3, resultData[i]["movie_star"].length); j++) {
            starString += resultData[i]["movie_star"][j];
            starString += "; ";
        }


        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        // rowHTML +=
        //     "<th>" +
        //     // Add a link to single-star.html with id passed with GET url parameter
        //     '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
        //     + resultData[i]["star_name"] +     // display star_name for the link text
        //     '</a>' +
        //     "</th>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
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
    method: "GET", // Setting request method
    url: "api/movieList", // Setting request url, which is mapped by MovieListServletServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the MovieListServlet
});
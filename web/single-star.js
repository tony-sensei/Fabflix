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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    let movieBodyElement = jQuery("#movie_table_body");
    let rowHTML = "";
    for (let i = 0; i <resultData[0]["movie_year"].length; i++) {
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + resultData[0]['movie_id_Array'][i] + '">'
            + resultData[0]['movie_title'][i] +     // display movie_title for the link text
            '</a>' +
            "</th>";

        rowHTML += "<th>" + resultData[0]["movie_year"][i] + "</th>";
        rowHTML += "</tr>";
    }

    movieBodyElement.append(rowHTML);


    let starInfoElement = jQuery("#star_info");
    starInfoElement.append(
        "<dt class=\"col-sm-3\">Star Name"     + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["star_name"] + "</dd>" +
        "<dt class=\"col-sm-3\">Date Of Birth" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["star_dob"]  + "</dd>" +
        "<dt class=\"col-sm-3\">Movies"        + "</dt>");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method GET
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
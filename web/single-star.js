let title = getParameterByName("title");
let year = getParameterByName("year");
let director = getParameterByName("director");
let star = getParameterByName("star");
let genre = getParameterByName("genre");
let letter = getParameterByName("letter");
let page = getParameterByName("page");
let maxsize = getParameterByName("maxsize");
let titleSort = getParameterByName("titleSort");
let ratingSort = getParameterByName("ratingSort");
let firstSort = getParameterByName("firstSort");


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


//movieList jump
let jumpElement = jQuery("#movieListJump");
let rowHTML = "";
let url = "";

if (!genre && !letter) {
    url += "movie-list.html?title=" + title + "&year=" + year + "&director=" +
        director + "&star=" + star + "&page=" + page +
        "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
        ratingSort + "&firstSort=" + firstSort;
}

if (!letter) {
    url += "movie-list.html?genre=" + genre + "&page=" + page +
        "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
        ratingSort + "&firstSort=" + firstSort;
} else {
    url += "movie-list.html?letter=" + letter + "&page=" + page +
        "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
        ratingSort + "&firstSort=" + firstSort;
}
rowHTML += "<a class='nav-link ' href='" + url + "'>Movie Lists </a>";
// console.log(rowHTML);
jumpElement.append(rowHTML)


// Get id from URL
let starId = getParameterByName('id');

if (!genre && !letter) {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/single-star?id=" + starId + "&title=" + title + "&year=" + year +
            "&director=" + director + "&star=" + star + "&page=" +
            page + "&maxsize=" + maxsize + "&titleSort=" + titleSort +
            "&ratingSort=" + ratingSort + "&firstSort=" + firstSort,
        success: (resultData) => handleResult(resultData)
    });
} else {
    if (!letter) {
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/single-star?id=" + starId + "&genre=" + genre + "&page=" + page +
                "&maxsize=" + maxsize + "&titleSort=" + titleSort +
                "&ratingSort=" + ratingSort + "&firstSort=" + firstSort,
            success: (resultData) => handleResult(resultData)
        });
    } else {
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/single-star?id=" + starId + "&letter=" + letter + "&page=" + page +
                "&maxsize=" + maxsize + "&titleSort=" + titleSort +
                "&ratingSort=" + ratingSort + "&firstSort=" + firstSort,
            success: (resultData) => handleResult(resultData)
        });
    }
}

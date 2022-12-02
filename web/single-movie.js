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

// Above function can be used parse Parameters in both single-movie.js and single-star.js

function addToCart(movieId, movieTitle) {
    console.log("add to cart");
    $.ajax("api/shopping-cart-action",{
        method: "POST",
        data: { "movieId": movieId, "action": "add-to-cart", "movieTitle": movieTitle },
        success: (resultData) => handleResultData(resultData)
    });
}

function handleResultData(resultData) {
    console.log("handle cart response");
    console.log(resultData);
    if ( resultData["status"] === "success" ) {
        alert(resultData["message"]);
    } else {
        alert("fail to add to cart");
    }
}



function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");
    // jQuery the movie information element
    let movieInfoElement = jQuery("#movie_info");

    // Get the genres from the resultData
    let genreString = "";
    let genreList = [];
    for (let i = 0; i < resultData.length; i++) {
        let curGenre = resultData[i]['movie_genre'];
        if(!genreList.includes(curGenre))
            genreList.push(curGenre);
        else continue;
    }
    for (let i = 0; i < genreList.length; i++) {
        genreString += '<a href="movie-list.html?genre='
            + genreList[i]
            + '">'
            + genreList[i]
            + "</a>; ";
    }
    genreString = genreString.slice(0, -2);

    let starString = "";
    let starList = [];
    let starIdList = [];
    for (let i = 0; i < resultData.length; i++) {
        let curStar = resultData[i]['movie_star'];
        let curStarId = resultData[i]['movie_star_id'];
        if(!starList.includes(curStar)){
            starList.push(curStar);
            starIdList.push(curStarId);
        }
        else continue;
    }
    for (let i = 0; i < starList.length; i++) {
        let singleStarURL = "single-star.html?id=" + starIdList[i];
        if (!genre && !letter) {
            singleStarURL += "&title=" + title + "&year=" + year + "&director=" + director + "&star=" + star +
                "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                ratingSort + "&firstSort=" + firstSort;
        } else {
            if (!letter) {
                singleStarURL += "&genre=" + genre + "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                    ratingSort + "&firstSort=" + firstSort;
            } else {
                singleStarURL += "&letter=" + letter + "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                    ratingSort + "&firstSort=" + firstSort;
            }
        }
        starString += '<a href="'
            + singleStarURL
            + '">'
            + starList[i]
            + "</a>; ";
    }
    starString = starString.slice(0, -2);

    // if(resultData[i]["movie_rating"] != 0)  rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
    // else rowHTML += "<th>N/A</th>";

    let addingElem = "<dt class=\"col-sm-3\">Movie Title" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["movie_title"] + "</dd>" +
        "<dt class=\"col-sm-3\">Release Year" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["movie_year"]  + "</dd>" +
        "<dt class=\"col-sm-3\">Director" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["movie_director"]  + "</dd>" +
        "<dt class=\"col-sm-3\">Genres" + "</dt>" + "<dd class=\"col-sm-9\">" + genreString  + "</dd>" +
        "<dt class=\"col-sm-3\">Stars" + "</dt>" + "<dd class=\"col-sm-9\">" + starString + "</dd>"

    if(resultData[0]["movie_rating"] > 0)  addingElem += "<dt class=\"col-sm-3\">Rating" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["movie_rating"]  + "</dd>";
    else addingElem += "<dt class=\"col-sm-3\">Rating" + "</dt>" + "<dd class=\"col-sm-9\">N/A</dd>";

    addingElem += "<dt class=\"col-sm-3\"></dt><dd class=\"col-sm-9\"><button class=\"btn btn-outline-success my-2 my-sm-0 mr-sm-2 \" onclick=\"addToCart(\'" +
        resultData[0]['movie_id'] + "\', \'" + resultData[0]['movie_title'] + "\')\">Add to cart</button>";

    // MODIFIED!!!
    movieInfoElement.append(addingElem);

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


let movieId = getParameterByName('id');

if (!genre && !letter) {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/single-movie?id=" + movieId + "&title=" + title + "&year=" + year +
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
            url: "api/single-movie?id=" + movieId + "&genre=" + genre + "&page=" + page +
                "&maxsize=" + maxsize + "&titleSort=" + titleSort +
                "&ratingSort=" + ratingSort + "&firstSort=" + firstSort,
            success: (resultData) => handleResult(resultData)
        });
    } else {
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/single-movie?id=" + movieId + "&letter=" + letter + "&page=" + page +
                "&maxsize=" + maxsize + "&titleSort=" + titleSort +
                "&ratingSort=" + ratingSort + "&firstSort=" + firstSort,
            success: (resultData) => handleResult(resultData)
        });
    }
}
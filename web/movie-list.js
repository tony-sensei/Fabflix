let title = getParameter("title");
let year = getParameter("year");
let director = getParameter("director");
let star = getParameter("star");
let genre = getParameter("genre");
let letter = getParameter("letter");
let page = 0;
let maxsize = 25;
let titleSort = getParameter("titleSort");
let ratingSort = getParameter("ratingSort");
let firstSort = getParameter("firstSort")
let totalMovie = 0;
if(getParameter("page") != null)
    page = parseInt(getParameter("page"));
if(getParameter("maxsize") != null)
    maxsize = parseInt(getParameter("maxsize"));

function getParameter(target) {
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

function handleMovieResult(resultData) {
    console.log("handleStarResult: populating movieList table from resultData");
    let movieTableBodyElement = jQuery("#movieList_table_body");
    totalMovie = resultData[resultData.length-1]["total"];
    console.log(totalMovie);
    for (let i = 0; i < Math.min(maxsize, resultData.length); i++) {

        let genreArray = resultData[i]["movie_genres"];
        let genreString = "";
        for (let j = 0; j < Math.min(3, genreArray.length); j++) {
            genreString += '<a href="movie-list.html?genre='
                + genreArray[j]["genre_name"]
                + '">'
                + genreArray[j]["genre_name"]
                + "</a>; ";
        }
        genreString = genreString.slice(0, -2);


        // Get the stars
        let starArray = resultData[i]["movie_stars"];
        let starString = "";
        for (let j = 0; j < Math.min(3, starArray.length); j++) {
            let singleStarURL = "single-star.html?id=" + starArray[j]["star_id"];
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
            console.log(singleStarURL);

            starString += "<a href=" + singleStarURL + ">"
                            + starArray[j]["star_name"]
                            + "</a>; ";
        }
        starString = starString.slice(0, -2);


        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";

        let singleMovieURL = "single-movie.html?id=" + resultData[i]['movie_id'];
        if (!genre && !letter) {
            singleMovieURL += "&title=" + title + "&year=" + year + "&director=" + director + "&star=" + star +
                "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                ratingSort + "&firstSort=" + firstSort;
        } else {
            if (!letter) {
                singleMovieURL += "&genre=" + genre + "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                    ratingSort + "&firstSort=" + firstSort;
            } else {
                singleMovieURL += "&letter=" + letter + "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                    ratingSort + "&firstSort=" + firstSort;
            }
        }

        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            "<a href=" + singleMovieURL + ">"
            + resultData[i]["movie_title"] +     // display movie_title for the link text
            "</a>" +
            "</th>";

        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + genreString + "</th>";
        rowHTML += "<th>" + starString + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "<th><button class=\"btn btn-outline-success my-2 my-sm-0 mr-sm-2 \" onclick=\"addToCart(\'" +
            resultData[i]['movie_id'] + "\', \'" + resultData[i]['movie_title'] + "\')\">Add to cart</button>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }

    let preElement = jQuery("#pre");
    rowHTML = "";
    let url = "";
    let prePage = page-1;
    if (prePage >= 0) {
        if (!genre && !letter) {
            url += "movie-list.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star +
                "&page=" + prePage + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                ratingSort + "&firstSort=" + firstSort;
        } else {
            if (!letter) {
                url += "movie-list.html?genre=" + genre + "&page=" + prePage + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                    ratingSort + "&firstSort=" + firstSort;
            } else {
                url += "movie-list.html?letter=" + letter + "&page=" + prePage + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                    ratingSort + "&firstSort=" + firstSort;
            }
        }
        rowHTML += "<a href='" + url + "'>" + "prev" + "</a>";
        preElement.append(rowHTML)
    }


    let nextElement = jQuery("#next");
    rowHTML = "";
    url = "";
    let nextPage = page + 1;
    console.log("now:" + totalMovie);
    if (nextPage * maxsize <= totalMovie) {
        if (!genre && !letter) {
            url += "movie-list.html?title=" + title + "&year=" + year + "&director=" +
                director + "&star=" + star + "&page=" + nextPage +
                "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                ratingSort + "&firstSort=" + firstSort;
        } else {
            if (!letter) {
                url += "movie-list.html?genre=" + genre + "&page=" + nextPage +
                    "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                    ratingSort + "&firstSort=" + firstSort;
            } else {
                url += "movie-list.html?letter=" + letter + "&page=" + nextPage +
                    "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                    ratingSort + "&firstSort=" + firstSort;
            }
        }
        rowHTML += "<a href='" + url + "'>" + "next" + "</a>";
        nextElement.append(rowHTML)
    }

}

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


// maxsize select
let maxsizeElement = jQuery("#maxsize");
let choiceSize = [10, 25, 50, 100];
rowHTML = "";
for (let i = 0; i < choiceSize.length; i++) {
    let url = "";
    if (!genre && !letter) {
        url += "movie-list.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star +
            "&page=" + page + "&maxsize=" + choiceSize[i] + "&titleSort=" + titleSort + "&ratingSort=" +
            ratingSort + "&firstSort=" + firstSort;
    } else {
        if (!letter) {
            url += "movie-list.html?genre=" + genre + "&page=" + page + "&maxsize=" + choiceSize[i] + "&titleSort=" + titleSort + "&ratingSort=" +
                ratingSort + "&firstSort=" + firstSort;
        } else {
            url += "movie-list.html?letter=" + letter + "&page=" + page + "&maxsize=" + choiceSize[i] + "&titleSort=" + titleSort + "&ratingSort=" +
                ratingSort + "&firstSort=" + firstSort;
        }
    }
    rowHTML += "<option value='" + url + "'>" + choiceSize[i] + "</option>";
}
maxsizeElement.append(rowHTML);


// title sort order select
let titleSortElement = jQuery("#titleSort");
let choiceTitle = ["asc","desc"];
rowHTML = "";
for (let i = 0; i < choiceTitle.length; i++) {
    let url = "";
    if (!genre && !letter) {
        url += "movie-list.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star +
            "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + choiceTitle[i] + "&ratingSort=" +
            ratingSort + "&firstSort=" + firstSort;
    } else {
        if (!letter) {
            url += "movie-list.html?genre=" + genre + "&page=" + page + "&maxsize=" + maxsize +
                "&titleSort=" + choiceTitle[i] + "&ratingSort=" +
                ratingSort + "&firstSort=" + firstSort;
        } else {
            url += "movie-list.html?letter=" + letter + "&page=" + page + "&maxsize=" + maxsize +
                "&titleSort=" + choiceTitle[i] + "&ratingSort=" +
                ratingSort + "&firstSort=" + firstSort;
        }
    }
    rowHTML += "<option value='" + url + "'>" + choiceTitle[i] + "</option>";
}
titleSortElement.append(rowHTML)


// rating sort order select
let ratingSortElement = jQuery("#ratingSort");
let choiceRating = ["asc","desc"];
rowHTML = "";
for (let i = 0; i < choiceRating.length; i++) {
    let url = "";
    if (!genre && !letter) {
        url += "movie-list.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star +
            "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
            choiceRating[i] + "&firstSort=" + firstSort;
    } else {
        if (!letter) {
            url += "movie-list.html?genre=" + genre + "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                choiceRating[i] + "&firstSort=" + firstSort;
        } else {
            url += "movie-list.html?letter=" + letter + "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                choiceRating[i] + "&firstSort=" + firstSort;
        }
    }
    rowHTML += "<option value='" + url + "'>" + choiceRating[i] + "</option>";
}
ratingSortElement.append(rowHTML)


//select which one sort first
let firstSortElement = jQuery("#firstSort");
let choiceFirst = ["title", "rating"];
rowHTML = "";
for (let i = 0; i < choiceFirst.length; i++) {
    let url = "";
    if (!genre && !letter) {
        url += "movie-list.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star +
            "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
            ratingSort + "&firstSort=" + choiceFirst[i];
    } else {
        if (!letter) {
            url += "movie-list.html?genre=" + genre + "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                ratingSort + "&firstSort=" + choiceFirst[i];
        } else {
            url += "movie-list.html?letter=" + letter + "&page=" + page + "&maxsize=" + maxsize + "&titleSort=" + titleSort + "&ratingSort=" +
                ratingSort + "&firstSort=" + choiceFirst[i];
        }
    }
    rowHTML += "<option value='" + url + "'>" + choiceFirst[i] + "</option>";
}
firstSortElement.append(rowHTML)



if (!genre && !letter) {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "movie-list?title=" + title + "&year=" + year +
            "&director=" + director + "&star=" + star + "&page=" +
            page + "&maxsize=" + maxsize + "&titleSort=" + titleSort +
            "&ratingSort=" + ratingSort + "&firstSort=" + firstSort,
        success: (resultData) => handleMovieResult(resultData)
    });
} else {
    if (!letter) {
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "movie-list?genre=" + genre + "&page=" + page +
                "&maxsize=" + maxsize + "&titleSort=" + titleSort +
                "&ratingSort=" + ratingSort + "&firstSort=" + firstSort,
            success: (resultData) => handleMovieResult(resultData)
        });
    } else {
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "movie-list?letter=" + letter + "&page=" + page +
                "&maxsize=" + maxsize + "&titleSort=" + titleSort +
                "&ratingSort=" + ratingSort + "&firstSort=" + firstSort,
            success: (resultData) => handleMovieResult(resultData)
        });
    }
}
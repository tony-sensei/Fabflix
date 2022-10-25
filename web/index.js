let search = jQuery("#search");

function handleSearchResult(resultDataString) {
    console.log("result data string");
    let resultDataJson = JSON.parse(resultDataString);
    let url = "";
    url += "movie-list.html?title=" + resultDataJson["title"] +
        "&year=" + resultDataJson["year"] +
        "&director=" + resultDataJson["director"] +
        "&star=" + resultDataJson["star"] +
        "&page=0&maxsize=25&titleSort=desc&ratingSort=desc&firstSort=rating";
    window.location.replace(url);
}

function submitSearchForm(formSubmitEvent) {
    console.log("Search form");

    formSubmitEvent.preventDefault();

    jQuery.ajax(
        "api/main", {
            method: "post",
            data: search.serialize(),
            success: resultDataString => {
                handleSearchResult(resultDataString);
            }
        }
    );
}


function handleBrowseResult(resultData) {
    console.log("handling BrowseResult");
    let genreListElement = jQuery("#genreList");
    let genreListString = "";
    for (let i = 0; i < resultData.length; i++) {
        genreListString += "<a href='movie-list.html?genre=" +
            resultData[i] +
            "&page=0&maxsize=25&titleSort=desc&ratingSort=desc&firstSort=rating'>" +
            resultData[i] +
            "</a>; ";
    }

    genreListElement.append(genreListString);

    let letter = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "*"];
    let alphabet = jQuery("#alphabet");
    let alphaListString = "";
    for (let i = 0; i < letter.length; i++) {
        alphaListString += "<a href='movie-list.html?letter=" +
            letter[i] +
            "&page=0&maxsize=25&titleSort=desc&ratingSort=desc&firstSort=rating'>" +
            letter[i] +
            "</a>; ";
    }

    alphabet.append(alphaListString);
}

jQuery.ajax({
    dataType: "json",
    method: "get",
    url: "api/main",
    success: (resultData) => handleBrowseResult(resultData)
});

search.submit(submitSearchForm);

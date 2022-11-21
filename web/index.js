let search = jQuery("#search");
var searchedTitle = {};

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

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here
    if (query in searchedTitle) {
        doneCallback({"suggestions": searchedTitle[query]})
        console.log("cached from front-end")
        return;
    }
    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "auto-complete?auto=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here
    searchedTitle[query] = jsonData;
    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    // console.log("you select " + suggestion["title"] + " with ID " + suggestion["id"])
    let url = "";
    url += "single-movie.html?id=" + suggestion["data"]["movieId"];

    window.location.href = url;
}


$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    minChars: 3,
    // set delay time
    deferRequestBy: 300,

    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});

function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    search.submit(submitSearchForm);
}

$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})


jQuery.ajax({
    dataType: "json",
    method: "get",
    url: "api/main",
    success: (resultData) => handleBrowseResult(resultData)
});

search.submit(submitSearchForm);

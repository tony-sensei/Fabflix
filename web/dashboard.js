
let login_form = $("#login_form");
let add_star_form = $("#add_star_form");
let add_movie_form = $("#add_movie_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        ////////////////////////
        window.location.replace("dashboard.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginPost(formSubmitEvent) {
    console.log("submit employee login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    // submit form to api/_dashboard
    $.ajax(
        "api/_dashboard", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            success: handleLoginResult
        }
    )
}

function handleAddStarResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log("handle login response");
    console.log(resultDataJson);
    if (resultDataJson["status"] === "success") {
        $("#add_star_error_message").text(resultDataJson["message"] + " new star id = " + resultDataJson["starId"]);
    } else {
        console.log("show add star error message");
        console.log(resultDataJson["message"]);
        $("#add_star_error_message").text(resultDataJson["message"]);
    }
}

function submitAddStarPost(formSubmitEvent) {
    console.log("submit add star form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/_dashboard", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_star_form.serialize(),
            success: handleAddStarResult
        }
    )
}

function handleAddMovieResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log("handle add movie response");
    console.log(resultDataJson);
    if (resultDataJson["status"] === "success") {
        $("#add_movie_error_message").text(resultDataJson["message"]);
    } else {
        console.log("show add movie error message");
        console.log(resultDataJson["message"]);
        $("#add_movie_error_message").text(resultDataJson["message"]);
    }
}

function submitAddMoviePost(formSubmitEvent) {
    console.log("submit add movie form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/_dashboard", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_movie_form.serialize(),
            success: handleAddMovieResult
        }
    )
}

function handleGetResult(resultDataJson) {
    // let resultDataJson = JSON.parse(resultData);
    console.log("handle get page response");
    console.log(resultDataJson);
    if (resultDataJson["status"] === "success") {
        console.log(resultDataJson["message"]);
        console.log(resultDataJson["tables_metadata"]);
        // set elements visible
        let login_div = document.getElementById("login_div");
        login_div.style.display = "none";
        let add_star_div  = document.getElementById("add_star_div");
        let add_movie_div = document.getElementById("add_movie_div");
        let metadata_div  = document.getElementById("metadata_div");
        add_star_div.style.visibility  = "visible";
        add_movie_div.style.visibility = "visible";
        metadata_div.style.visibility  = "visible";
        // add_star_div.setAttribute("hidden", false);
        // if (hidden) {
        //     add_star_div.removeAttribute("hidden");
        //     add_movie_div.removeAttribute("hidden");
        //     metadata_div.removeAttribute("hidden");
        // }
        let tableInfoArray = resultDataJson["tables_metadata"];
        let metadataElement   = $("#metadata_div");
        for (let i = 0; i < tableInfoArray.length; i++) {
            let tableName = tableInfoArray[i]["tableName"];
            metadataElement.append(
                "<dl className=\"row\" id=\"table" + i + "\">" +
                "<dt class=\"col-sm-3\">" + tableInfoArray[i]["tableName"] + "</dt>"
            );
            let columnInfoArray = tableInfoArray[i]["columnInfo"];
            for (let i = 0; i < columnInfoArray.length; i++) {
                metadataElement.append(
                    "<dt class=\"col-sm-3\">" + columnInfoArray[i]["columnName"] +
                    "</dt><dd class=\"col-sm-9\">" + columnInfoArray[i]["columnType"] + "</dd>"
                );
            }
        }
        metadataElement.append("</dl>");
    } else {
        console.log("show add movie error message");
        console.log(resultDataJson["message"]);
        // $("#add_movie_error_message").text(resultDataJson["message"]);
    }
}

$.ajax(
    "api/_dashboard", {
        dataType: "json",
        method: "GET",
        success: resultData => handleGetResult(resultData)
    }
)

// Bind the submit action of the form to a handler function
login_form.submit(submitLoginPost);
add_star_form.submit(submitAddStarPost);
add_movie_form.submit(submitAddMoviePost);
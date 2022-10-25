/**
 * functions dealing with quantity of each movie item
 * @param movieId
 */
function deleteFromCart(movieId) {
    console.log("delete it");
    $.ajax("api/shopping-cart-action", {
        method: "POST",
        data: { "movieId": movieId, "action": "deletion" },
        success: window.location.reload()
    });
}

function addToCart(movieId, movieTitle) {
    console.log("add to cart");
    $.ajax("api/shopping-cart-action",{
        method: "POST",
        data: { "movieId": movieId, "action": "add-to-cart", "movieTitle": movieTitle },
        success: window.location.reload()
    });
}

function addQuantity(movieId) {
    console.log("add by 1");
    $.ajax("api/shopping-cart-action",{
        method: "POST",
        data: { "movieId": movieId, "action": "addition"},
        success: (resultData) => handleResultData(resultData)
    });
}

function subtractQuantity(movieId) {
    console.log("subtract by 1");
    let formElementString = "#change-quantity-form-" + movieId;
    let currentQuantity = $(formElementString).children("label").text();
    console.log(currentQuantity);
    if ( parseInt(currentQuantity, 10) <= 1 ) {
        alert("Invalid Quantity");
        return;
    }
    $.ajax("api/shopping-cart-action",{
        method: "POST",
        data: { "movieId": movieId, "action": "subtraction" },
        success: (resultData) => handleResultData(resultData)
    });
}

function handleResultData(resultData) {
    console.log("handle cart response");
    console.log(resultData);
    let formElementString = "#change-quantity-form-" + resultData["movieId"];
    let formElement = $(formElementString);
    let originalTotalPrice = $("#total-price-info").children("dd").text();
    let originalQuantity = formElement.children("label").text();
    // calculate total price
    if ( resultData["quantity"] > originalQuantity ) {
        // console.log(originalTotalPrice);
        // console.log(resultData["quantity"]);
        // console.log(originalQuantity);
        // console.log(resultData["price"]);
        let currentPrice = parseInt(originalTotalPrice) + (resultData["quantity"] - originalQuantity) * resultData["price"];
        console.log(currentPrice);
        $("#total-price-info").children("dd").text(currentPrice);
    } else {
        let currentPrice = parseInt(originalTotalPrice) - (originalQuantity - resultData["quantity"]) * resultData["price"];
        console.log(currentPrice);
        $("#total-price-info").children("dd").text(currentPrice);
    }
    formElement.children("label").text(resultData["quantity"]);

}

function submitFormGet(formSubmitEvent) {
    console.log("submit get form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    $.ajax("api/shopping-cart-action", {
        method: "GET",
        success: (resultData) => handleResultData(resultData)
    });
}

// let formElements = $("form");
// // Bind the submit action of the form to a handler function
// formElements.each(function () {
//     $(this).submit(submitFormGet);
// })
let formElement = $("#change-quantity-form-1");
formElement.submit(submitFormGet);

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult(resultData) {

    let sessionInfoElement = $("#session_info");

    sessionInfoElement.append(
        "<dt class=\"col-sm-3\">Welcome" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData["username"] + "</dd>" +
        "<dt class=\"col-sm-3\">SessionID" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData["sessionId"] + "</dd>" +
        "<dt class=\"col-sm-3\">Creation Time" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData["creationTime"] + "</dd>" +
        "<dt class=\"col-sm-3\">Last Access Time" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData["lastAccessTime"] + "</dd>"
    )
    // populate items into table
    let itemsInfoElement = $("#items_table_body");
    let totalPrice = 0;
    for (let i = 0; i < resultData['items'].length; i++) {
        let price = parseInt(resultData['items'][i]['quantity'], 10)*resultData['items'][i]['price'];
        console.log(price);
        totalPrice = totalPrice + price;
        let rowHtml = "";
        rowHtml += "<tr>";
        rowHtml += "<th>" +
            '<a href="single-movie.html?id=' + resultData['items'][i]['movieId'] + '">'
            + resultData['items'][i]["movieTitle"] +     // display movie_title for the link text
            '</a>' +
            "</th>";
        // ============================
        rowHtml += '<th>' +
            '<form class="form-inline my-2 my-lg-0" id="change-quantity-form-' + resultData["items"][i]["movieId"] +
            '" method="GET">' +
            '<button class="btn btn-outline-success my-2 my-sm-0 mr-sm-2 " type="button" onclick="subtractQuantity(\'' + resultData["items"][i]["movieId"] +
            '\')">-</button>' +
            '<label class="form-control mr-sm-2" >' + resultData["items"][i]["quantity"] +
            '</label>' +
            '<button class="btn btn-outline-success my-2 my-sm-0 mr-sm-2" type="button" onclick="addQuantity(\'' + resultData["items"][i]["movieId"] +
            '\')">+</button>' +
            '<button class="btn btn-outline-success my-2 my-sm-0 " type="button" onclick="deleteFromCart(\'' + resultData["items"][i]["movieId"] +
            '\')">delete</button>' +
            // '<input className="form-control mr-sm-2" type="text" name="name" placeholder="' + resultData["items"][i]["quantity"] + '">' +
            // '<button class="btn btn-outline-success my-2 my-sm-0 " type="submit">Change</button>' +
            '</form>' +
            '</th>';
        rowHtml += '<th>' +
            '$' + resultData["items"][i]["price"] +
            '</th>';
        rowHtml += "</tr>";

        itemsInfoElement.append(rowHtml);
    }
    $("#total-price-info").children("dd").text(totalPrice);

    // let movieString = "";
    // for (let i = 0; i < resultData[0]["movie_title"].length; i++) {
    //     movieString += '<a href="single-movie.html?id='
    //         + resultData[0]['movie_id_Array'][i]
    //         + '">'
    //         + resultData[0]["movie_title"][i]
    //         + "</a>; ";
    // }
    // movieString = movieString.slice(0, -2);
    //
    // // append two html <p> created to the h3 body, which will refresh the page
    // starInfoElement.append(
    //     "<dt class=\"col-sm-3\">Star Name" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["star_name"] + "</dd>" +
    //     "<dt class=\"col-sm-3\">Date Of Birth" + "</dt>" + "<dd class=\"col-sm-9\">" + resultData[0]["star_dob"] + "</dd>" +
    //     "<dt class=\"col-sm-3\">Movies" + "</dt>" + "<dd class=\"col-sm-9\">" + movieString + "</dd>");
}

$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/shopping-cart",
    success: (resultData) => handleResult(resultData)
});
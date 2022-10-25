let checkout_form = $("#checkout_form");

function handleResult(resultDataString) {
    let resultData = JSON.parse(resultDataString);
    console.log("handle login response");
    console.log(resultData);

    if ( resultData["status"] === "success" ) {
        // remove the checkout form
        $("#checkout_form").empty();
        // populate items into table
        let orderInfoHeadElement = $("#order_info_table_head");
        orderInfoHeadElement.append("<tr>\n" +
            "        <th>Sale ID </th>\n" +
            "        <th>Movie </th>\n" +
            "        <th>Quantity </th>\n" +
            "      </tr>"
        );
        let orderInfoElement = $("#order_info_table_body");
        let totalPrice = 0;
        for (let i = 0; i < resultData['items'].length; i++) {
            let price = parseInt(resultData['items'][i]['quantity'], 10)*resultData['items'][i]['price'];
            console.log(price);
            totalPrice = totalPrice + price;
            let rowHtml = "";
            rowHtml += "<tr>";
            rowHtml += "<th>" + resultData['items'][i]['saleId'] + "</th>"
            rowHtml += "<th>" +
                '<a href="single-movie.html?id=' + resultData['items'][i]['movieId'] + '">'
                + resultData['items'][i]["movieTitle"] +     // display movie_title for the link text
                '</a>' +
                "</th>";
            rowHtml += '<th>' +
                '<label class="form-control mr-sm-2" >' + resultData['items'][i]['quantity'] + '</label>' +
                '</th>';
            rowHtml += "</tr>";
            orderInfoElement.append(rowHtml);
        }
        $("#total-price-info").children("dt").text("Total Price");
        $("#total-price-info").children("dd").text(totalPrice);
        alert("Thank you for your payment!");
        // window.location.replace("index.html+");

    } else {
        // If payment fails, the web page will display
        console.log("show error message");
        // console.log(resultData["message"]);
        alert(resultData["message"]);
        // $("#payment_error_info").text(resultData["message"]);
    }
}
/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPost(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    jQuery.ajax(
        "api/checkout", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: checkout_form.serialize(),
            success: (resultData) => handleResult(resultData)
        }
    )
}

// Bind the submit action of the form to a handler function
checkout_form.submit(submitPost);

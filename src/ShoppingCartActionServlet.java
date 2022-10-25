import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * ShoppingCartAction: no interaction with db
 */
@WebServlet(name = "ShoppingCartActionServlet", urlPatterns = "/api/shopping-cart-action")
public class ShoppingCartActionServlet extends HttpServlet {
    private static final long serialVersionUID = 10L;

//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        // Get a instance of current session on the request
//        HttpSession session = request.getSession();
//        // Get parameters
//        String movieId = request.getParameter("movieId");
//        String quantity = request.getParameter("quantity");
//        System.out.println(movieId);
//        System.out.println(quantity);
//
//        JsonObject responseJsonObject = new JsonObject();
//
//        User user = (User) session.getAttribute("user");
//        String sessionId = session.getId();
//        Date creationTime = new Date(session.getCreationTime());
//        Date lastAccessTime = new Date(session.getLastAccessedTime());
//        String username = user.getUsername();
//
//        // set response data
//        responseJsonObject.addProperty("username", username);
//        responseJsonObject.addProperty("sessionId", sessionId);
//        responseJsonObject.addProperty("creationTime", creationTime.toString());
//        responseJsonObject.addProperty("lastAccessTime", lastAccessTime.toString());
//
//        response.setContentType("application/json");
//        PrintWriter out = response.getWriter();
//        out.write(responseJsonObject.toString());
//
//        out.close();
//    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get a instance of current session on the request
        HttpSession session = request.getSession();
        // Get parameters
        String movieId = request.getParameter("movieId");
        System.out.println(movieId);
        String action = request.getParameter("action");
        System.out.println(action);
        // used when add new movie (could be null)
        String movieTitle = request.getParameter("movieTitle");
        System.out.println(movieTitle);

        // Retrieve data named "previousItems" from session
        // which is an arraylist of Item class
        ArrayList<Item> previousItems = (ArrayList<Item>) session.getAttribute("previousItems");
        if (previousItems == null) {
            // Add the newly created ArrayList to session, so that it could be retrieved next time
            previousItems = new ArrayList<>();
            session.setAttribute("previousItems", previousItems);
        }

        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");


        // find movieId
        Item currItem = (Item) previousItems.stream().filter(item -> movieId.equals(item.getId()))
                .findFirst()
                .orElse(null);
        if ( currItem == null ) {
            // if not newly added
            if ( !action.equals("add-to-cart") ) {
                System.out.println("currItem is null???");
                return;
            }
            Item newItem = new Item(movieId, movieTitle);
            previousItems.add(newItem);
            // remember to change currItem
            currItem = (Item) previousItems.stream().filter(item -> movieId.equals(item.getId()))
                    .findFirst()
                    .orElse(null);
        }
        switch ( action ) {
            case "addition":
                currItem.addQuantity();
                break;
            case "subtraction":
                currItem.subQuantity();
                break;
            case "deletion":
                previousItems.remove(currItem);
            case "add-to-cart":
                // here means add by 1
                currItem.addQuantity();
        }
        // get parameters of all movie items
        int movieQuantity = currItem.getQuantity();
        int moviePrice = currItem.getPrice();
        System.out.println(movieQuantity);
        System.out.println(moviePrice);


        // set up response object body
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("status", "success");
        responseJsonObject.addProperty("message", "successfully add to cart");

        User user = (User) session.getAttribute("user");
        String sessionId = session.getId();
        Date creationTime = new Date(session.getCreationTime());
        Date lastAccessTime = new Date(session.getLastAccessedTime());
        String username = user.getUsername();

        // set response data
        responseJsonObject.addProperty("username", username);
        responseJsonObject.addProperty("sessionId", sessionId);
        responseJsonObject.addProperty("creationTime", creationTime.toString());
        responseJsonObject.addProperty("lastAccessTime", lastAccessTime.toString());


        //make up
        responseJsonObject.addProperty("movieId", movieId);
        responseJsonObject.addProperty("quantity", movieQuantity);
        responseJsonObject.addProperty("price", moviePrice);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(responseJsonObject.toString());

        out.close();
    }
}

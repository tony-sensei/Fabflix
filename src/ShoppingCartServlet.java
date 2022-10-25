import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import java.util.Date;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get a instance of current session on the request
        HttpSession session = request.getSession();

        // Retrieve data named "previousItems" from session
        // which is an arraylist of Item class
        ArrayList<Item> previousItems = (ArrayList<Item>) session.getAttribute("previousItems");

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems
        // ArrayList for the user
        if (previousItems == null) {
            // Add the newly created ArrayList to session, so that it could be retrieved next time
            previousItems = new ArrayList<>();
            session.setAttribute("previousItems", previousItems);
        }

        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");

        // add items to json response
        JsonObject responseJsonObject = new JsonObject();
        JsonArray responseItemList = new JsonArray();
        for (Item item : previousItems) {
            JsonObject singleItem = new JsonObject();
            singleItem.addProperty("movieId", item.getId());
            singleItem.addProperty("movieTitle", item.getName());
            singleItem.addProperty("quantity", item.getQuantity());
            singleItem.addProperty("price", item.getPrice());
            responseItemList.add(singleItem);
        }
        responseJsonObject.add("items", responseItemList);
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

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(responseJsonObject.toString());

        out.close();

//        // get newItem parameter
//        String newItem = request.getParameter("newItem"); // Get parameter that sent by GET request url
//        // // set html response // //
//        response.setContentType("text/html");
//        PrintWriter out = response.getWriter();
//        String title = "Items Purchased";
//
//        out.println(String.format("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
//                "<html>\n" +
//                "   <head>" +
//                "   <title>%s</title>" +
//                "   </head>\n" +
//                "   <body bgcolor=\"#FDF5E6\">\n" +
//                "       <h1>%s</h1>", title, title));
//
//        // In order to prevent multiple clients, requests from altering previousItems ArrayList at the same time, we
//        // lock the ArrayList while updating
//        synchronized (previousItems) {
//            if (newItem != null) {
//                previousItems.add(newItem); // Add the new item to the previousItems ArrayList
//            }
//
//            // Display the current previousItems ArrayList
//            if (previousItems.size() == 0) {
//                out.println("<i>No items</i>");
//            } else {
//                out.println("<ul>");
//                for (String previousItem : previousItems) {
//                    out.println("<li>" + previousItem);
//                }
//                out.println("</ul>");
//            }
//        }
//
//        out.println("</body></html>");
    }

}

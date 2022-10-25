import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;


@WebServlet(name = "CreditCardServlet", urlPatterns = "/api/checkout")
public class CreditCardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DataSource dataSource;

    // Set up movie db connection
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/movieDB");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cardId = request.getParameter("cardNumber");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String expireDate = request.getParameter("expireDate");

        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM creditcards WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cardId);
            ResultSet res = statement.executeQuery();

            if ( !res.next() ) {
                // card ID not exists
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("check card id failed");
                responseJsonObject.addProperty("message", "invalid card number " + cardId);
                out.write(responseJsonObject.toString());
                res.close();
                statement.close();
                return;
            }
            // check if other information correct
            String cardFirstName = res.getString("firstName");
            String cardLastName = res.getString("lastName");
            String cardExpireDate = res.getString("expiration");
            if (    cardFirstName.equals(firstName) &&
                    cardLastName.equals(lastName)   &&
                    cardExpireDate.equals(expireDate))
            {
                System.out.println("valid credit card!!!");
                // other information matches
                // access session only when success
                HttpSession session = request.getSession();
                // get current date
                long millis = System.currentTimeMillis();
                Date saleDate = new Date(millis);
                User user = (User) session.getAttribute("user");
                int userId = Integer.parseInt(user.getId());
                ArrayList<Item> allItems = (ArrayList<Item>) session.getAttribute("previousItems");

                // create a jsonArray to store sold items info
                JsonArray soldItemsArray = new JsonArray();
                for (Item item : allItems) {
                    // movieId (itemId) is a string
                    String itemId = item.getId();
                    int quantity = item.getQuantity();
                    String insertQuery = "INSERT INTO sales(customerId, movieId, saleDate, quantity) VALUES(?, ?, ?, ?)";
                    PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                    insertStatement.setInt(1, userId);
                    insertStatement.setString(2, itemId);
                    insertStatement.setString(3, saleDate.toString());
                    insertStatement.setInt(4, quantity);
                    int row = insertStatement.executeUpdate();
                    // handle exception
                    JsonObject soldItemInfo = new JsonObject();
                    soldItemInfo.addProperty("movieId", itemId);
                    soldItemInfo.addProperty("movieTitle", item.getName());
                    soldItemInfo.addProperty("quantity", quantity);
                    soldItemInfo.addProperty("price", item.getPrice());
                    insertStatement = conn.prepareStatement("SELECT LAST_INSERT_ID() as id");
                    ResultSet resId = insertStatement.executeQuery();
                    if ( resId.next() ) {
                        int saleId = resId.getInt("id");
                        soldItemInfo.addProperty("saleId", saleId);
                    }
                    // add to jsonArray
                    soldItemsArray.add(soldItemInfo);
                    resId.close();
                    insertStatement.close();
                }

                responseJsonObject.add("items", soldItemsArray);
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                out.write(responseJsonObject.toString());
                res.close();
                statement.close();
                return;
            }
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("check card info failed");
            responseJsonObject.addProperty("message", "invalid card information");
            out.write(responseJsonObject.toString());
            res.close();
            statement.close();

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);

        } finally {
            out.close();
        }
    }
}

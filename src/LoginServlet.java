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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
/*
    org.jasypt StrongPasswordEncryptor was imported for password encryption (11/07/2022)
    see maven for dependency details
 */
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 6L;
    // Create a dataSource which registered in web.xml
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

        // set response
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        // get parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String userAgent = request.getHeader("User-Agent");

        // check if android user
        if (userAgent.contains("Android")) {
            request.getServletContext().log("User-Agent: " + userAgent);
            System.out.println("User-Agent: " + userAgent);

        } else {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

            // verify recaptcha
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "recaptcha verification error");
                // Log error to localhost log
                request.getServletContext().log("Error:", e);
                out.write(responseJsonObject.toString());
                out.close();
                return;
            }
        }
        // successfully verify recaptcha OR Android users //

        // Establish the db connection
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM customers as c " +
                    "where c.email = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            ResultSet res = statement.executeQuery();

            // check if username equals to email in db
            // assume email (username) is unique
            if (!res.next()) {
                // username not exists
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                out.write(responseJsonObject.toString());
                res.close();
                statement.close();
                return;
            }
            // get userId for future use
            String userId = res.getString("id");
            /*
                check if password correct (add password encryption in 11/07/2022)
             */
            // String userPassWord = res.getString("password");
            String encryptedPassword = res.getString("password");
            boolean success = false;
            // use the same encryptor to compare the user input password with encrypted password stored in DB
            success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            if (!success) {
                // wrong password
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "incorrect password");
                out.write(responseJsonObject.toString());
                res.close();
                statement.close();
                return;
            }
            /*
                login success, set this user into the session
             */
            request.getSession().setAttribute("user", new User(username, userId));
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");
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
//        if (username.equals("anteater") && password.equals("123456")) {
//
//            // set this user into the session
//            request.getSession().setAttribute("user", new User(username));
//            responseJsonObject.addProperty("status", "success");
//            responseJsonObject.addProperty("message", "success");
//
//        } else {
//            // Login fail
//            responseJsonObject.addProperty("status", "fail");
//            // Log to localhost log
//            request.getServletContext().log("Login failed");
//            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
//            if (!username.equals("anteater")) {
//                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
//            } else {
//                responseJsonObject.addProperty("message", "incorrect password");
//            }
//        }
//        response.getWriter().write(responseJsonObject.toString());
    }
}
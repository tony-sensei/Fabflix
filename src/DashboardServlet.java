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
import java.sql.*;
/*
    org.jasypt StrongPasswordEncryptor was imported for password encryption (11/07/2022)
    see maven for dependency details
 */
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/_dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // set response
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        HttpSession session = request.getSession();
        Employee curr_employee = (Employee) session.getAttribute("employee");
        if ( curr_employee != null ) {
            try ( Connection conn = dataSource.getConnection() ) {
                DatabaseMetaData md = conn.getMetaData();
                ResultSet tableResultSet = md.getTables(conn.getCatalog(), null, "%", new String [] {"TABLE"});
                JsonArray tableInfoArray = new JsonArray();
                while (tableResultSet.next()) {
                    JsonObject tableInfoObject = new JsonObject();
                    String tableName = tableResultSet.getString(3);
                    ResultSet columnResultSet = md.getColumns(conn.getCatalog(), null, tableName, "%");
                    System.out.println("Table " + tableName + ": ");
                    JsonArray columnInfoArray = new JsonArray();
                    while (columnResultSet.next()) {
                        JsonObject columnInfo = new JsonObject();
                        String columnName = columnResultSet.getString(4);
                        String columnType = columnResultSet.getString(6);
                        System.out.print(columnName + " has type: ");
                        System.out.println(columnType);
                        columnInfo.addProperty("columnName", columnName);
                        columnInfo.addProperty("columnType", columnType);
                        columnInfoArray.add(columnInfo);
                    }
                    tableInfoObject.addProperty("tableName", tableName);
                    tableInfoObject.add("columnInfo", columnInfoArray);
                    tableInfoArray.add(tableInfoObject);
                }
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "Get employee login session");
                responseJsonObject.add("tables_metadata", tableInfoArray);
                out.write(responseJsonObject.toString());
                out.close();
                return;

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

        responseJsonObject.addProperty("status", "fail");
        responseJsonObject.addProperty("message", "employee not login");
        out.write(responseJsonObject.toString());
        out.close();
        /*
            Get metadata from database
         */
//        try ( Connection conn = dataSource.getConnection() ) {
//            DatabaseMetaData md = conn.getMetaData();
//        } catch (Exception e) {
//            // Write error message JSON object to output
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("errorMessage", e.getMessage());
//            out.write(jsonObject.toString());
//            // Log error to localhost log
//            request.getServletContext().log("Error:", e);
//            // Set response status to 500 (Internal Server Error)
//            response.setStatus(500);
//        } finally {
//            out.close();
//        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // set response
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        // get action
        String action = request.getParameter("submit_action");
        try ( Connection conn = dataSource.getConnection() ) {
            switch ( action ) {
                case "employee_login":
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");
                    String query = "SELECT * FROM employees WHERE email = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, username);
                    ResultSet res = stmt.executeQuery();
                    if ( !res.next() ) {
                        // username not exists
                        responseJsonObject.addProperty("status", "fail");
                        // Log to localhost log
                        request.getServletContext().log("Employee login failed");
                        responseJsonObject.addProperty("message", "employee " + username + " doesn't exist");
                        out.write(responseJsonObject.toString());
                        res.close();
                        stmt.close();
                        return;
                    }
                    // check if password correct
                    String fullname = res.getString("fullname");
                    // For Test use: PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                    // String newPassword = passwordEncryptor.encryptPassword(password);
                    // String new2Password = passwordEncryptor.encryptPassword("classta");
                    // System.out.println(passwordEncryptor.checkPassword(password, newPassword));
                    // System.out.println(passwordEncryptor.checkPassword("classta", newPassword));
                    String encryptedPassword = res.getString("password");
                    boolean success = false;
                    // use the same encryptor to compare the user input password with encrypted password stored in DB
                    success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                    if ( !success ) {
                        // wrong password
                        responseJsonObject.addProperty("status", "fail");
                        // Log to localhost log
                        request.getServletContext().log("Employee login failed");
                        responseJsonObject.addProperty("message", "incorrect password");
                        out.write(responseJsonObject.toString());
                        res.close();
                        stmt.close();
                        return;
                    }
                    // login success
                    request.getSession().setAttribute("employee", new Employee(username, fullname));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "employee login success");
                    out.write(responseJsonObject.toString());
                    res.close();
                    stmt.close();
                    break;
                case "employee_add_star":
                    String starName = request.getParameter("starName");
                    String starBirthYear = request.getParameter("starBirthYear");
                    CallableStatement c_stmt = conn.prepareCall("{ CALL add_star(?, ?, ?) }");
                    c_stmt.setString(1, starName);
                    c_stmt.setInt(2, "".equals(starBirthYear) ? -1 : Integer.parseInt(starBirthYear) );
                    // register out parameters
                    c_stmt.registerOutParameter(3, Types.VARCHAR);
                    c_stmt.execute();
                    /*
                        Since add star will be always be successful,
                        we do not check if the returned starId is null here
                     */
                    String retStarId = c_stmt.getString(3);
                    // login success
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "employee add star success");
                    responseJsonObject.addProperty("starId", retStarId);
                    out.write(responseJsonObject.toString());
                    c_stmt.close();
                    break;
                case "employee_add_movie":
                    String movieTitle    = request.getParameter("movieTitle");
                    String movieYear     = request.getParameter("movieYear");
                    String movieDirector = request.getParameter("movieDirector");
                    String movieStar     = request.getParameter("movieStar");
                    String movieGenre    = request.getParameter("movieGenre");
                    CallableStatement add_movie_stmt = conn.prepareCall("{ CALL add_movie(?, ?, ?, ?, ?) }");
                    add_movie_stmt.setString(1, movieTitle);
                    add_movie_stmt.setInt(2, Integer.parseInt(movieYear));
                    add_movie_stmt.setString(3, movieDirector);
                    add_movie_stmt.setString(4, movieStar);
                    add_movie_stmt.setString(5, movieGenre);
                    boolean hadResults = add_movie_stmt.execute();
                    if ( !hadResults ) {
                        System.out.println("Why not hadResults???");
                    }
                    ResultSet retVal = (ResultSet) add_movie_stmt.getResultSet();
                    if (retVal.next()) {
                        String sp_ret_msg = retVal.getString("answer");
                        if ( sp_ret_msg.equals("movie repeat error") ) {
                            responseJsonObject.addProperty("status", "fail");
                            responseJsonObject.addProperty("message", sp_ret_msg);
                            responseJsonObject.addProperty("sp_ret_msg", sp_ret_msg);
                            out.write(responseJsonObject.toString());
                            retVal.close();
                            add_movie_stmt.close();
                            return;
                        }
                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "employee add movie success");
                        responseJsonObject.addProperty("sp_ret_msg", sp_ret_msg);
                        out.write(responseJsonObject.toString());
                        retVal.close();
                        add_movie_stmt.close();
                    } else {
                        System.out.println("Why retVal is null???");
                    }
                    break;
            }
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

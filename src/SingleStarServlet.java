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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/movieDB");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "WITH StarsOtherMovies AS (" +
                            "SELECT " +
                                "stars_in_movies.movieId, " +
                                "movies.title, " +
                                "movies.year " +
                            "FROM stars_in_movies " +
                            "JOIN movies ON (stars_in_movies.movieId = movies.Id) " +
                            "WHERE starId = ?) " +
                            "SELECT " +
                                "stars.id AS starID, " +
                                "stars.name AS starName, " +
                                "stars.birthYear, " +
                                "StarsOtherMovies.year, " +
                                "StarsOtherMovies.movieId, " +
                                "StarsOtherMovies.title " +
                            "FROM stars " +
                            "JOIN stars_in_movies ON (stars.id = stars_in_movies.starId) " +
                            "JOIN StarsOtherMovies ON ( StarsOtherMovies.movieId = stars_in_movies.movieId) " +
                            "WHERE stars.id = ? " +
                            "ORDER BY year DESC, title ASC; ";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);
            statement.setString(2, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // Create a new jsonArray
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String starId = rs.getString("starID");
                String starName = rs.getString("starName");
                String starDob = rs.getString("birthYear") == null ? "N/A" : rs.getString("birthYear");
                String movieYear = rs.getString("year");
                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", starId);
                jsonObject.addProperty("star_name", starName);
                jsonObject.addProperty("star_dob", starDob);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_year", movieYear);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

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
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
import java.sql.Statement;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;

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

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String query = "WITH StarsOtherMovies AS (" +
                            "SELECT " +
                                "stars_in_movies.starId, " +
                                "COUNT(*) AS movieCount " +
                            "FROM stars_in_movies " +
                            "WHERE movieId = ? " +
                            "GROUP BY 1) " +
                            "SELECT " +
                                "movies.id AS movieID, " +
                                "movies.title, " +
                                "movies.year, " +
                                "movies.director, " +
                                "genres.name AS genreName, " +
                                "stars.id AS starID, " +
                                "stars.name AS starName, " +
                                "ratings.rating " +
                            "FROM movies " +
                            "JOIN stars_in_movies ON (movies.id = stars_in_movies.movieId) " +
                            "JOIN stars ON (stars.id = stars_in_movies.starId) " +
                            "JOIN ratings ON (ratings.movieId = stars_in_movies.movieId) " +
                            "JOIN genres_in_movies ON ( movies.id = genres_in_movies.movieId) " +
                            "JOIN genres ON (genres.id = genres_in_movies.genreId) " +
                            "JOIN StarsOtherMovies ON ( StarsOtherMovies.starId = stars_in_movies.starId) " +
                            "WHERE movies.id = ? " +
                            "ORDER BY movieCount DESC, starName ASC, genreName ASC";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            statement.setString(2, id);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {

                String movieId = rs.getString("movieID");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieGenre = rs.getString("genreName");
                String movieStarId = rs.getString("starID");
                String movieStarName = rs.getString("starName");
                String movieRating = rs.getString("rating");



                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_star", movieStarName);
                jsonObject.addProperty("movie_star_id", movieStarId);
                jsonObject.addProperty("movie_genre", movieGenre);
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_rating", movieRating);
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

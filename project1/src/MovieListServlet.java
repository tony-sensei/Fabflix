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
import java.sql.ResultSet;
import java.sql.Statement;


@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movieList")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/movieDB");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Declare our statement
            Statement statementMovie = conn.createStatement();

            // Construct a query to get the movie info and id (with descending rate order)
            String queryMovie = "SELECT m.id, m.title, m.year, m.director, r.rating " +
                                "FROM movies m, ratings r " +
                                "WHERE m.id = r.movieId " +
                                "ORDER BY r.rating DESC " +
                                "LIMIT 20";

            // Perform the movie query (without genre and star)
            ResultSet rsM = statementMovie.executeQuery(queryMovie);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rsM
            while (rsM.next()) {

                String movieId = rsM.getString("id");
                String movieTitle = rsM.getString("title");
                String movieYear = rsM.getString("year");
                String movieDirector = rsM.getString("director");
                String movieRating = rsM.getString("rating");

                // Declare our statement
                Statement statementGenre = conn.createStatement();

                // Construct a query to get the genres by using id
                String queryGenre = "SELECT DISTINCT g.name " +
                        "FROM movies m, genres_in_movies gim, genres g " +
                        "WHERE gim.movieId = '" + movieId + "' AND gim.genreId = g.id";

                // Perform the genre query
                ResultSet rsG = statementGenre.executeQuery(queryGenre);


                JsonArray jsonArrayG = new JsonArray();
                // Iterate through each row of rsG
                while (rsG.next()) {
                    String genreName = rsG.getString("name");
                    jsonArrayG.add(genreName);
                }
                rsG.close();
                statementGenre.close();


                // Construct a query to get the stars by using id
                Statement statementStar = conn.createStatement();

                String queryStar = "SELECT DISTINCT s.id, s.name " +
                        "FROM movies m, stars_in_movies sim, stars s " +
                        "WHERE sim.movieId = '" + movieId + "' AND sim.starId = s.id";

                // Perform the star query
                ResultSet rsS = statementStar.executeQuery(queryStar);

                JsonArray jsonArrayS = new JsonArray();
                // Iterate through each row of rsS
                while (rsS.next()) {
                    String starName = rsS.getString("name");
                    jsonArrayS.add(starName);
                }
                rsS.close();
                statementStar.close();


                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.add("movie_genre", jsonArrayG);
                jsonObject.add("movie_star", jsonArrayS);
                jsonObject.addProperty("movie_rating", movieRating);
                jsonArray.add(jsonObject);
            }
            rsM.close();
            statementMovie.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

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

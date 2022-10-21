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
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            Statement statementMovie = conn.createStatement();
            String queryMovie = "WITH Top20Ratings AS (" +
                                "SELECT movieId, rating " +
                                "FROM ratings " +
                                "ORDER BY rating DESC " +
                                "LIMIT 20) " +
                                "SELECT title, year, director, M.id as movieId, rating, " +
                                "GROUP_CONCAT(DISTINCT G.name SEPARATOR ',') as genres, " +
                                "GROUP_CONCAT(DISTINCT S.name SEPARATOR ',') as stars, " +
                                "GROUP_CONCAT(DISTINCT S.id SEPARATOR ',') as star_ids " +
                                "FROM Top20Ratings AS R, movies as M, stars as S, " +
                                "stars_in_movies as SM, genres as G, genres_in_movies as GM " +
                                "WHERE M.id = R.movieId AND M.id = SM.movieId " +
                                "AND S.id = SM.starId AND GM.movieId = M.id " +
                                "AND GM.genreId = G.id " +
                                "GROUP BY M.title " +
                                "ORDER BY R.rating DESC " +
                                "LIMIT 20;";

            ResultSet rsM = statementMovie.executeQuery(queryMovie);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rsM
            while (rsM.next()) {
                String movieId = rsM.getString("movieId");
                String movieTitle = rsM.getString("title");
                String movieYear = rsM.getString("year");
                String movieDirector = rsM.getString("director");
                String movieGenres = rsM.getString("genres");
                String movieStars = rsM.getString("stars");
                String movieStarIds = rsM.getString("star_ids");
                String movieRating = rsM.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_genre", movieGenres);
                jsonObject.addProperty("movie_star", movieStars);
                jsonObject.addProperty("movie_star_id", movieStarIds);
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

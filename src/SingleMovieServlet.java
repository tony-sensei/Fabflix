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
            String query = "SELECT sim.movieId, title, year, director, sim.starId, s.name as sname, birthYear, rating " +
                    "from stars as s, stars_in_movies as sim, movies as m, ratings as r " +
                    "where m.id = sim.movieId and sim.starId = s.id and m.id = r.movieId and m.id = ?";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {

                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieRating = rs.getString("rating");

                String queryGenre = "SELECT DISTINCT name " +
                        "FROM movies as m, genres as g, genres_in_movies as gim " +
                        "WHERE gim.genreId = g.id and gim.movieId = ?";


                PreparedStatement statementGenre = conn.prepareStatement(queryGenre);
                statementGenre.setString(1, movieId);
                ResultSet resultGenre = statementGenre.executeQuery();

                JsonArray genreArray = new JsonArray();

                while (resultGenre.next()) {
                    String genreName = resultGenre.getString("name");
                    genreArray.add(genreName);
                }
                resultGenre.close();
                statementGenre.close();


                // Construct a query to get the stars by using id
                String queryStar = "SELECT DISTINCT s.id, s.name " +
                        "FROM movies as m, stars as s, stars_in_movies as sim " +
                        "WHERE sim.starId = s.id and sim.movieId = ? " +
                        " order by (select count(*) from stars_in_movies as sim2 " +
                        "where sim2.starId = s.id) desc, s.name asc ";

                // Declare the statement
                PreparedStatement statementStar = conn.prepareStatement(queryStar);

                // Change the parameter
                statementStar.setString(1, movieId);


                // Execute the query
                ResultSet rsS = statementStar.executeQuery();

                JsonArray jsonArrayS = new JsonArray();
                JsonArray jsonArraySI = new JsonArray();

                // Iterate through each row of rsS
                while (rsS.next()) {
                    String starId = rsS.getString("id");
                    String starName = rsS.getString("name");
                    jsonArrayS.add(starName);
                    jsonArraySI.add(starId);
                }
                rsS.close();
                statementStar.close();


                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.add("movie_star", jsonArrayS);
                jsonObject.add("star_id_Array", jsonArraySI);
                jsonObject.add("movie_genre", genreArray);
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

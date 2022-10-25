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

import static java.lang.Integer.parseInt;


@WebServlet(name = "MovieListServlet", urlPatterns = "/movie-list")
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

        String title = request.getParameter("title");

        String year = request.getParameter("year");

        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");

        String letter = request.getParameter("letter");
        String page = request.getParameter("page");

        String maxsize = request.getParameter("maxsize");

        String titleSort = request.getParameter("titleSort");
        String ratingSort = request.getParameter("ratingSort");
        String firstSort = request.getParameter("firstSort");

        int offset = parseInt(page) * parseInt(maxsize);

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String queryTempTable;
            String mainQuery = "SELECT title, year, director, M.id as movieId, rating " +
                    "FROM tempTable AS T, movies as M, stars as S, " +
                    "stars_in_movies as SIM, genres as G, genres_in_movies as GIM " +
                    "WHERE M.id = T.movieId AND M.id = SIM.movieId " +
                    "AND S.id = SIM.starId AND GIM.movieId = M.id " +
                    "AND GIM.genreId = G.id ";


            if (genre == null && letter == null) {
                if (!title.equals("null"))
                    mainQuery += "AND title LIKE '%" + title + "%' ";
                if (!year.equals("null"))
                    mainQuery += "AND year = " + year + " ";
                if (!director.equals("null"))
                    mainQuery += "AND director LIKE '%" + director + "%' ";
                if (!star.equals("null"))
                    mainQuery += "AND S.name LIKE '%" + star + "%' ";
            } else {
                if (letter == null) {
                    mainQuery += "AND G.name = '" + genre + "' ";
                } else {
                    if (letter.equals("*")) {
                        mainQuery += "AND title REGEXP '^[^a-z0-9A-z]'";
                    } else {
                        mainQuery += "AND title LIKE '" + letter + "%' ";
                    }
                }
            }

            mainQuery += "GROUP BY M.title ";

            //sort and sort order
            if(firstSort.equals("title")) {
                queryTempTable = "WITH tempTable AS (" +
                        "SELECT movieId, title as tempTitle, rating  " +
                        "FROM movies, ratings " +
                        "WHERE movies.id = ratings.movieId ";
                if(titleSort.equals("asc")){
                    queryTempTable += "order by title asc ";
                    if(ratingSort.equals("asc"))
                        mainQuery += "order by M.title asc, T.rating asc ";
                    else
                        mainQuery += "order by M.title asc, T.rating desc ";
                }
                else{
                    queryTempTable += "order by title desc ";
                    if(ratingSort.equals("asc"))
                        mainQuery += "order by M.title desc, T.rating asc ";
                    else
                        mainQuery += "order by M.title desc, T.rating desc ";
                }
            } else {
                queryTempTable = "WITH tempTable AS (" +
                        "SELECT movieId, rating " +
                        "FROM ratings ";
                if(ratingSort.equals("asc")){
                    queryTempTable += "order by rating asc ";
                    if(titleSort.equals("asc"))
                        mainQuery += "order by T.rating asc, M.title asc ";
                    else
                        mainQuery += "order by T.rating asc, M.title desc ";
                }
                else{
                    queryTempTable += "order by rating desc ";
                    if(titleSort.equals("asc"))
                        mainQuery += "order by T.rating desc, M.title asc ";
                    else
                        mainQuery += "order by T.rating desc, M.title desc ";
                }
            }


            //count the total number of movies
            Statement statement = conn.createStatement();
            String countQuery = "SELECT COUNT(*) as totalMovie FROM movies";
            ResultSet r = statement.executeQuery(countQuery);
            r.next();
            String totalMovie = r.getString("totalMovie");
            r.close();
            statement.close();



            //pagination
            mainQuery += " limit " + maxsize + " offset " + offset + "; ";
            queryTempTable += ") ";
//            queryTempTable += " limit " + maxsize + " offset " + offset + ") ";
            mainQuery = queryTempTable + mainQuery;


            Statement statementMovie = conn.createStatement();
            ResultSet rs = statementMovie.executeQuery(mainQuery);

            JsonArray jsonArray = new JsonArray();


            while (rs.next()) {
                String movie_id = rs.getString("movieId");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                String query2 = "select G.id, G.name " +
                        "from genres_in_movies as GIM, genres as G " +
                        "where G.id = GIM.genreId " +
                        "and GIM.movieId = '" + movie_id +
                        "' order by G.name " +
                        "LIMIT 3";

                Statement statement2 = conn.createStatement();
                ResultSet rs2 = statement2.executeQuery(query2);
                JsonArray movieGenres = new JsonArray();


                while (rs2.next()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("genre_id", rs2.getString("id"));
                    obj.addProperty("genre_name", rs2.getString("name"));
                    movieGenres.add(obj);
                }


                statement2.close();
                rs2.close();


                String query3 = "select S.id, S.name " +
                        "from stars_in_movies as SIM, stars as S " +
                        "where S.id = SIM.starId " +
                        "and SIM.movieId = '"+ movie_id +
                        "' order by (select count(*) from stars_in_movies as SIM2 " +
                        "where SIM2.starId = S.id) desc, S.name asc " +
                        "LIMIT 3";

                Statement statement3 = conn.createStatement();
                ResultSet rs3 = statement3.executeQuery(query3);
                JsonArray movieStars = new JsonArray();
                while (rs3.next()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("star_id", rs3.getString("id"));
                    obj.addProperty("star_name", rs3.getString("name"));
                    movieStars.add(obj);
                }
                statement3.close();
                rs3.close();


                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.add("movie_genres", movieGenres);
                jsonObject.add("movie_stars", movieStars);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("total", totalMovie);
            jsonArray.add(obj);
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

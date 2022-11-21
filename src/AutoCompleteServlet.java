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

@WebServlet("/auto-complete")
public class AutoCompleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/movieDB");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public AutoCompleteServlet() {
        super();
    }

    private static JsonObject generateJsonObject(String movieId, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try (Connection conn = dataSource.getConnection()) {

            JsonArray movieAuto = new JsonArray();

            String auto = request.getParameter("auto");

            // return the empty json array if query is null or empty
            if (auto == null || auto.trim().isEmpty() || auto.length() < 3) {
                response.getWriter().write(movieAuto.toString());
                return;
            }



            String query1 = "select distinct id, title from movies " +
                    "where MATCH(title) AGAINST (? IN BOOLEAN MODE) " +
                    "OR title LIKE ? " +
//                    "OR soundex(title) = soundex(?) " +
                    "OR edth(title, ?, ?) " +
                    "limit 10";

            PreparedStatement statementAuto = conn.prepareStatement(query1);

            String queryParam = "";
            String[] splitAuto = auto.split("\\s+");
            for(String s: splitAuto) queryParam += "+" + s + "* ";
            queryParam = queryParam.substring(0, queryParam.length()-1);
            statementAuto.setString(1, queryParam);
            statementAuto.setString(2, "%" + auto + "%");
            statementAuto.setString(3, auto);
//            uncomment when library extended
            int adjustNum;
            adjustNum = (int) auto.length() / 3 + 1;
            statementAuto.setInt(4, adjustNum);
            System.out.println(adjustNum);

            ResultSet rs = statementAuto.executeQuery();
            while (rs.next()) {
                movieAuto.add(generateJsonObject(rs.getString("id"), rs.getString("title")));
            }

            statementAuto.close();
            rs.close();

            response.getWriter().write(movieAuto.toString());
            return;

        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

}

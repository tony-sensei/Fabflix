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
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "MainServlet", urlPatterns = "/api/main")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 8L;
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

//        HttpSession session = request.getSession();
//
//        if(session.getAttribute("item") != null){
//            session.setAttribute("item", null);
//        }
//
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String query = "select name from genres";

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                jsonArray.add(rs.getString("name"));
            }
            rs.close();
            statement.close();

            out.write(jsonArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            request.getServletContext().log("Error:", e);
            response.setStatus(500);
        } finally {
            out.close();
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = "null";
        String year = "null";
        String director = "null";
        String star = "null";
        System.out.println("haveresponse");
        JsonObject resJsonObject = new JsonObject();
        if (!request.getParameter("title").equals(""))
            title = request.getParameter("title");
        if (!request.getParameter("year").equals(""))
            year = request.getParameter("year");
        if (!request.getParameter("director").equals(""))
            director = request.getParameter("director");
        if (!request.getParameter("star").equals(""))
            star = request.getParameter("star");

        resJsonObject.addProperty("title", title);
        resJsonObject.addProperty("year", year);
        resJsonObject.addProperty("director", director);
        resJsonObject.addProperty("star", star);

        response.getWriter().write(resJsonObject.toString());
        System.out.println(response.toString());
    }
}
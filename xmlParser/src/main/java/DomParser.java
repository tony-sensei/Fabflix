package main.java;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DomParser {

//    List<Movie> movies = new ArrayList<>();
//    Set<String> genres = new HashSet<>();
//    List<Actor> actors = new ArrayList<>();
    private List<GenresInMovies> gims = new ArrayList<>();
    private List<StarsInMovies> sims = new ArrayList<>();
    private int gimCount = 0;
    private int simCount = 0;

    private static Connection conn;

    private Document domActor;
    private Document domCast;
    private Document domMain;

//    public static void main(String[] args) throws Exception {
//        try {
//            String myUrl = "jdbc:mysql://localhost:3306/movieDB";
//            Class.forName("com.mysql.jdbc.Driver");
//            conn = DriverManager.getConnection(myUrl, "mytestuser", "CS122Bupup!");
//            // parse the xml file and get the dom object
//            parseXmlFile();
//
//            parseDocument();
//
//            // iterate through the list and print the data
//            printData();
//
//            handleInsertion();
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        System.out.println("finish");
//    }
    public void runExample() throws Exception {
        String myUrl = "jdbc:mysql://localhost:3306/movieDB";
        String loginUser = "root";
        String loginPasswd = "CS122Bupup!";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection(myUrl, loginUser, loginPasswd);
        // parse the xml file and get the dom object
        parseXmlFile();
        parseDocument();
        // iterate through the list and print the data
        printData();
        handleInsertion();
//        try {
//            String myUrl = "jdbc:mysql://localhost:3306/movieDB";
//            String loginUser = "mytestuser";
//            String loginPasswd = "CS122Bupup!";
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            conn = DriverManager.getConnection(myUrl, loginUser, loginPasswd);
//            // parse the xml file and get the dom object
//            parseXmlFile();
//
//            parseDocument();
//
//            // iterate through the list and print the data
//            printData();
//
//            handleInsertion();
//        } catch (Exception e) {
//            System.out.println(e);
//        }
        System.out.println("finished");
    }

    /**
     * parseXmlFile (called by run function)
     */
    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            domActor = documentBuilder.parse("actors63.xml");
            domCast = documentBuilder.parse("casts124.xml");
            domMain = documentBuilder.parse("mains243.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentActorElement = domActor.getDocumentElement();
        Element documentCastElement = domCast.getDocumentElement();
        Element documentMainElement = domMain.getDocumentElement();

        NodeList nodeActorList = documentActorElement.getElementsByTagName("actor");
        NodeList nodeDFCastList = documentCastElement.getElementsByTagName("dirfilms");
        NodeList nodeDFMainList = documentMainElement.getElementsByTagName("directorfilms");

        int movieCount = 0;

        // main.xml
        if (nodeDFMainList != null) {
            for (int i = 0; i < nodeDFMainList.getLength(); i++) {

//            for (int i = 0; i < 3; i++) {

                // get the directorFilms element
                Element dfElement = (Element) nodeDFMainList.item(i);

                // get the director element
                Element directorElement = (Element) dfElement.getElementsByTagName("director").item(0);
                String dirname = parseDirector(directorElement);
//                System.out.println(dirname);

                // get the Films element
                Element filmsElement = (Element) dfElement.getElementsByTagName("films").item(0);
                NodeList nodeFilmsList = filmsElement.getElementsByTagName("film");
//                System.out.println(nodeFilmsList.getLength());

                if (nodeFilmsList != null) {
                    for (int j = 0; j < nodeFilmsList.getLength(); j++) {
                        Element singleFilmElement = (Element) nodeFilmsList.item(j);
                        Movie movie = parseMovie(singleFilmElement, dirname);
                        movieCount++;
                        if(movieCount % 1000 == 0) System.out.println("movie inserted: " + movieCount);
                        if(movie.getDirector() != null && movie.getYear() != -1 && movie.getDirector() != null) {
                            String movieId = movie.getId();
                            try {
                                CallableStatement insertMovieStatement = conn.prepareCall(" {CALL add_movie4(?, ?, ?, ?)}");
                                insertMovieStatement.setString(1, movie.getId());
                                insertMovieStatement.setString(2, movie.getTitle());
                                insertMovieStatement.setInt(3, movie.getYear());
                                insertMovieStatement.setString(4, movie.getDirector());
                                insertMovieStatement.registerOutParameter(1, Types.VARCHAR);
                                insertMovieStatement.execute();
                                // returned movieId by calling stored procedures
                                movieId = insertMovieStatement.getString(1);
                                insertMovieStatement.close();
                            } catch (Exception e) {
                                System.out.println(e);
                            }


                            Element catsElement = (Element) singleFilmElement.getElementsByTagName("cats").item(0);
                            NodeList nodeCatList = null;
                            if (catsElement != null) nodeCatList = catsElement.getElementsByTagName("cat");
                            if (nodeCatList != null) {
                                for (int k = 0; k < nodeCatList.getLength(); k++) {
                                    Element singleCatElement = (Element) nodeCatList.item(k);
                                    try {
                                        GenresInMovies gim = parseGIM(singleCatElement, movieId);
                                        if(singleCatElement != null) gims.add(gim);
                                    } catch (Exception gimE) {
                                        System.out.println("Error for GIM when movieId:" + movieId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //cast.xml
        if (nodeDFCastList != null) {
            for (int i = 0; i < nodeDFCastList.getLength(); i++) {
//            for (int i = 0; i < 3; i++) {
                Element dfElement = (Element) nodeDFCastList.item(i);
                NodeList filmcList = dfElement.getElementsByTagName("filmc");
                String movieId = null;
                for (int j = 0; j < filmcList.getLength(); j++) {
                    Element mElement = (Element) filmcList.item(j);
                    if(movieId == null) movieId = getTextValue(mElement, "f");
                    StarsInMovies sim = parseSIM(mElement, movieId);
                    if(sim.getStageName() != null) sims.add(sim);
                }
            }
        }

        //actor.xml
        if (nodeActorList != null) {
                for (int i = 0; i < nodeActorList.getLength(); i++) {
//                for (int i = 0; i < 3; i++) {
                    Element actorElement = (Element) nodeActorList.item(i);
                    Actor actor = parseActor(actorElement);
                    try {
                        if (actor.getStageName() != null) {
                            CallableStatement insertActorStatement = conn.prepareCall(" {CALL add_star(?, ?, ?)}");
                            insertActorStatement.setString(1, actor.getStageName());
                            insertActorStatement.setInt(2, actor.getDob());
                            insertActorStatement.registerOutParameter(3, Types.VARCHAR);
                            insertActorStatement.execute();
                            // String last_inserted_starId = insertActorStatement.getString(3);
                            // System.out.println("finished inserting star id = " + last_inserted_starId);
                            insertActorStatement.close();
                        }
                    }catch (Exception e) {
                        System.out.println(e);
                    }
                }
        }
    }

    private Actor parseActor(Element element) {
        String stageName = getTextValue(element, "stagename");
        int dob = getIntValue(element, "dob");
        return new Actor(stageName, dob);
    }

    private StarsInMovies parseSIM(Element element, String movieId) {
        String stageName = getTextValue(element, "a");
        return new StarsInMovies(movieId, stageName);
    }

    private GenresInMovies parseGIM(Element element, String movieId) {
            String genreName = element.getFirstChild().getNodeValue();
            int genreId = -1;
            if(genreName != null) {
                try {
                    String findGenreQuery = "SELECT id, name " +
                                            "FROM genres " +
                                            "WHERE MATCH (name) AGAINST (? IN boolean MODE) " +
                                            "OR soundex(name) like soundex(?);";
                    PreparedStatement findGenreStatement = conn.prepareStatement(findGenreQuery);
                    findGenreStatement.setString(1, genreName + '*');
                    findGenreStatement.setString(2, genreName);
                    ResultSet resId = findGenreStatement.executeQuery();


                    if(resId.next()) {
                        genreId = resId.getInt("id");
                        return new GenresInMovies(movieId, genreId);
                    } else {
                        String insertGenreQuery = "INSERT INTO genres(name) VALUES(?)";
                        PreparedStatement insertGenreStatement = conn.prepareStatement(insertGenreQuery);
                        insertGenreStatement.setString(1, genreName);
                        insertGenreStatement.execute();
                        insertGenreStatement.close();

                        String getGenreQuery = "SELECT LAST_INSERT_ID()";
                        PreparedStatement getGenreStatement = conn.prepareStatement(getGenreQuery);
                        ResultSet resId2 = getGenreStatement.executeQuery();
                        resId2.next();
                        genreId = resId2.getInt(1);

                        resId2.close();
                        getGenreStatement.close();
                    }

                    resId.close();
                    findGenreStatement.close();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            return new GenresInMovies(movieId, genreId);
    }

    private String parseDirector(Element element) {
        String name = getTextValue(element, "dirname");
        return name;
    }


    private Movie parseMovie(Element element, String dirname) {

        String title = getTextValue(element, "t");
        String id = getTextValue(element, "fid");
        int year = getIntValue(element, "year");

        // create a new Employee with the value read from the xml nodes
        return new Movie(title, id, year, dirname);
    }

    /**
     * It takes an XML element and the tag name, look for the tag and get
     * the text content
     * i.e for <Employee><Name>John</Name></Employee> xml snippet if
     * the Element points to employee node and tagName is name it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            if(nodeList.item(0).getFirstChild() == null) return textVal;
            textVal = nodeList.item(0).getFirstChild().getNodeValue();
        }
        return textVal;
    }


    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        String preValue = getTextValue(ele, tagName);
        int expectResult = -1;
        if(preValue == null) return expectResult;
        try{
            expectResult = Integer.parseInt(preValue);
        } catch (Exception e) {
            expectResult = -1;
        } finally {
            return expectResult;
        }
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

//        System.out.println("Total parsed " + movies.size() + " movies");
//        for (Movie movie : movies) {
//            if(movie.getTitle() == null) {
//                System.out.println("\t" + movie.toString());
//            }
//            System.out.println("\t" + movie.toString());
//        }
//
//        System.out.println("Total parsed " + gims.size() + " pairs of movies and genres");
//        for (GenresInMovies gim : gims) {
//            System.out.println("\t" + gim.toString());
//        }

//        System.out.println("Total parsed " + sims.size() + " pairs of movies and stars");
//        for (StarsInMovies sim : sims) {
//            if(sim.getMovieId() == null)
//            System.out.println("\t" + sim.toString());
//        }

//        System.out.println("Total parsed " + actors.size() + " stars");
//        for (Actor actor : actors) {
//            System.out.println("\t" + actor.toString());
//        }
    }

    private void handleInsertion() {
//        try (Connection conn = dataSource.getConnection()){
            for (GenresInMovies gim : gims) {
                try{
                    String insertGIMQuery = "INSERT INTO genres_in_movies(genreId, movieId) VALUES(?, ?)";
                    gimCount++;
                    if(gimCount % 1000 == 0) System.out.println("gim inserted: " + gimCount);
                    PreparedStatement insertGIMStatement = conn.prepareStatement(insertGIMQuery);
                    insertGIMStatement.setInt(1, gim.getGenreId());// adding genreId added in gim
                    insertGIMStatement.setString(2, gim.getMovieId());
                    insertGIMStatement.execute();
                    insertGIMStatement.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            for (StarsInMovies sim : sims) {
                try {
                    String insertSIMQuery = "INSERT INTO stars_in_movies(starId, movieId) VALUES(?, ?)";
                    PreparedStatement insertSIMStatement = conn.prepareStatement(insertSIMQuery);

                    String findStarIdQuery = "SELECT id FROM stars WHERE stars.name = ? ";
                    PreparedStatement findStarIdStatement = conn.prepareStatement(findStarIdQuery);
                    findStarIdStatement.setString(1, sim.getStageName());

                    ResultSet resId = findStarIdStatement.executeQuery();
                    simCount++;
                    if(simCount % 1000 == 0) System.out.println("sim inserted: " + simCount);


                    if(resId.next()){
                        String starId = resId.getString("id");

                        insertSIMStatement.setString(1, starId);
                        insertSIMStatement.setString(2, sim.getMovieId());
                    }
//                    else {
//                        System.out.println("not find:" + sim.getStageName() + " in SIM");
//                    }

                    resId.close();
                    findStarIdStatement.close();

                    insertSIMStatement.execute();
                    insertSIMStatement.close();
                } catch (Exception e) {
                    System.out.println(e);
                }

            }

    }

    public static void main(String[] args) throws Exception {
        // create an instance
        DomParser domParserExample = new DomParser();
        // call run example
        domParserExample.runExample();
    }

}
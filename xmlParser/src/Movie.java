
public class Movie {
    private final String title;
    private final String id;
    private final int year;
    private final String director;

    public Movie(String title, String id, int year, String director) {
        this.title = title;
        this.id = id;
        this.year = year;
        this.director = director;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String toString() {
        return "Title:" + getTitle() + ", " +
                "Year:" + getYear() + ", " +
                "ID:" + getId() + ", " +
                "Director:" + getDirector();
    }
}

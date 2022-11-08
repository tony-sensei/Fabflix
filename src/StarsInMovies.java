public class StarsInMovies {
    private final String movieId;
    private final String stageName;

    public StarsInMovies(String movieId, String stageName) {
        this.movieId = movieId;
        this.stageName = stageName;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getStageName() {
        return stageName;
    }


    public String toString() {
        return "MovieId:" + getMovieId() + ", " +
                "StageName:" + getStageName();
    }
}

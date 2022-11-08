public class GenresInMovies {
    private final String movieId;
    private final int genreId;

    public GenresInMovies(String movieId, int genreId) {
        this.movieId = movieId;
        this.genreId = genreId;
    }

    public String getMovieId() {
        return movieId;
    }

    public int getGenreId() {
        return genreId;
    }


    public String toString() {
        return "MovieId:" + getMovieId() + ", " +
                "GenreName:" + getGenreId();
    }
}

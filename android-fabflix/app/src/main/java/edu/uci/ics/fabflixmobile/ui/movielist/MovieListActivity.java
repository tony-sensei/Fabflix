package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.mainpage.MainPageSearchActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private final String host = "35.90.237.219";
//    private final String host = "10.0.2.2";
    private final String port = "8443";
    private final String domain = "cs122b-fall22-project1";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private final String requestPath = "/movie-list";
    private String requestURL;
    private String TITLE;
    private String YEAR;
    private String DIRECTOR;
    private String STAR;
    private int PAGE_NUM;
    private int TOTAL_NUM;
    private final int PAGE_CAPACITY = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        TITLE    = getIntent().getStringExtra("title");
        YEAR     = getIntent().getStringExtra("year");
        DIRECTOR = getIntent().getStringExtra("director");
        STAR     = getIntent().getStringExtra("star");
        PAGE_NUM = 0;
        TOTAL_NUM = 0;
        requestURL = baseURL + requestPath +
                "?title="    + TITLE +
                "&year="     + YEAR  +
                "&director=" + DIRECTOR +
                "&star="     + STAR  +
                "&page="     + PAGE_NUM +
                "&maxsize=20&titleSort=desc&ratingSort=desc&firstSort=rating";

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                requestURL,
                null,
                response -> {
                    // deal with response
                    Log.d("get search result", response.toString());
                    Log.d("length is: ", response.length()+"");

                    final ArrayList<Movie> movies = parseMovies(response);

                    // movies.add(new Movie("The Terminal", (short) 2004, "Director1", Arrays.asList("genre1", "genre2", "genre3"), Arrays.asList("star1", "star2", "star3")));
                    // movies.add(new Movie("The Final Season", (short) 2007, "Director2", Arrays.asList("genre1", "genre2", "genre3"), Arrays.asList("star1", "star2", "star3")));
                    MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
                    ListView listView = findViewById(R.id.list);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Movie movie = movies.get(position);
                        @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        finish();
                        Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                        SingleMoviePage.putExtra("movieId", movie.getId());
                        SingleMoviePage.putExtra("movieTitle", movie.getName());
                        SingleMoviePage.putExtra("movieYear", movie.getYear() + "");
                        SingleMoviePage.putExtra("movieDirector", movie.getDirector());
                        startActivity(SingleMoviePage);
                    });
                    // bind the next & prev button
                    final Button nextButton = findViewById(R.id.nextButton);
                    nextButton.setOnClickListener(view -> doNext());
                    final Button prevButton = findViewById(R.id.prevButton);
                    prevButton.setOnClickListener(view -> doPrev());
                    // check if there is prev/next page
                    prevButton.setEnabled(false);
                    if ((PAGE_NUM + 1) * PAGE_CAPACITY > TOTAL_NUM) nextButton.setEnabled(false);
                },
                error -> {
                    Log.d("MovieListActivity.error", error.toString());
                }
        );
        queue.add(jsonArrayRequest);
    }

    private ArrayList<Movie> parseMovies(JSONArray jsonArrayResponse) {
        final ArrayList<Movie> movies = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArrayResponse.length(); i++) {

                JSONObject movieJsonObject = (JSONObject) jsonArrayResponse.get(i);
                // get total number of returned movies
                if (movieJsonObject.has("total")) {
                    TOTAL_NUM = Integer.parseInt(movieJsonObject.getString("total"));
                    continue;
                }
                JSONArray genresJsonArray = movieJsonObject.has("movie_genres") ? movieJsonObject.getJSONArray("movie_genres") : new JSONArray();
                JSONArray starsJsonArray = movieJsonObject.has("movie_stars") ? movieJsonObject.getJSONArray("movie_stars") : new JSONArray();
                List<String> genresName = new ArrayList<>();
                List<String> starsName = new ArrayList<>();
                for (int j = 0; j < genresJsonArray.length(); j++) {
                    genresName.add(genresJsonArray.getJSONObject(j).getString("genre_name"));
                }
                for (int j = 0; j < starsJsonArray.length(); j++) {
                    starsName.add(starsJsonArray.getJSONObject(j).getString("star_name"));
                }
                movies.add(new Movie(
                        movieJsonObject.getString("movie_id"),
                        movieJsonObject.getString("movie_title"),
                        Short.parseShort(movieJsonObject.getString("movie_year")),
                        movieJsonObject.getString("movie_director"),
                        genresName,
                        starsName
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @SuppressLint("SetTextI18n")
    public void doNext() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        requestURL = baseURL + requestPath +
                "?title="    + TITLE +
                "&year="     + YEAR  +
                "&director=" + DIRECTOR +
                "&star="     + STAR  +
                "&page="     + (++PAGE_NUM) +
                "&maxsize=20&titleSort=desc&ratingSort=desc&firstSort=rating";
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                requestURL,
                null,
                response -> {
                    // deal with response
                    Log.d("get search result", response.toString());
                    Log.d("length is: ", response.length()+"");

                    final ArrayList<Movie> movies = parseMovies(response);

                    MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
                    ListView listView = findViewById(R.id.list);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Movie movie = movies.get(position);
                        @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d has id: %s", position, movie.getName(), movie.getYear(), movie.getId());
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        finish();
                        Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                        SingleMoviePage.putExtra("movieId", movie.getId());
                        SingleMoviePage.putExtra("movieTitle", movie.getName());
                        SingleMoviePage.putExtra("movieYear", movie.getYear() + "");
                        SingleMoviePage.putExtra("movieDirector", movie.getDirector());
                        startActivity(SingleMoviePage);
                    });
                    // bind the next & prev button
                    final Button nextButton = findViewById(R.id.nextButton);
                    nextButton.setOnClickListener(view -> doNext());
                    final Button prevButton = findViewById(R.id.prevButton);
                    prevButton.setOnClickListener(view -> doPrev());
                    // check if there is perv/next page
                    prevButton.setEnabled(true);
                    if ((PAGE_NUM + 1) * PAGE_CAPACITY > TOTAL_NUM) nextButton.setEnabled(false);
                },
                error -> {
                    Log.d("MovieListActivity.error", error.toString());
                }
        );
        queue.add(jsonArrayRequest);
    }

    @SuppressLint("SetTextI18n")
    public void doPrev() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        requestURL = baseURL + requestPath +
                "?title="    + TITLE +
                "&year="     + YEAR  +
                "&director=" + DIRECTOR +
                "&star="     + STAR  +
                "&page="     + (--PAGE_NUM) +
                "&maxsize=20&titleSort=desc&ratingSort=desc&firstSort=rating";
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                requestURL,
                null,
                response -> {
                    // deal with response
                    Log.d("get search result", response.toString());
                    Log.d("length is: ", response.length()+"");

                    final ArrayList<Movie> movies = parseMovies(response);

                    MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
                    ListView listView = findViewById(R.id.list);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Movie movie = movies.get(position);
                        @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        finish();
                        Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                        SingleMoviePage.putExtra("movieId", movie.getId());
                        SingleMoviePage.putExtra("movieTitle", movie.getName());
                        SingleMoviePage.putExtra("movieYear", movie.getYear() + "");
                        SingleMoviePage.putExtra("movieDirector", movie.getDirector());
                        startActivity(SingleMoviePage);
                    });
                    // bind the next & prev button
                    final Button nextButton = findViewById(R.id.nextButton);
                    nextButton.setOnClickListener(view -> doNext());
                    final Button prevButton = findViewById(R.id.prevButton);
                    prevButton.setOnClickListener(view -> doPrev());
                    // check if there is next page
                    if (PAGE_NUM <= 0) prevButton.setEnabled(false);
                    nextButton.setEnabled(true);
                },
                error -> {
                    Log.d("MovieListActivity.error", error.toString());
                }
        );
        queue.add(jsonArrayRequest);
    }
}


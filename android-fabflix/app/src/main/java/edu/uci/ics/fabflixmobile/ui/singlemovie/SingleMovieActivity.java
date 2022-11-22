package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;

public class SingleMovieActivity extends AppCompatActivity {

    private final String host = "35.90.237.219";
//    private final String host = "10.0.2.2";
    private final String port = "8443";
    private final String domain = "cs122b-fall22-project1";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private final String requestPath = "/api/single-movie";
    private String requestURL;
    private String movieId;
    private String movieTitle;
    private String movieYear;
    private String movieDirector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        Intent currIntent = getIntent();
        movieId       = currIntent.getStringExtra("movieId");
        movieTitle    = currIntent.getStringExtra("movieTitle");
        movieYear     = currIntent.getStringExtra("movieYear");
        movieDirector = currIntent.getStringExtra("movieDirector");
        requestURL = baseURL + requestPath +
                "?id=" + movieId +
                "&title=null&year=null&director=null&star=null&page=0&maxsize=20&titleSort=desc&ratingSort=desc&firstSort=rating";

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                requestURL,
                null,
                response -> {
                    // deal with response
                    Log.d("get search result", response.toString());

                    ArrayList<String> stars = new ArrayList<>();
                    ArrayList<String> genres = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject singleMovieObject = response.getJSONObject(i);
                            // add stars and genres
                            String curr_star = singleMovieObject.getString("movie_star");
                            if (!stars.contains(curr_star)) stars.add(curr_star);
                            String curr_genre = singleMovieObject.getString("movie_genre");
                            if (!genres.contains(curr_genre)) genres.add(curr_genre);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TextView singleMovieTitle = findViewById(R.id.singleMovieTitle);
                    singleMovieTitle.setText(movieTitle);
                    TextView singleMovieYear  = findViewById(R.id.singleMovieYear);
                    singleMovieYear.setText(movieYear);
                    TextView singleMovieDirector = findViewById(R.id.singleMovieDirector);
                    singleMovieDirector.setText(movieDirector);
                    // display genres and stars
                    SingleMovieGenreViewAdapter adapter = new SingleMovieGenreViewAdapter(this, genres);
                    ListView singleMovieGenresList = findViewById(R.id.singleMovieGenres);
                    singleMovieGenresList.setAdapter(adapter);
                    SingleMovieStarViewAdapter adapter2  = new SingleMovieStarViewAdapter(this, stars);
                    ListView singleMovieStarsList  = findViewById(R.id.singleMovieStars);
                    singleMovieStarsList.setAdapter(adapter2);
                },
                error -> {
                    Log.d("SingleMovieActivity.error", error.toString());
                }
        );
        queue.add(jsonArrayRequest);
    }

    private ArrayList<Movie> parseSingleMovie(JSONArray jsonArrayResponse) {

        return null;
    }

}


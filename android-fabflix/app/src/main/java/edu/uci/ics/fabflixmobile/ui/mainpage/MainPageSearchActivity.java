package edu.uci.ics.fabflixmobile.ui.mainpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainpageSearchBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

import java.util.HashMap;
import java.util.Map;

public class MainPageSearchActivity extends AppCompatActivity {
    private EditText movieTitle;
    private EditText movieYear;
    private EditText movieDirector;
    private EditText movieStar;

//    private final String host = "10.0.2.2";
    private final String host = "35.87.114.5";
    private final String port = "8080";
//    private final String domain = "cs122b-fall22-project2-login-cart-example";
    private final String domain = "cs122b-fall22-project1";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainpageSearchBinding binding = ActivityMainpageSearchBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());
        movieTitle = binding.movieTitle;
        movieYear  = binding.movieYear;
        movieDirector = binding.movieDirector;
        movieStar  = binding.movieStar;

        // bind search button with submitSearch()
        final Button searchButton = binding.search;
        searchButton.setOnClickListener(view -> submitSearch());
    }

    @SuppressLint("SetTextI18n")
    public void submitSearch() {
        String getURL =
                "?title="    + (movieTitle.getText().toString().equals("") ? "null" : movieTitle.getText().toString())       +
                "&year="     + (movieYear.getText().toString().equals("") ? "null" : movieYear.getText().toString())         +
                "&director=" + (movieDirector.getText().toString().equals("") ? "null" : movieDirector.getText().toString()) +
                "&star="     + (movieStar.getText().toString().equals("") ? "null" : movieStar.getText().toString())         +
                "&page=0&maxsize=20&titleSort=desc&ratingSort=desc&firstSort=rating";
        finish();
        // initialize the activity(page)/destination
        Intent MovieListPage = new Intent(MainPageSearchActivity.this, MovieListActivity.class);
        MovieListPage.putExtra("REQUEST_URL", baseURL + "/movie-list" + getURL);
        MovieListPage.putExtra("title", (movieTitle.getText().toString().equals("") ? "null" : movieTitle.getText().toString()));
        MovieListPage.putExtra("year", (movieYear.getText().toString().equals("") ? "null" : movieYear.getText().toString()));
        MovieListPage.putExtra("director", (movieDirector.getText().toString().equals("") ? "null" : movieDirector.getText().toString()));
        MovieListPage.putExtra("star", (movieStar.getText().toString().equals("") ? "null" : movieStar.getText().toString()));
        // activate the list page.
        startActivity(MovieListPage);
//        final StringRequest searchRequest = new StringRequest(
//                Request.Method.GET,//                baseURL + "/movie-list" + getURL,
//                response -> {
//                    // deal with response
//                    Log.d("get search result", response);
//                    //Complete and destroy login activity once successful
//                    finish();
//                    // initialize the activity(page)/destination
//                    Intent MovieListPage = new Intent(MainPageSearchActivity.this, MovieListActivity.class);
//                    // activate the list page.
//                    startActivity(MovieListPage);
//                },
//                error -> {
//                    Log.d("login.error", error.toString());
//                }
//        );

    }

}

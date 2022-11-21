package edu.uci.ics.fabflixmobile.ui.movielist;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView subtitle;
        TextView directorText;
        List<TextView> genres;
        List<TextView> stars;
//        TextView genreText1;
//        TextView genreText2;
//        TextView genreText3;
//        TextView starText1;
//        TextView starText2;
//        TextView starText3;
    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.movielist_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.subtitle = convertView.findViewById(R.id.subtitle);
            viewHolder.directorText = convertView.findViewById(R.id.directorText);
            viewHolder.genres = Arrays.asList(
                    convertView.findViewById(R.id.genreText1),
                    convertView.findViewById(R.id.genreText2),
                    convertView.findViewById(R.id.genreText3)
            );
            viewHolder.stars  = Arrays.asList(
                    convertView.findViewById(R.id.starText1),
                    convertView.findViewById(R.id.starText2),
                    convertView.findViewById(R.id.starText3)
            );
//            viewHolder.genres.add(convertView.findViewById(R.id.genreText1));
//            viewHolder.genres.add(convertView.findViewById(R.id.genreText2));
//            viewHolder.genres.add(convertView.findViewById(R.id.genreText3));
//            viewHolder.stars.add(convertView.findViewById(R.id.starText1));
//            viewHolder.stars.add(convertView.findViewById(R.id.starText2));
//            viewHolder.stars.add(convertView.findViewById(R.id.starText3));
//            viewHolder.genreText1 = convertView.findViewById(R.id.genreText1);
//            viewHolder.genreText2 = convertView.findViewById(R.id.genreText2);
//            viewHolder.genreText3 = convertView.findViewById(R.id.genreText3);
//            viewHolder.starText1 = convertView.findViewById(R.id.starText1);
//            viewHolder.starText2 = convertView.findViewById(R.id.starText2);
//            viewHolder.starText3 = convertView.findViewById(R.id.starText3);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.title.setText(movie.getName());
        viewHolder.subtitle.setText(movie.getYear() + "");
        viewHolder.directorText.setText(movie.getDirector());
        List<String> genresString = movie.getGenres();
        for (int i = 0; i < Math.min(3, genresString.size()); i++) {
            viewHolder.genres.get(i).setText(genresString.get(i));
        }
        List<String> starsString = movie.getStars();
        for (int i = 0; i < Math.min(3, starsString.size()); i++) {
            viewHolder.stars.get(i).setText(starsString.get(i));
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
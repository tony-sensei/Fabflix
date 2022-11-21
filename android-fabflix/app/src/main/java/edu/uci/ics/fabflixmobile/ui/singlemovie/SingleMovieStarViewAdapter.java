package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.uci.ics.fabflixmobile.R;

public class SingleMovieStarViewAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> stars;

    // View lookup cache
    private static class ViewHolder {
        TextView starName;
    }

    public SingleMovieStarViewAdapter(Context context, ArrayList<String> stars) {
        super(context, R.layout.single_movie_star_row, stars);
        this.stars = stars;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        String star = stars.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.single_movie_star_row, parent, false);
            viewHolder.starName = convertView.findViewById(R.id.starName);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.starName.setText(star);
        // Return the completed view to render on screen
        return convertView;
    }
}
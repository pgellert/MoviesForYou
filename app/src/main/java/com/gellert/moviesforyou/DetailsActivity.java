package com.gellert.moviesforyou;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gellert.moviesforyou.objects.Movie;
import com.gellert.moviesforyou.objects.Review;
import com.gellert.moviesforyou.objects.Trailer;
import com.gellert.moviesforyou.provider.FavoritesColumns;
import com.gellert.moviesforyou.provider.FavoritesProvider;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";


    private Movie movie;
    private ReviewsAdapter reviewsAdapter;
    private TrailersAdapter trailersAdapter;

    private ShareActionProvider mShareActionProvider;

    ScrollView containerScrollView;
    TextView movieTitle;
    TextView releaseDateTextView;
    TextView movieLengthTextView;
    TextView voteAverageTextView;
    TextView overviewTextView;
    ImageView posterImageView;
    ListView reviewsListView;
    ListView trailersListView;

    Button markFavoriteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Movie Details");

        Bundle data = getIntent().getExtras();

        movie = new Movie();

        movie.setID(data.getInt(Movie.MOVIE_ID));
        movie.setOriginalTitle(data.getString(Movie.MOVIE_ORIGINAL_TITLE));
        movie.setReleaseDate(data.getString(Movie.MOVIE_RELEASE_DATE));
        movie.setImageUrl(data.getString(Movie.MOVIE_IMAGE_URL));
        movie.setPlotSynopsis(data.getString(Movie.MOVIE_PLOT_SYNOPSIS));
        movie.setVoteAverage(data.getFloat(Movie.MOVIE_VOTE_AVERAGE));


        Log.i("DetailsActivity",String.valueOf(movie.getID()));
        Log.i("DetailsActivity",String.valueOf(movie.getVoteAverage()));

        containerScrollView = (ScrollView) findViewById(R.id.containerScrollView);
        movieTitle = (TextView) findViewById(R.id.movieTitle);
        releaseDateTextView = (TextView) findViewById(R.id.releaseDateTextView);
        movieLengthTextView = (TextView) findViewById(R.id.movieLength);
        voteAverageTextView = (TextView) findViewById(R.id.voteAverageTextView);
        overviewTextView = (TextView) findViewById(R.id.overviewTextView);
        posterImageView = (ImageView) findViewById(R.id.posterImageView);
        markFavoriteButton = (Button) findViewById(R.id.markFavoriteButton);

        movieTitle.setText(movie.getOriginalTitle());
        releaseDateTextView.setText(movie.getReleaseDate());
        voteAverageTextView.setText(getString(R.string.format_vote_average,movie.getVoteAverage()));
        overviewTextView.setText(movie.getPlotSynopsis());

        Picasso.with(DetailsActivity.this).load(movie.getImageUrl()).into(posterImageView);

        reviewsListView = (ListView) findViewById(R.id.reviewsListView);
        reviewsAdapter = new ReviewsAdapter(this,R.id.reviewsListView);
        reviewsListView.setAdapter(reviewsAdapter);

        trailersListView = (ListView) findViewById(R.id.trailersListView);
        trailersAdapter = new TrailersAdapter(this,R.id.trailersListView);
        trailersListView.setAdapter(trailersAdapter);

        markFavoriteButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        ContentValues cv = new ContentValues();

                        cv.put(FavoritesColumns.MOVIE_ID,movie.getID());
                        cv.put(FavoritesColumns.IMAGE_URL,movie.getImageUrl());
                        cv.put(FavoritesColumns.PLOT_SYNOPSIS,movie.getPlotSynopsis());
                        cv.put(FavoritesColumns.RELEASE_DATE,movie.getReleaseDate());
                        cv.put(FavoritesColumns.TITLE,movie.getOriginalTitle());
                        cv.put(FavoritesColumns.VOTE_AVERAGE,movie.getVoteAverage());

                        Cursor c = getContentResolver().query(FavoritesProvider.Favorites.CONTENT_URI,
                                null, FavoritesColumns.TITLE + " = \"" + movie.getOriginalTitle() + "\"", null, null);
                        if(c.getCount() == 0) {
                            // not found in database
                            getContentResolver().insert(FavoritesProvider.Favorites.CONTENT_URI,cv);
                        }
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(!trailersAdapter.isEmpty()){
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }

        return true;
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this movie: " + trailersAdapter.getItem(0).getUrl());
        return shareIntent;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(Utility.isOnline(DetailsActivity.this)) {
            FetchFurtherMovieDetails fetchFurtherMovieDetails = new FetchFurtherMovieDetails();
            fetchFurtherMovieDetails.execute(movie);
        } else{
            movieLengthTextView.setVisibility(TextView.GONE);

            trailersListView.setVisibility(ListView.GONE);
            findViewById(R.id.trailersLabel).setVisibility(TextView.GONE);
            findViewById(R.id.trailersDividerLine).setVisibility(View.GONE);

            reviewsListView.setVisibility(ListView.GONE);
            findViewById(R.id.reviewsLabel).setVisibility(TextView.GONE);
            findViewById(R.id.reviewsDividerLine).setVisibility(View.GONE);
        }
    }

    public class FetchFurtherMovieDetails extends AsyncTask<Movie,Void,Movie> {
        private final String apiKey = getResources().getString(R.string.apiKey);
        private final String TAG = "FetchFurtherMovieDetails";


        @Override
        protected void onPostExecute(Movie m) {
            movie.setLength(m.getLength());
            movie.setReviews(m.getReviews());
            movie.setTrailers(m.getTrailers());

            movieLengthTextView.setText(getString(R.string.format_movie_length,movie.getLength()));

            for(Trailer t:movie.getTrailers()) {
                trailersAdapter.add(t);
            }

            for(Review r:movie.getReviews()) {
                reviewsAdapter.add(r);
            }

            if(trailersAdapter.isEmpty()) {
                trailersListView.setVisibility(ListView.GONE);
                findViewById(R.id.trailersLabel).setVisibility(TextView.GONE);
                findViewById(R.id.trailersDividerLine).setVisibility(View.GONE);
            }

            if(reviewsAdapter.isEmpty()){
                reviewsListView.setVisibility(ListView.GONE);
                findViewById(R.id.reviewsLabel).setVisibility(TextView.GONE);
                findViewById(R.id.reviewsDividerLine).setVisibility(View.GONE);
            }

            Utility.setListViewHeightBasedOnChildren(trailersListView);
            Utility.setListViewHeightBasedOnChildren(reviewsListView);

            containerScrollView.smoothScrollTo(0,0);
        }

        @Override
        protected Movie doInBackground(Movie... params) {
            Movie movie = params[0];

            int ID = movie.getID();
            movie.setLength(getMovieLength(ID));
            movie.setTrailers(getTrailers(ID));
            movie.setReviews(getReviews(ID));

            return movie;
        }



        private String getJsonDataFromUrl(URL url){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String dataJsonString = null;
            InputStream inputStream;
            StringBuilder buffer = null;


            try {
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                inputStream = urlConnection.getInputStream();
            } catch (IOException e) {
                inputStream = urlConnection.getErrorStream();
            }
            try{
                buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                dataJsonString = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return dataJsonString;
        }

        private int getMovieLength(int id){
            URL url = buildMovieUrl(id);
            String jsonData = getJsonDataFromUrl(url);

            try {
                JSONObject root = new JSONObject(jsonData);
                return root.getInt("runtime");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }

        private URL buildMovieUrl(int id) {
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APIKEY_PARAM = "api_key";

                Uri myUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(String.valueOf(id))
                        .appendQueryParameter(APIKEY_PARAM, apiKey)
                        .build();
                //Log.i(TAG,myUri.toString());
                return new URL(myUri.toString());
            }
            catch (MalformedURLException e){
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<Review> getReviews(int id) {
            ArrayList<Review> reviews = new ArrayList<Review>();
            URL url = buildReviewsUrl(id);
            String jsonData = getJsonDataFromUrl(url);

            try{
                JSONObject root = new JSONObject(jsonData);
                JSONArray results = root.getJSONArray("results");

                for (int i = 0; i < results.length();i++) {
                    String author = results.getJSONObject(i).getString("author");
                    String content = results.getJSONObject(i).getString("content");
                    reviews.add(new Review(author,content));

                    //Log.i(TAG,author + ": " + content);
                }
            } catch (JSONException e) {
                //Log.i(TAG,url.toString());
                e.printStackTrace();
            }

            return reviews;
        }

        private URL buildReviewsUrl(int id){
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String REVIEWS_PATH = "reviews";
                final String APIKEY_PARAM = "api_key";

                Uri myUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(String.valueOf(id))
                        .appendPath(REVIEWS_PATH)
                        .appendQueryParameter(APIKEY_PARAM, apiKey)
                        .build();
                return new URL(myUri.toString());
            }
            catch (MalformedURLException e){
                e.printStackTrace();
            }
            return null;
        }


        private ArrayList<Trailer> getTrailers(int id) {
            ArrayList<Trailer> trailers = new ArrayList<Trailer>();
            URL url = buildTrailersUrl(id);
            String jsonData = getJsonDataFromUrl(url);

            try{
                JSONObject root = new JSONObject(jsonData);
                JSONArray youtube = root.getJSONArray("youtube");

                for (int i = 0; i < youtube.length();i++) {
                    String name = youtube.getJSONObject(i).getString("name");
                    String relativePath = youtube.getJSONObject(i).getString("source");
                    String youtubeUrl = buildYoutubeUrl(relativePath);

                    trailers.add(new Trailer(name,youtubeUrl));
                    //Log.i(TAG,name + ": " + youtubeUrl);
                }
            } catch (JSONException e) {
                //Log.i(TAG,url.toString());
                e.printStackTrace();
            }

            return trailers;
        }

        private URL buildTrailersUrl(int id){
            try {

                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String TRAILERS_PATH = "trailers";
                final String APIKEY_PARAM = "api_key";

                Uri myUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(String.valueOf(id))
                        .appendPath(TRAILERS_PATH)
                        .appendQueryParameter(APIKEY_PARAM, apiKey)
                        .build();
                return new URL(myUri.toString());
            }
            catch (MalformedURLException e){
                e.printStackTrace();
            }
            return null;
        }

        private String buildYoutubeUrl(String relativePath){
            return "https://youtube.com/watch?v=" + relativePath;
        }
    }

}

package com.gellert.moviesforyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gellert.moviesforyou.objects.Movie;

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

public class MainActivity extends AppCompatActivity {
    public MoviesAdapter myAdapter;
    public GridView imagesGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesGridView = (GridView)findViewById(R.id.imagesGridView);

        myAdapter = new MoviesAdapter(MainActivity.this,R.id.imagesGridView);
        imagesGridView.setAdapter(myAdapter);

        imagesGridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Movie movie = myAdapter.getItem(position);
                        Intent details = new Intent(MainActivity.this, DetailsActivity.class)
                                .putExtra(Movie.MOVIE_ID, movie.getID())
                                .putExtra(Movie.MOVIE_IMAGE_URL, String.valueOf(movie.getImageUrl()))
                                .putExtra(Movie.MOVIE_ORIGINAL_TITLE, movie.getOriginalTitle())
                                .putExtra(Movie.MOVIE_PLOT_SYNOPSIS, movie.getPlotSynopsis())
                                .putExtra(Movie.MOVIE_RELEASE_DATE, movie.getReleaseDate())
                                .putExtra(Movie.MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
                        startActivity(details);
                    }
        });

    }

    private String getTitleBySortOrder() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        String sortOder = sharedPreferences.getString(getString(R.string.pref_sort_oder_key), getString(R.string.pref_sort_oder_default));

        switch (sortOder){
            case "vote_average.desc":
                return "Highest Rated Movies";
            case "popularity.desc":
                return "Pop Movies";
            default:
                return "Movies";
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateImages();

        setTitle(getTitleBySortOrder());
    }

    public void UpdateImages(){
        if(Utility.isOnline(MainActivity.this)) {
            FetchMoviesData fetchMoviesData = new FetchMoviesData();
            fetchMoviesData.execute();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need network connection to discover new movies, but you can still check you favorite movies.")
                    .setTitle("Unable to connect")
                    .setCancelable(false)
                    .setPositiveButton("Favorites",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(MainActivity.this,FavoritesActivity.class);
                                    startActivity(i);
                                }
                            }
                    )
                    .setNegativeButton("WIFI",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(i);
                                }
                            }
                    );
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_favorite:
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class FetchMoviesData extends AsyncTask<String, Void, Movie[]> {
        final static String TAG = "FetchMoviesData";
        private final String apiKey = "df950a8cfc357418431121a4593acff9";

        @Override
        protected Movie[] doInBackground(String... params) {
            URL url = buildDiscoverMoviesUrl();
            String moviesJsondata = getJsonDataFromUrl(url);
            return FetchMoviesJsonData(moviesJsondata);
        }


        @Override
        protected void onPostExecute(Movie[] results) {
            Log.i(TAG,"onPostExecute");
            if(results != null) {
                myAdapter.clear();
                for (Movie movie:results) {
                    myAdapter.add(movie);
                }
            }
        }


        private Movie[] FetchMoviesJsonData(String jsonData){
            try{
                JSONObject data = new JSONObject(jsonData);
                JSONArray results = data.getJSONArray("results");

                Movie[] movies = new Movie[results.length()];

                for (int i = 0; i < results.length();i++) {
                    int ID = Integer.parseInt(results.getJSONObject(i).getString("id"));
                    String imageUrl = buildImageUrl(results.getJSONObject(i).getString("poster_path"));
                    String originalTitle = results.getJSONObject(i).getString("original_title");
                    String releaseDate = results.getJSONObject(i).getString("release_date");
                    if(releaseDate.length() >= 4) {
                        releaseDate = releaseDate.substring(0, 4);
                    }
                    float voteAverage = Float.parseFloat(results.getJSONObject(i).getString("vote_average"));
                    String plotSynopsis = results.getJSONObject(i).getString("overview");
                    movies[i] = new Movie(ID,imageUrl,originalTitle,plotSynopsis,releaseDate,voteAverage);
                }

                return movies;
            } catch(JSONException e){
                e.printStackTrace();
                return null;
            }
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
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return dataJsonString;
        }

        private URL buildDiscoverMoviesUrl(){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

            String sortOder = sharedPreferences.getString(getString(R.string.pref_sort_oder_key), getString(R.string.pref_sort_oder_default));

            try {

                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_ORDER_PARAM = "sort_by";
                final String APIKEY_PARAM = "api_key";

                Uri myUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_ORDER_PARAM, sortOder)
                        .appendQueryParameter(APIKEY_PARAM, apiKey)
                        .build();
                return new URL(myUri.toString());
            }
            catch (MalformedURLException e){
                e.printStackTrace();
            }
            return null;
        }


        private String buildImageUrl(String relativePath){
            final String BASE_URL = "http://image.tmdb.org/t/p/";
            final String IMAGE_SIZE = "w342/";
            return BASE_URL + IMAGE_SIZE + relativePath;
        }
    }
}

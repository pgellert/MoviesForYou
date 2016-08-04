package com.gellert.moviesforyou.objects;

import java.util.ArrayList;

/**
 * Created by gellert on 2016. 01. 28..
 */
public class Movie {
    public static final String MOVIE_ID = "MOVIE_ID";
    public static final String MOVIE_IMAGE_URL = "MOVIE_IMAGE_URL";
    public static final String MOVIE_ORIGINAL_TITLE = "MOVIE_ORIGINAL_TITLE";
    public static final String MOVIE_RELEASE_DATE = "MOVIE_RELEASE_DATE";
    public static final String MOVIE_PLOT_SYNOPSIS = "MOVIE_PLOT_SYNOPSIS";
    public static final String MOVIE_VOTE_AVERAGE = "MOVIE_VOTE_AVERAGE";
    public static final String MOVIE_LENGTH = "MOVIE_LENGTH";
    public static final String MOVIE_REVIEWS = "MOVIE_REVIEWS";
    public static final String MOVIE_TRAILERS = "MOVIE_TRAILERS";

    int ID;
    String imageUrl;
    String originalTitle;
    String releaseDate;
    float voteAverage;
    String plotSynopsis;
    ArrayList<Trailer> trailers;
    ArrayList<Review> reviews;
    int length;

    public Movie() {
    }

    public Movie(int ID, String imageUrl, String originalTitle, String plotSynopsis, String releaseDate, float voteAverage) {
        this.ID = ID;
        this.imageUrl = imageUrl;
        this.originalTitle = originalTitle;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }
}

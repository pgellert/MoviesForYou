package com.gellert.moviesforyou.objects;

/**
 * Created by gellert on 2016. 07. 07..
 */
public class Trailer {
    String url;
    String name;

    public Trailer() {
    }

    public Trailer(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

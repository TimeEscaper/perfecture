package ru.mail.tp.perfecture.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sibirsky on 14.04.17.
 */

public class Place {
    private long id;
    private String title;
    private String location;
    private String description;
    private List<String> photos = new ArrayList<>();

    public Place(long id, String title, String location, String description) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhotos() {
        return photos;
    }
}

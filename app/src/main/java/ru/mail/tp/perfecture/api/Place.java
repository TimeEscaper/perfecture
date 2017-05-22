package ru.mail.tp.perfecture.api;

import java.util.ArrayList;
import java.util.List;

import ru.mail.tp.perfecture.places.cache.Cacheable;

@SuppressWarnings("unused")
public class Place implements Cacheable {
    private long id;
    private String title;
    private String description = "";
    private double latitude;
    private double longitude;
    private List<String> photos = new ArrayList<>();

    public Place(long id, String title, String description, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) { this.photos = photos; }
}

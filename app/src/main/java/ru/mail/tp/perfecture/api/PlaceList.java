package ru.mail.tp.perfecture.api;

import java.util.List;

import ru.mail.tp.perfecture.places.cache.Cacheable;

public class PlaceList implements Cacheable {
    private List<Place> places;

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}

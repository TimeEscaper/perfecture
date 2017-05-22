package ru.mail.tp.perfecture.api;

import ru.mail.tp.perfecture.places.cache.Cacheable;

public class PlaceError implements Cacheable {
    private String message;

    public PlaceError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

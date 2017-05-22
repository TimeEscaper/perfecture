package ru.mail.tp.perfecture.api;

/**
 * Created by sibirsky on 21.05.17.
 */

public class CachedObject<T> {
    private T object;
    private String error;

    public CachedObject(T object) {
        this.object = object;
    }

    public CachedObject(String error) {
        this.error = error;
    }

    public T getObject() {
        return object;
    }

    public String getError() {
        return error;
    }
}

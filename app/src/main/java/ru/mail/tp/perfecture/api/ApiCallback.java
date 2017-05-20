package ru.mail.tp.perfecture.api;

/**
 * Created by sibirsky on 20.05.17.
 */

public interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(String message);
}

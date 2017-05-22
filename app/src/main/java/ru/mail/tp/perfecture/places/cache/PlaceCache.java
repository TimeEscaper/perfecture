package ru.mail.tp.perfecture.places.cache;

import android.util.SparseArray;

public class PlaceCache {
    private SparseArray<Cacheable> cache = new SparseArray<>();

    public boolean contains(int id) {
        return cache.indexOfKey(id) >= 0;
    }

    public void put(int id, Cacheable value) {
        cache.put(id, value);
    }

    public Cacheable get(int id) {
        return cache.get(id);
    }

    public void remove(int id) {
        cache.remove(id);
    }
}

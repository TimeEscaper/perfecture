package ru.mail.tp.perfecture.storage;

/**
 * Created by sibirsky on 14.04.17.
 */

class DbManager {
    private static final DbManager ourInstance = new DbManager();

    static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
    }
}

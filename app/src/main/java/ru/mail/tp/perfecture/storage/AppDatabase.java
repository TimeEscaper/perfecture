package ru.mail.tp.perfecture.storage;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by sibirsky on 13.04.17.
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "AppDatabase";

    public static final int VERSION = 1;
}
package ru.mail.tp.perfecture.mvp;

import android.os.Bundle;

import java.util.List;

/**
 * Created by maksimus on 01.04.17.
 */

public class Presenter {
    public void onCreate(Bundle buindle) { };

    public void onPermissionGranted() { };

    public void onPermissionNotGranted(List<String> permissions) { };

    public void onDestroy() { }
}

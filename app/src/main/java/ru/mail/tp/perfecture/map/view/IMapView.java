package ru.mail.tp.perfecture.map.view;

import ru.mail.tp.perfecture.mvp.IView;

/**
 * Created by maksimus on 01.04.17.
 */

public interface IMapView extends IView {
    void checkPermissions(String... permissions);
}

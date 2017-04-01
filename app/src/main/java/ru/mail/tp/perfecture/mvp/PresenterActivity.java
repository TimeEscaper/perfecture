package ru.mail.tp.perfecture.mvp;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by maksimus on 01.04.17.
 */

public abstract class PresenterActivity<P extends Presenter> extends AppCompatActivity
        implements IView {
    protected P presenter;

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void showToast(String text, int length) {
        Toast.makeText(this, text, length).show();
    }
}

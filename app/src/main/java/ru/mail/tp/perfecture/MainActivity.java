package ru.mail.tp.perfecture;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.mail.tp.perfecture.map.MapActivity;
import ru.mail.tp.perfecture.places.PlacesListActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnOpenMap;
    private Button btnOpenList;

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenMap = (Button)findViewById(R.id.btn_map);
        btnOpenList = (Button)findViewById(R.id.btn_places);

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
        btnOpenList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlacesList();
            }
        });
    }

    private void openMap() {
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    private void openPlacesList() {
        startActivity(new Intent(MainActivity.this, PlacesListActivity.class));
    }
}

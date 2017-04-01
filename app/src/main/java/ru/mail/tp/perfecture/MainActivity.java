package ru.mail.tp.perfecture;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.mail.tp.perfecture.map.MapActivity;
import ru.mail.tp.perfecture.map.MapsActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnOpenMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenMap = (Button)findViewById(R.id.btn_map);

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
    }

    private void openMap() {
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }
}

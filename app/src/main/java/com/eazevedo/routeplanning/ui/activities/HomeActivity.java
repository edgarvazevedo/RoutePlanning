package com.eazevedo.routeplanning.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eazevedo.routeplanning.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fabAddTrip;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        fabAddTrip = findViewById(R.id.fab_add_trip);

        fabAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //para avancar para a outra activity
                 Intent intent = new Intent(HomeActivity.this, NewRoute.class);
                 startActivity(intent);

                // Para este exemplo, vamos apenas mostrar um Toast
                Toast.makeText(HomeActivity.this, "Adicionar nova viagem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Liberar recursos do AdView quando a activity for destruída
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}

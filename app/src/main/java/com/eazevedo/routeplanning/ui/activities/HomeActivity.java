package com.eazevedo.routeplanning.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eazevedo.routeplanning.R;
import com.eazevedo.routeplanning.ui.adapters.TripAdapter;
import com.eazevedo.routeplanning.db.Trip;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fabAddTrip;
    private AdView adView;
    private RecyclerView recyclerViewTrips;
    private TripAdapter tripAdapter;
    private List<Trip> trips;
    private TextView startLocationTextView;
    private TextView destinationTextView;
    private TextView stopsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        adView = findViewById(R.id.adView);
        recyclerViewTrips = findViewById(R.id.recycler_view_trips);  // Certifique-se de que este ID está correto
        fabAddTrip = findViewById(R.id.fab_add_trip);

        startLocationTextView = findViewById(R.id.start_location);
        destinationTextView = findViewById(R.id.destination);
        stopsTextView = findViewById(R.id.stops);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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

        // Atualizar SharedPreferences
        updateAddressToParadas();

        trips = loadTripsFromSharedPreferences();

        tripAdapter = new TripAdapter(trips);
        recyclerViewTrips.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrips.setAdapter(tripAdapter);

        // Load and display trip details
        displayTripDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualizar dados e UI quando a atividade voltar a ser visível
        updateAddressToParadas();
        trips = loadTripsFromSharedPreferences();
        tripAdapter.notifyDataSetChanged();

        displayTripDetails();
    }

    @Override
    protected void onDestroy() {
        // Liberar recursos do AdView quando a activity for destruída
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void updateAddressToParadas() {
        SharedPreferences sharedPreferences = getSharedPreferences("RoutePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Carregar a string que contém os detalhes das viagens
        String tripsJson = sharedPreferences.getString("trips", "");

        if (!tripsJson.isEmpty()) {
            try {
                // Substituir "address" por "Paradas"
                String updatedTripsJson = tripsJson.replace("\"address\"", "\"Paradas\"");

                // Salvar a string modificada nas SharedPreferences
                editor.putString("trips", updatedTripsJson);
                editor.apply();

                Log.d("HomeActivity", "SharedPreferences updated successfully.");
            } catch (Exception e) {
                Log.e("HomeActivity", "Failed to update SharedPreferences: " + e.getMessage());
            }
        }
    }

    private List<Trip> loadTripsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("RoutePrefs", Context.MODE_PRIVATE);

        String startLocation = sharedPreferences.getString("startLocation", "");
        String stops = sharedPreferences.getString("stops", "");
        String destination = sharedPreferences.getString("destination", "");

        Log.e("HomeActivity", "VER VALORES: " + startLocation + " | " + destination + " | " + stops);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Trip>>() {}.getType();
        List<Trip> trips = gson.fromJson(sharedPreferences.getString("trips", ""), type);

        return trips != null ? trips : new ArrayList<>();
    }

    private void displayTripDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("RoutePrefs", Context.MODE_PRIVATE);

        String startLocation = sharedPreferences.getString("startLocation", "");
        String stops = sharedPreferences.getString("stops", "");
        String destination = sharedPreferences.getString("destination", "");

        startLocationTextView.setText(startLocation);
        stopsTextView.setText(stops);
        destinationTextView.setText(destination);
    }
}

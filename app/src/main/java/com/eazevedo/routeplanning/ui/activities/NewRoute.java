package com.eazevedo.routeplanning.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eazevedo.routeplanning.R;
import com.eazevedo.routeplanning.services.RetrofitClient;
import com.eazevedo.routeplanning.ui.activities.MapsActivity;
import com.eazevedo.routeplanning.ui.adapters.RouteRequest;
import com.eazevedo.routeplanning.ui.adapters.RouteResponse;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.gson.Gson;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.android.gms.ads.AdView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRoute extends AppCompatActivity {

    private static final String TAG = "NewRoute";

    private LinearLayout stopsContainer;
    private Button addStopButton;
    private Button calculateButton;
    private MaterialSwitch roundTripSwitch;
    private int stopCount = 0;
    private static final int MAX_STOPS = 5;
    private String startLocation;
    private String destination;
    private boolean isRoundTrip;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
            Log.d(TAG, "Places API Initialized.");
        }

        // Para o ponto de partida:
        AutocompleteSupportFragment autocompleteStartLocation = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_start_location);
        autocompleteStartLocation.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteStartLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                startLocation = place.getName();
                if (isRoundTrip) {
                    destination = startLocation; // Se for ida e volta, define o destino como o ponto de partida
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        // Para o destino:
        AutocompleteSupportFragment autocompleteDestination = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_destination);
        autocompleteDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (!isRoundTrip) {
                    destination = place.getName();
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        stopsContainer = findViewById(R.id.stops_container);
        addStopButton = findViewById(R.id.btn_add_stop);
        calculateButton = findViewById(R.id.btn_calculate);
        roundTripSwitch = findViewById(R.id.switch_round_trip);

        addStopButton.setOnClickListener(v -> {
            if (stopCount < MAX_STOPS) {
                addStopField();
                stopCount++;
                if (stopCount == MAX_STOPS) {
                    addStopButton.setEnabled(false);
                }
            } else {
                Toast.makeText(NewRoute.this, "Você pode adicionar no máximo 5 paradas.", Toast.LENGTH_SHORT).show();
            }
        });

        calculateButton.setOnClickListener(v -> calculateRoute());

        roundTripSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRoundTrip = isChecked;
            AutocompleteSupportFragment destinationFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_destination);

            if (isChecked) {
                destination = startLocation;  // Define o destino como o ponto de partida
                if (destinationFragment != null) {
                    getSupportFragmentManager().beginTransaction().hide(destinationFragment).commit();
                }
            } else {
                destination = null;  // Reseta o destino
                if (destinationFragment != null) {
                    getSupportFragmentManager().beginTransaction().show(destinationFragment).commit();
                }
            }
        });
    }

    private void addStopField() {
        View stopView = getLayoutInflater().inflate(R.layout.stop_field, null);
        ImageButton removeButton = stopView.findViewById(R.id.btn_remove_stop);
        removeButton.setOnClickListener(v -> {
            stopsContainer.removeView(stopView);
            stopCount--;
            addStopButton.setEnabled(true);
        });
        stopsContainer.addView(stopView);
    }

    private void calculateRoute() {
        if (startLocation == null || startLocation.isEmpty() || (!isRoundTrip && (destination == null || destination.isEmpty()))) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<RouteRequest.Location> stops = new ArrayList<>();
        for (int i = 0; i < stopsContainer.getChildCount(); i++) {
            View stopView = stopsContainer.getChildAt(i);
            EditText stopEditText = stopView.findViewById(R.id.et_stop);
            stops.add(new RouteRequest.Location(stopEditText.getText().toString()));
        }

        // Salvar os dados no SharedPreferences
        saveRouteDataToSharedPreferences(startLocation, destination, stops, isRoundTrip);

        RouteRequest request = new RouteRequest(
                new RouteRequest.Location(startLocation),
                new RouteRequest.Location(destination),
                stops
        );

        RetrofitClient.getApiService().computeRoutes(request).enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RouteResponse routeResponse = response.body();
                    if (routeResponse.getRoutes() != null && !routeResponse.getRoutes().isEmpty()) {
                        List<Integer> optimizedIndexes = routeResponse.getRoutes().get(0).getOptimizedIntermediateWaypointIndex();
                        displayResults(optimizedIndexes);
                    } else {
                        Log.e(TAG, "Falha na resposta da API: Lista de rotas está vazia ou nula");
                        Toast.makeText(NewRoute.this, "Erro ao calcular rota: Lista de rotas está vazia ou nula", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Corpo de erro vazio";
                        Log.e(TAG, "Falha na resposta da API: " + response.message() + ", Corpo de erro: " + errorBody);
                        Toast.makeText(NewRoute.this, "Erro ao calcular rota: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler o corpo da resposta de erro", e);
                        Toast.makeText(NewRoute.this, "Erro ao calcular rota: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Log.e(TAG, "Erro na chamada da API: ", t);
                Toast.makeText(NewRoute.this, "Erro ao calcular rota", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRouteDataToSharedPreferences(String startLocation, String destination, List<RouteRequest.Location> stops, boolean isRoundTrip) {
        SharedPreferences sharedPreferences = getSharedPreferences("RoutePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("startLocation", startLocation);
        editor.putString("destination", destination);
        editor.putBoolean("isRoundTrip", isRoundTrip);

        // Salva as paradas como uma string JSON
        String stopsJson = new Gson().toJson(stops);
        editor.putString("stops", stopsJson);

        editor.apply();
    }

    private void displayResults(List<Integer> optimizedIndexes) {
        if (optimizedIndexes == null) {
            Log.e(TAG, "Lista de índices intermediários otimizada é nula");
            Toast.makeText(this, "Erro ao calcular rota: Lista de índices intermediários otimizada é nula", Toast.LENGTH_SHORT).show();
            return;
        }

        String origin = startLocation;
        String destination = this.destination;

        List<String> intermediates = new ArrayList<>();
        for (int i = 0; i < stopsContainer.getChildCount(); i++) {
            EditText stopEditText = (EditText) stopsContainer.getChildAt(i).findViewById(R.id.et_stop);
            intermediates.add(stopEditText.getText().toString());
        }

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("origin", origin);
        intent.putExtra("destination", destination);
        intent.putStringArrayListExtra("intermediates", (ArrayList<String>) intermediates);
        intent.putIntegerArrayListExtra("optimizedIndexes", (ArrayList<Integer>) optimizedIndexes);
        startActivity(intent);
    }
}

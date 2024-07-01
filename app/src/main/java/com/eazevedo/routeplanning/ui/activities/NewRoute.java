package com.eazevedo.routeplanning.ui.activities;

import android.content.Intent;
import android.net.Uri;
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
import com.eazevedo.routeplanning.ui.adapters.RouteRequest;
import com.eazevedo.routeplanning.ui.adapters.RouteResponse;
import com.eazevedo.routeplanning.services.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRoute extends AppCompatActivity {

    private static final String TAG = "NewRoute";

    private LinearLayout stopsContainer;
    private Button addStopButton;
    private EditText startLocation;
    private EditText destination;
    private Button calculateButton;
    private int stopCount = 0;
    private static final int MAX_STOPS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        startLocation = findViewById(R.id.et_start_location);
        destination = findViewById(R.id.et_destination);
        stopsContainer = findViewById(R.id.stops_container);
        addStopButton = findViewById(R.id.btn_add_stop);
        calculateButton = findViewById(R.id.btn_calculate);

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
        String originAddress = startLocation.getText().toString();
        String destinationAddress = destination.getText().toString();
        List<RouteRequest.Location> stops = new ArrayList<>();

        for (int i = 0; i < stopsContainer.getChildCount(); i++) {
            View stopView = stopsContainer.getChildAt(i);
            EditText stopEditText = stopView.findViewById(R.id.et_stop);
            stops.add(new RouteRequest.Location(stopEditText.getText().toString()));
        }

        RouteRequest request = new RouteRequest(
                new RouteRequest.Location(originAddress),
                new RouteRequest.Location(destinationAddress),
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

    private void displayResults(List<Integer> optimizedIndexes) {
        if (optimizedIndexes == null) {
            Log.e(TAG, "Lista de índices intermediários otimizada é nula");
            Toast.makeText(this, "Erro ao calcular rota: Lista de índices intermediários otimizada é nula", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder result = new StringBuilder("Ordem otimizada das paradas:\n");
        for (Integer index : optimizedIndexes) {
            result.append("Parada ").append(index).append("\n");
        }
        Log.i(TAG, result.toString());
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();

        String origin = startLocation.getText().toString();
        String destination = this.destination.getText().toString();

        List<String> intermediates = new ArrayList<>();
        for (int i = 0; i < stopsContainer.getChildCount(); i++) {
            EditText stopEditText = (EditText) stopsContainer.getChildAt(i).findViewById(R.id.et_stop);
            intermediates.add(stopEditText.getText().toString());
        }

        //para criar o url do google maps
        StringBuilder uriBuilder = new StringBuilder("https://www.google.com/maps/dir/?api=1&travelmode=driving");

        uriBuilder.append("&origin=").append(Uri.encode(origin));
        uriBuilder.append("&destination=").append(Uri.encode(destination));

        if (!optimizedIndexes.isEmpty()) {
            uriBuilder.append("&waypoints=");
            for (int i = 0; i < optimizedIndexes.size(); i++) {
                int index = optimizedIndexes.get(i);
                if (index >= 0 && index < intermediates.size()) {
                    uriBuilder.append(Uri.encode(intermediates.get(index)));
                    if (i < optimizedIndexes.size() - 1) {
                        uriBuilder.append("|");
                    }
                }
            }
        }

        String uriString = uriBuilder.toString();
        Log.i(TAG, "URI do Google Maps: " + uriString);

        // Inicie o Google Maps
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Google Maps não está instalado", Toast.LENGTH_SHORT).show();
        }
    }


}

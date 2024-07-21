package com.eazevedo.routeplanning.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eazevedo.routeplanning.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private String origin;
    private String destination;
    private ArrayList<String> intermediates;
    private ArrayList<Integer> optimizedIndexes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 segundos

        // Verificar e solicitar permissão de localização, se necessário
        requestLocationPermission();

        // Obter o SupportMapFragment e ser notificado quando o mapa estiver pronto para ser usado
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Recuperar dados da rota
        origin = getIntent().getStringExtra("origin");
        destination = getIntent().getStringExtra("destination");
        intermediates = getIntent().getStringArrayListExtra("intermediates");
        optimizedIndexes = getIntent().getIntegerArrayListExtra("optimizedIndexes");

        // Adicionar botão para abrir no Google Maps
        Button btnOpenGoogleMaps = findViewById(R.id.btn_open_google_maps);
        btnOpenGoogleMaps.setOnClickListener(v -> openInGoogleMaps());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Habilitar controles UI no mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Desenhar rota no mapa
        drawRoute();
    }

    private void drawRoute() {
        if (origin == null || destination == null || intermediates == null || optimizedIndexes == null) {
            Toast.makeText(this, "Dados da rota inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        List<LatLng> routePoints = new ArrayList<>();

        // Adicionar origem
        LatLng originLatLng = getLocationFromAddress(origin);
        routePoints.add(originLatLng);

        // Adicionar intermediários
        for (int index : optimizedIndexes) {
            if (index >= 0 && index < intermediates.size()) {
                LatLng waypoint = getLocationFromAddress(intermediates.get(index));
                routePoints.add(waypoint);
            }
        }

        // Adicionar destino
        LatLng destinationLatLng = getLocationFromAddress(destination);
        routePoints.add(destinationLatLng);

        // Configurar e adicionar polilinha ao mapa
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(routePoints)
                .width(10)
                .color(Color.BLUE);
        mMap.addPolyline(polylineOptions);

        // Ajustar a visualização do mapa para incluir toda a rota
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : routePoints) {
            builder.include(point);
        }
        LatLngBounds bounds = builder.build();
        int padding = 100; // Padding em pixels ao redor da rota
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }


    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng latLng = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }


    private void openInGoogleMaps() {
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
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Google Maps não está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Se a permissão não foi concedida, solicite-a
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            // Se a permissão já foi concedida, você pode proceder com a lógica para obter a localização
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, você pode proceder com a lógica para obter a localização
                startLocationUpdates();
            } else {
                // Permissão negada
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            // Aqui você pode usar a localização obtida (location)
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Exemplo de utilização: adicionar marcador no mapa
                            LatLng userLatLng = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(userLatLng).title("Sua Localização"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f));
                        }
                    }
                },
                null /* Looper */);
    }
}

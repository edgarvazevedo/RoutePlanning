package com.eazevedo.routeplanning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eazevedo.routeplanning.auth.LogInActivity;
import com.eazevedo.routeplanning.db.SubjectsActivity;
import com.eazevedo.routeplanning.ui.activities.HomeActivity;
import com.eazevedo.routeplanning.ui.activities.MapsActivity;
import com.eazevedo.routeplanning.ui.activities.NewRoute;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent;
        if (mFirebaseUser == null) {
            Log.i(TAG, "User not log in");
            // Not logged in, launch the Log In activity
            intent = new Intent(this, LogInActivity.class);
            //FLAG_ACTIVITY_NEW_TASK: this activity will become the start of a new task on this history stack
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //FLAG_ACTIVITY_CLEAR_TASK: this flag will cause any existing task that would be associated with the activity to be cleared before the activity is started
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            Log.i(TAG, "User log in");
            intent = new Intent(this, HomeActivity.class);
            //FLAG_ACTIVITY_NEW_TASK: this activity will become the start of a new task on this history stack
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //FLAG_ACTIVITY_CLEAR_TASK: this flag will cause any existing task that would be associated with the activity to be cleared before the activity is started
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log and toast
                        String msg = " token: " + token;
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, TAG + msg, Toast.LENGTH_SHORT).show();
                    }
                });

        Log.d(TAG, "Calling writeNewTrip method");
        writeNewTrip("tripId1", "Porto1", "Salamanca");

        // Read from the database
        readTrips();
    }

    private void writeNewTrip(String tripId, String start, String destination) {
        Log.d(TAG, "writeNewTrip called with tripId: " + tripId + ", start: " + start + ", destination: " + destination);
        Trip trip = new Trip(start, destination);
        mDatabase.child("trips").child(tripId).setValue(trip)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Trip successfully written!");
                        } else {
                            Log.w(TAG, "Failed to write trip", task.getException());
                        }
                    }
                });
    }



    private void readTrips() {
        Log.d(TAG, "Trip read entrou!");
        mDatabase.child("trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Trip trip = postSnapshot.getValue(Trip.class);
                    Log.d(TAG, "Trip read entrou 2! trip: " + trip);
                    if (trip != null) {
                        Log.d(TAG, "Trip start: " );
                    } else {
                        Log.d(TAG, "Trip is null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadTrip:onCancelled", databaseError.toException());
            }
        });
    }

    public static class Trip {
        public String start;
        public String destination;

        public Trip() {
            // Default constructor required for calls to DataSnapshot.getValue(Trip.class)
        }

        public Trip(String start, String destination) {
            this.start = start;
            this.destination = destination;
        }
    }
}

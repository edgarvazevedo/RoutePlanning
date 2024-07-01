package com.eazevedo.routeplanning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.eazevedo.routeplanning.auth.LogInActivity;
import com.eazevedo.routeplanning.db.SubjectsActivity;
import com.eazevedo.routeplanning.ui.activities.MapsActivity;
import com.eazevedo.routeplanning.ui.activities.NewRoute;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
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
            intent = new Intent(this, NewRoute.class);
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
                        Toast.makeText(MainActivity.this, TAG + msg, Toast.LENGTH_SHORT).show();
                    }
                });


    }
}

// Inicia a MapsActivity
       /* Intent intents = new Intent(this, MapsActivity.class);
        startActivity(intents);
        finish();*/ // Finaliza a MainActivity para não voltar a ela pressionando o botão de voltar

package com.eazevedo.routeplanning.db;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eazevedo.routeplanning.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddActivity extends AppCompatActivity {
    private static final String TAG = "AddActivity";
    EditText title, description;
    Button btAdd;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        btAdd = (Button) findViewById(R.id.add);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subject subject = new Subject();
                subject.setUid(mDatabaseReference.child("subjects").push().getKey());
                subject.setTitle(title.getText().toString());
                subject.setDescription(description.getText().toString());
                mDatabaseReference.child("subjects").child(subject.getUid()).setValue(subject, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error != null) {
                            Log.i(TAG, "Data could not be saved: error (" + error.getMessage()+")");
                        }else{
                            Log.i(TAG, "Data could not be saved ");
                        }
                    }
                });
                finish();
            }
        });


    }
}

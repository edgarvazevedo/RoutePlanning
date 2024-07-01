package com.eazevedo.routeplanning.db;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.eazevedo.routeplanning.MainActivity;
import com.eazevedo.routeplanning.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubjectsActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ListView lv;
    ArrayList<Subject> items;
    LVAdapter adapter;


    DatabaseReference mDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        lv = (ListView) findViewById(R.id.listview);
        items = new ArrayList<>();
        adapter = new LVAdapter(this, R.layout.subject_item,items);
        lv.setAdapter(adapter);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mDatabaseReference!=null) {

            mDatabaseReference.child("subjects").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    items.clear();
                    for (DataSnapshot subjectDataSnapshot : dataSnapshot.getChildren()) {
                        Subject subject = subjectDataSnapshot.getValue(Subject.class);
                        items.add(subject);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.optionmenu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int itemId = item.getItemId();
        if (itemId == R.id.add) {
            Toast.makeText(getApplicationContext(), "Add Selected", Toast.LENGTH_LONG).show();
            intent = new Intent(SubjectsActivity.this, AddActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_logout) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            intent = new Intent(SubjectsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);


            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
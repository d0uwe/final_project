/* Douwe van der Wal
 * 11042206
 *
 * Displays a listview containing all scores and whom owns the score.
 */

package com.example.douwe.final_pset;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class highscoresActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Map<String, Object> currentScores;
    private DatabaseReference ref = database.getReference("scores");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        mAuth = FirebaseAuth.getInstance();
        ref.addValueEventListener(new dataChangeListener());
    }

    // update listview if data changed in the database
    private class dataChangeListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            currentScores = (Map<String, Object>) dataSnapshot.getValue();
            currentScores = (Map<String, Object>) currentScores.get("users");
            Log.d("getdata", "Value is: " + currentScores.toString());

            // for every user, add his score and name to the arraylist
            ArrayList<String> highscores = new ArrayList<>();
            for ( String key : currentScores.keySet() ) {
                highscores.add(currentScores.get(key).toString() + " " + key);
            }

            // display highscores in listview
            ListView listViewer = findViewById(R.id.viewer);
            ArrayAdapter listAdapter = new ArrayAdapter(highscoresActivity.this,
                    android.R.layout.simple_list_item_1, highscores);
            listViewer.setAdapter(listAdapter);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w("getdata", "Failed to read value.", error.toException());
        }
    }
}


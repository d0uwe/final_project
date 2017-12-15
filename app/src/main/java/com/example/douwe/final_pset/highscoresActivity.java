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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class highscoresActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Map<String, Object> current_scores;
    DatabaseReference ref = database.getReference("scores");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        ref.addValueEventListener(new dataChangeListener());
    }

    // update listview if data changed in the database
    private class dataChangeListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            current_scores = (Map<String, Object>) dataSnapshot.getValue();
            current_scores = (Map<String, Object>)current_scores.get("users");
            Log.d("getdata", "Value is: " + current_scores.toString());

            // for every user, add his score and name to the arraylist
            ArrayList<String> highscores = new ArrayList<>();
            for ( String key : current_scores.keySet() ) {
                highscores.add(current_scores.get(key).toString() + " " + key);
            }

            // display highscores in listview
            ListView listviewer = findViewById(R.id.viewer);
            ArrayAdapter listadapter = new ArrayAdapter(highscoresActivity.this,
                    android.R.layout.simple_list_item_1, highscores);
            listviewer.setAdapter(listadapter);
        }
        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w("getdata", "Failed to read value.", error.toException());
        }
    }
}


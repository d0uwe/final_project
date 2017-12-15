/* Douwe van der Wal
 * 11042206
 *
 * Allows a logged in user to answer question, increment his score by hand, view the highscores
 * or logout.
 */

package com.example.douwe.final_pset;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class loggedInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private HashMap<String, Object> current_scores;
    private DatabaseReference ref = database.getReference("scores");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        Button butt = findViewById(R.id.buttonlogout);
        butt.setOnClickListener(new HandleClickLogout());

        butt = findViewById(R.id.increment);
        butt.setOnClickListener(new HandleClickIncrement());

        butt = findViewById(R.id.highscore);
        butt.setOnClickListener(new HandleClickHighscores());

        butt = findViewById(R.id.question);
        butt.setOnClickListener(new HandleClickQuestion());

        mAuth = FirebaseAuth.getInstance();
        ref.addValueEventListener(new dataChangeListener());
    }

    // save current scores if activity is no longer on foreground
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("hashmap", current_scores);
    }

    // restore current scores in variable, which allows us to update the score.
    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        current_scores = (HashMap<String, Object>) inState.getSerializable("hashmap");
    }

    // sign a user out
    private class HandleClickLogout implements View.OnClickListener {
        public void onClick(View view) {
            mAuth.signOut();
            loggedInActivity.this.finish();
        }
    }

    // increment users score bye one
    private class HandleClickIncrement implements View.OnClickListener {
        public void onClick(View view) {
            // get database containing all scores
            DatabaseReference usersRef = ref.child("users");
            Map<String, Object> users = new HashMap<>();
            // get current user and his current score from the database
            final FirebaseUser currentUser = mAuth.getCurrentUser();
            long currentscore = (long)current_scores.get(currentUser.getDisplayName());
            // update score
            users.put(mAuth.getCurrentUser().getDisplayName(), currentscore + 1);
            usersRef.updateChildren(users);
        }
    }

    // go to the highscore activity
    private class HandleClickHighscores implements View.OnClickListener {
        public void onClick(View view) {
            Intent intent = new Intent(loggedInActivity.this, highscoresActivity.class);
            startActivity(intent);
        }
    }

    // go the the questionActivity activity
    private class HandleClickQuestion implements View.OnClickListener {
        public void onClick(View view) {
            Intent intent = new Intent(loggedInActivity.this, questionActivity.class);
            startActivity(intent);
        }
    }

    // update textview if data changed in the database
    private class dataChangeListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            final FirebaseUser currentUser = mAuth.getCurrentUser();
            current_scores = (HashMap<String, Object>) dataSnapshot.getValue();
            current_scores = (HashMap<String, Object>)current_scores.get("users");
            TextView welcomeview = findViewById(R.id.textView2);
            welcomeview.setText("welcome " + currentUser.getDisplayName() + " current score: " +
                    current_scores.get(currentUser.getDisplayName()));
            Log.d("getdata", "Value is: " + current_scores.toString());
        }
        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w("getdata", "Failed to read value.", error.toException());
        }
    }
}

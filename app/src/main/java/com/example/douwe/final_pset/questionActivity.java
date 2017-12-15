/* Douwe van der Wal
 * 11042206
 *
 * Gets a question from the numbers API and displays this to the user. If the user presses the
 * submit button, the answer is retrieved and checked. 5000 points are awarded for a good answer.
 */

package com.example.douwe.final_pset;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class questionActivity extends AppCompatActivity {
    String currentAnswer;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Map<String, Object> currentScores;
    DatabaseReference ref = database.getReference("scores");
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Button submitButt = findViewById(R.id.submit);
        submitButt.setOnClickListener(new HandleClickSubmit());
        mAuth = FirebaseAuth.getInstance();
        ref.addValueEventListener(new dataChangeListener());
        getString();
    }

    // request a new question from the api
    public String getString(){
        String url = "http://numbersapi.com/random/trivia?max=2000000";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                new apiResponseListener(), new apiResponseErrorListener());
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return "done";
    }

    // display the retrieved question, but replacing the answer (always in the first position)
    // with "what".
    void parseResponse(String response){
        TextView question = findViewById(R.id.questiontextview);
        // split text in answer and question
        String parts[] = response.split(" ", 2);
        currentAnswer = parts[0];
        question.setText("what " + parts[1]);
    }

    // wait for a response from the api and send this to the processing function
    private class apiResponseListener implements Response.Listener<String> {
        @Override
        public void onResponse(String response) {
            parseResponse(response);
        }
    }

    // print an error to the console
    private class apiResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("volley error" + error.toString());
        }
    }

    // check if the input of the user is corret
    private class HandleClickSubmit implements View.OnClickListener {
        public void onClick(View view) {
            // hide keyboard
            View focusView = questionActivity.this.getCurrentFocus();
            if (focusView != null) {
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
            // process answer
            EditText numberEditor = findViewById(R.id.numberinput);
            TextView resultView = findViewById(R.id.answertextview);
            String inputString = numberEditor.getText().toString();
            numberEditor.setText("");
            // get the next question if nothing was entered
            int input;
            try {
                input = Integer.parseInt(inputString);
            } catch (Exception e) {
                resultView.setText("You entered invalid input. We moved on to the next question");
                getString();
                return;
            }

            // string is a valid int, check if it's the correct answer
            if (input == Integer.parseInt(currentAnswer)) {
                resultView.setText("You answered the last question correct, you earned 5000 points");
                // reward 5000 points
                DatabaseReference usersRef = ref.child("users");
                // get current score to add the 5000 points to
                Map<String, Object> users = new HashMap<>();
                final FirebaseUser currentUser = mAuth.getCurrentUser();
                long currentScore = (long) currentScores.get(currentUser.getDisplayName());
                users.put(mAuth.getCurrentUser().getDisplayName(), currentScore + 5000);
                usersRef.updateChildren(users);
            } else {
                resultView.setText("Wrong, the true answer on the last question was " +
                        currentAnswer);
            }
            // get a new question
            getString();
        }
    }

    // keep track of the lastest scores in the database, so a score is updated to the right amount
    private class dataChangeListener implements ValueEventListener {
        @Override
        // update known scores for all users
        public void onDataChange(DataSnapshot dataSnapshot) {
            // this method is called once with the initial value and again
            // whenever data at this location is updated
            currentScores = (Map<String, Object>) dataSnapshot.getValue();
            currentScores = (Map<String, Object>) currentScores.get("users");
        }
        @Override
        // do nothing untill the next sucessful attempt
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w("getdata", "Failed to read value.", error.toException());
        }
    }
}

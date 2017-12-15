/* Douwe van der Wal
 * 11042206
 *
 * Shows the login screen to users who are not logged in yet or start a registration to be able
 * to log in.
 */

package com.example.douwe.final_pset;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // set listeners for all buttons
        Button butt = findViewById(R.id.registerButton);
        butt.setOnClickListener(new HandleClickRegister());
        butt = findViewById(R.id.submit);
        butt.setOnClickListener(new HandleClickLogin());
    }

    // if user is logged-in, move to the next screen, else do nothing
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, loggedInActivity.class);
            startActivity(intent);
        }
    }

    // start register activity
    private class HandleClickRegister implements View.OnClickListener {
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, registerActivity.class);
            startActivity(intent);
        }
    }

    // log a user in using provided password and username
    private class HandleClickLogin implements View.OnClickListener {
        public void onClick(View view) {
            // get password and username
            EditText emailText = findViewById(R.id.editText);
            EditText pwText = findViewById(R.id.editText2);
            String email = emailText.getText().toString();
            String password = pwText.getText().toString();

            // attempt sign-in
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new logInCompleteListener());
        }
    }

    // check if user succesfully logs in with given credentials and show next screen or error.
    private class logInCompleteListener implements OnCompleteListener<AuthResult> {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            String TAG = "log in";
            TextView errorView = findViewById(R.id.errortext);
            if (task.isSuccessful()) {
                // sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                // hide possible previous errors and go to the next activity
                errorView.setText("");
                updateUI(user);
            } else {
                // if sign in fails, display a message to the user
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                errorView.setText(task.getException().toString());
            }
        }
    }
}
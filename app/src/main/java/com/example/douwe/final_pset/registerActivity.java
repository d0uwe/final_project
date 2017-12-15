/* Douwe van der Wal
 * 11042206
 *
 * Allows the user to enter his email, password and username which will then be put into the
 * database allowing the user to log in from now on.
 */

package com.example.douwe.final_pset;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class registerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // set onclick listener and get authentication instance
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new HandleClickSubmit());

        mAuth = FirebaseAuth.getInstance();
    }

    // register a user using provided credentials
    private class HandleClickSubmit implements View.OnClickListener {
        public void onClick(View view) {
            // get provided username, password and displayname
            EditText emailEditText = findViewById(R.id.editText);
            EditText passwordEditText = findViewById(R.id.editText2);
            EditText displaynameEditText = findViewById(R.id.displayname);

            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();
            username = displaynameEditText.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(registerActivity.this, new onDoneCreatingUser());
        }
    }

    private class onDoneCreatingUser implements OnCompleteListener<AuthResult>{
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // sign in success, update UI with the signed-in user's information
                Log.d("succeed", "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();

                // set chosen displayname
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username).build();
                user.updateProfile(profileUpdates);

                // give user a score of 0, so a check if a score exists is redundant
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("scores");
                DatabaseReference usersRef = ref.child("users");
                Map<String, Object> users = new HashMap<>();
                users.put(username, 0);
                usersRef.updateChildren(users);

                // registration finished, close activity
                registerActivity.this.finish();
            } else {
                // if sign in fails, display a message to the user
                Log.w("no", "createUserWithEmail:failure", task.getException());
                TextView errorText = findViewById(R.id.errortext);
                errorText.setText(task.getException().toString());
            }
        }
    }
}

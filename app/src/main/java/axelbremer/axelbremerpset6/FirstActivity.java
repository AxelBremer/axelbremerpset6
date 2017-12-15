package axelbremer.axelbremerpset6;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This activity contains the login and register page where a user can log in using e-mail and
 * password. Or register a new account using email and password.
 */

public class FirstActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference mDatabase;
    String email;
    String password;
    EditText emailEditText;
    EditText passwordEditText;


    /**
     * Initializes the firebase database and authorization objects and finds the editTexts.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        mDatabase = db.getReference();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    /**
     * Checks if the given password is longer than 6 characters and creates a user using the given
     * firebase method. A new favorites object is also created and added to the database. After
     * logging in it sends the user to the search activity.
     */
    public void createUser(View view) {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if(password.length() > 6) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                Log.d("CREATEUSER", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                makeNewFavorites(user, email);
                                Intent intent = new Intent(FirstActivity.this, SearchActivity.class);
                                Toast.makeText(FirstActivity.this, "User created.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(intent);

                            } else {

                                // If sign in fails, display a message to the user.
                                Log.w("CREATEUSER", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(FirstActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        } else {
            Toast.makeText(FirstActivity.this, "Password must be more than 6 characters.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a new favorites object and adds it to the database. Also adds the new user to the
     * email to uid table in the database.
     */
    private void makeNewFavorites(FirebaseUser user, String email) {
        String uid = user.getUid();
        Favorites fav = new Favorites();

        String dbEmail = email.replace('.', ',');

        mDatabase.child("favorites").child(uid).setValue(fav);
        mDatabase.child("emailToUid").child(dbEmail).setValue(uid);
        Log.d("FAVORITES", "makeNewFavorites: ");
    }

    /**
     * Signs in the user using the given firebase method. After logging in it sends the user
     * to the Search activity.
     */
    public void signIn(View view) {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN", "signInWithEmail:success");
                            Intent intent = new Intent(FirstActivity.this, SearchActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                            Toast.makeText(FirstActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

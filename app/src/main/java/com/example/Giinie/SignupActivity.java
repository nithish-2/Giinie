package com.example.Giinie;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private Button signupButton;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    private TextInputEditText nameEditText;

    private TextInputEditText confirmEditText;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Set your back icon here
        }

        signupButton = findViewById(R.id.signupButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmEditText = findViewById(R.id.confirmPasswordEditText); // Initialize this view too
        nameEditText = findViewById(R.id.nameEditText); // Initialize the nameEditText view

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Create a GoogleSignInClient instance
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement signup logic here
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmEditText.getText().toString().trim(); // Add this line

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                    if (password.equals(confirmPassword)) { // Check if passwords match
                        // Firebase email/password signup
                        signUpWithEmailPassword(name, email, password);
                    } else {
                        Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Please enter name, email, password, and confirm password", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Find the Google Sign-In button by its ID
        SignInButton googleSignInButton = findViewById(R.id.googleSignInButton);

        // Set a click listener for the Google Sign-In button
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle(); // Call the Google Sign-In method
            }
        });
    }

    private void signUpWithEmailPassword(String name, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-up user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                String userEmail = user.getEmail();
                                saveUserEmail(userEmail);
                                insertUserToDatabase(name, userEmail);
                                openHomeScreen();
                            }
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void insertUserToDatabase(String name, String email) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        long userId = databaseHelper.getUserIdByEmail(email);

        if (userId == -1) {
            // User not present, insert into the database
            databaseHelper.insertUser(name, email);
        }
    }


    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN); // Launch the Google Sign-In activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, get the account details
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String displayName = account.getDisplayName();
                    String email = account.getEmail();

                    // Insert the user to the local database
                    insertUserToDatabase(displayName, email);

                    // Save the user's email to SharedPreferences
                    saveUserEmail(email);

                    // Open the home screen
                    openHomeScreen();
                }
            } catch (ApiException e) {
                // Google Sign-In failed, handle the error
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveUserEmail(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("skipLogin");
        editor.putString("userEmail", email);
        editor.apply();
    }

    private void openHomeScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

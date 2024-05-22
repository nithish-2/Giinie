package com.example.Giinie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView signupLinkTextView;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 123;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Set your back icon here
        }

        loginButton = findViewById(R.id.loginButton);
        signupLinkTextView = findViewById(R.id.signupLinkTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Create a GoogleSignInClient instance
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Create an ActivityResultLauncher to handle the result of Google Sign-In
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
                            if (account != null) {
                                // Google Sign-In was successful, you can handle the account here
                                String userName = account.getDisplayName();
                                Toast.makeText(this, "Google Sign-In Success. Welcome, " + userName + "!", Toast.LENGTH_SHORT).show();
                                String userEmail = account.getEmail();
                                saveUserEmail(userEmail);
                                openHomeScreen();
                            }
                        }
                    } else {
                        // Google Sign-In failed, show an error message
                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                        Log.e("GoogleSignIn", "Google Sign-In failed with result code: " + result.getResultCode());
                    }
                });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your custom login logic here
                String userEmail = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (!userEmail.isEmpty() && !password.isEmpty()) {
                    signInWithEmailPassword(userEmail, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupScreen();
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

    private void openSignupScreen() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
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

    private void insertUserToDatabase(String name, String email) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        long userId = databaseHelper.getUserIdByEmail(email);

        if (userId == -1) {
            // User not present, insert into the database
            databaseHelper.insertUser(name, email);
        }
    }



    private void signInWithEmailPassword(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Sign-in success, update UI with the signed-in user's information
                            // Authentication successful, show a toast
                            Toast.makeText(LoginActivity.this, "Authentication successful!", Toast.LENGTH_SHORT).show();
                            insertUserToDatabase("", email);
                            String userEmail = user.getEmail();
                            saveUserEmail(userEmail);
                            openHomeScreen();
                        }
                    } else {
                        // If sign-in fails, display a message to the user.
                        if (task.getException() instanceof FirebaseAuthException) {
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

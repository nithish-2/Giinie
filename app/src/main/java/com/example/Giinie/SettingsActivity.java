package com.example.Giinie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox checkboxNotification;
    private SwitchMaterial switchDarkMode;
    private MaterialButton buttonSave;

    private MaterialButton buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back); // Set your back icon here
        }

        checkboxNotification = findViewById(R.id.checkboxNotification);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        buttonSave = findViewById(R.id.buttonSave);

        // Load saved settings
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        checkboxNotification.setChecked(sharedPreferences.getBoolean("notification", true));
        switchDarkMode.setChecked(sharedPreferences.getBoolean("darkMode", false));

        checkboxNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Save notification setting
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("notification", isChecked);
                editor.apply();
            }
        });

        buttonLogout = findViewById(R.id.logoutButton);
        buttonLogout.setOnClickListener(view -> {
            // Clear user session data and navigate to the login activity
            SharedPreferences sharedPreferences1 = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.clear();
            editor.apply();

            // Optionally, you can navigate to the login activity here
            Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Close the current activity to prevent going back to the settings screen

            Toast.makeText(SettingsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Save dark mode setting
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("darkMode", isChecked);
                editor.apply();
            }
        });

        buttonSave.setOnClickListener(view -> {
            // Show a toast message when settings are saved
            Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            // Settings menu item clicked, do nothing since we are already in the settings activity
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

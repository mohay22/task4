package com.example.task4;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Use your main layout file

        // Initialize FirebaseApp
        if (FirebaseApp.getApps(getApplicationContext()).isEmpty()) {
            FirebaseApp.initializeApp(getApplicationContext());
        }

        if (savedInstanceState == null) {
            // Load DashboardFragment as the default fragment on initial launch
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DashboardFragment())  // Replace with DashboardFragment
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();  // Load DashboardFragment
            } else if (item.getItemId() == R.id.nav_uploaded_files) {
                selectedFragment = new UploadedFilesActivity();  // Example: Replace with your actual fragment or activity
            } else if (item.getItemId() == R.id.nav_upload) {
                selectedFragment = new UploadFilesActivity();  // Example: Replace with your actual fragment or activity
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileActivity();  // Example: Replace with your actual fragment or activity
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)  // Replace with the selected fragment
                        .commit();
            }

            return true;
        });

    }
}

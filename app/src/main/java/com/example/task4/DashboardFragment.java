package com.example.task4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardFragment extends Fragment {

    private ImageView profileImage;
    private TextView userName, userEmail;
    private DatabaseReference userRef;
    private FirebaseAuth auth;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize Firebase if needed
        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            FirebaseApp.initializeApp(requireContext());
        }

        // Firebase Authentication
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userID == null) {
            Log.e("DashboardFragment", "User is not authenticated");
            return view;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);

        // Initialize UI Elements
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);

        // Load user profile from Firebase
        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String profilePicBase64 = snapshot.child("profileImage").getValue(String.class);  // Ensure this matches Firebase

                    if (name != null) userName.setText(name);
                    if (email != null) userEmail.setText(email);

                    if (profilePicBase64 != null && !profilePicBase64.isEmpty()) {
                        Bitmap decodedBitmap = decodeBase64ToBitmap(profilePicBase64);
                        if (decodedBitmap != null) {
                            profileImage.setImageBitmap(decodedBitmap);
                        } else {
                            Log.e("DashboardFragment", "Failed to decode profile image");
                        }
                    } else {
                        Log.e("DashboardFragment", "Profile picture is null or empty");
                    }
                } else {
                    Log.e("DashboardFragment", "User data does not exist in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardFragment", "DatabaseError: " + error.getMessage());
            }
        });
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            Log.e("DashboardFragment", "Invalid Base64 string", e);
            return null;
        }
    }
}

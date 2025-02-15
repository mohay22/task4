package com.example.task4;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ProfileActivity extends Fragment {

    private EditText editName, editEmail, editPassword;
    private ImageView profileImage;
    private Button btnUpdate, btnChangeImage;

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private String userId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String encodedImage;  // Store the Base64 string

    public void ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            FirebaseApp.initializeApp(requireContext());
        }
        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        }

        // Initialize Views
        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editPassword = view.findViewById(R.id.editPassword);
        profileImage = view.findViewById(R.id.profileImage);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnChangeImage = view.findViewById(R.id.btnChangeImage);

        // Load user data
        loadUserProfile();

        // Button to pick a new profile image
        btnChangeImage.setOnClickListener(v -> openImageChooser());

        // Button to update user info
        btnUpdate.setOnClickListener(v -> updateUserProfile());

        return view;
    }

    private void loadUserProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String profilePicBase64 = snapshot.child("profileImage").getValue(String.class);

                    editName.setText(name);
                    editEmail.setText(email);

                    if (profilePicBase64 != null && !profilePicBase64.isEmpty()) {
                        profileImage.setImageBitmap(decodeBase64(profilePicBase64));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Convert image to Base64
                encodedImage = encodeToBase64(selectedImage);
                profileImage.setImageBitmap(selectedImage); // Set the new image
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUserProfile() {
        String newName = editName.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();

        if (!newName.isEmpty()) {
            userRef.child("name").setValue(newName);
        }

        if (!newEmail.isEmpty()) {
            auth.getCurrentUser().updateEmail(newEmail)
                    .addOnSuccessListener(aVoid -> userRef.child("email").setValue(newEmail))
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update email", Toast.LENGTH_SHORT).show());
        }

        if (!newPassword.isEmpty()) {
            auth.getCurrentUser().updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Password updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show());
        }

        if (encodedImage != null) {
            userRef.child("profileImage").setValue(encodedImage);
        }

        Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }

    // Convert Bitmap to Base64
    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Convert Base64 to Bitmap
    private Bitmap decodeBase64(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}

package com.example.task4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadFilesActivity extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnPickImage, btnUpload;
    private ImageView imgSelected;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_upload_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            FirebaseApp.initializeApp(requireContext());
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUserId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("uploads");

        // Bind views
        btnPickImage = view.findViewById(R.id.btnPickImage);
        btnUpload = view.findViewById(R.id.btnUpload);
        imgSelected = view.findViewById(R.id.imgSelected);

        // Set button listeners
        btnPickImage.setOnClickListener(v -> openFileChooser());
        btnUpload.setOnClickListener(v -> uploadFile());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgSelected.setImageURI(imageUri);
        }
    }

    private void uploadFile() {
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] data = baos.toByteArray();

                // Convert the byte array to Base64 string
                String base64String = Base64.encodeToString(data, Base64.DEFAULT);

                // Store the Base64 string in Firebase Database under the user node
                String fileId = databaseReference.push().getKey();
                databaseReference.child(fileId).setValue(base64String);

                Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Image processing failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}

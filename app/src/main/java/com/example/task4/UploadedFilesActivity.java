package com.example.task4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UploadedFilesActivity extends Fragment {

    private RecyclerView recyclerViewUploadedByYou, recyclerViewUploadedByOthers;
    private UploadedFilesAdapter adapterUploadedByYou, adapterUploadedByOthers;
    private List<String> fileListByYou, fileListByOthers;
    private String currentUserId;
    private TextView titleUploadedByYou, titleUploadedByOthers;
    private boolean isUserDataLoaded = false, isOthersDataLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uploaded_files, container, false);

        // Initialize Views
        recyclerViewUploadedByYou = view.findViewById(R.id.recyclerViewUploadedByYou);
        recyclerViewUploadedByOthers = view.findViewById(R.id.recyclerViewUploadedByOthers);
        titleUploadedByYou = view.findViewById(R.id.titleUploadedByYou);
        titleUploadedByOthers = view.findViewById(R.id.titleUploadedByOthers);

        // Set LayoutManagers
        recyclerViewUploadedByYou.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUploadedByOthers.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Lists
        fileListByYou = new ArrayList<>();
        fileListByOthers = new ArrayList<>();

        // Get Current User ID from Firebase Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        } else {
            currentUserId = ""; // Handle case where user is not logged in
        }

        // Load Files
        loadFilesUploadedByUser();
        loadFilesUploadedByOthers();

        return view;
    }

    private void loadFilesUploadedByUser() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId).child("uploads");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fileListByYou.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String base64Image = snapshot.getValue(String.class);
                    if (base64Image != null) {
                        fileListByYou.add(base64Image);
                    }
                }

                // Set Adapter
                adapterUploadedByYou = new UploadedFilesAdapter(fileListByYou, getContext(), currentUserId);
                recyclerViewUploadedByYou.setAdapter(adapterUploadedByYou);

                // Show/Hide Section
                isUserDataLoaded = !fileListByYou.isEmpty();
                updateSectionVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to retrieve data", databaseError.toException());
            }
        });
    }

    private void loadFilesUploadedByOthers() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fileListByOthers.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (!userSnapshot.getKey().equals(currentUserId)) {
                        for (DataSnapshot fileSnapshot : userSnapshot.child("uploads").getChildren()) {
                            String base64Image = fileSnapshot.getValue(String.class);
                            if (base64Image != null) {
                                fileListByOthers.add(base64Image);
                            }
                        }
                    }
                }

                // Set Adapter
                adapterUploadedByOthers = new UploadedFilesAdapter(fileListByOthers, getContext(), currentUserId);
                recyclerViewUploadedByOthers.setAdapter(adapterUploadedByOthers);

                // Show/Hide Section
                isOthersDataLoaded = !fileListByOthers.isEmpty();
                updateSectionVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to retrieve data", databaseError.toException());
            }
        });
    }

    private void updateSectionVisibility() {
        titleUploadedByYou.setVisibility(isUserDataLoaded ? View.VISIBLE : View.GONE);
        recyclerViewUploadedByYou.setVisibility(isUserDataLoaded ? View.VISIBLE : View.GONE);

        titleUploadedByOthers.setVisibility(isOthersDataLoaded ? View.VISIBLE : View.GONE);
        recyclerViewUploadedByOthers.setVisibility(isOthersDataLoaded ? View.VISIBLE : View.GONE);
    }
}

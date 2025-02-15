package com.example.task4;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.OutputStream;
import java.util.List;

public class UploadedFilesAdapter extends RecyclerView.Adapter<UploadedFilesAdapter.FileViewHolder> {

    private List<String> fileList;
    private Context context;
    private final String currentUserId;

    public UploadedFilesAdapter(List<String> fileList, Context context, String currentUserId) {
        this.fileList = fileList;
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        String base64Image = fileList.get(position);

        if (base64Image != null) {
            Bitmap decodedImage = decodeBase64ToBitmap(base64Image);

            if (decodedImage != null) {
                holder.imageView.setImageBitmap(decodedImage);

                // Set click listener to save image when clicked
                holder.imageView.setOnClickListener(v -> {
                    boolean success = saveImageToGallery(decodedImage);
                    if (success) {
                        Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewFile);
        }
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean saveImageToGallery(Bitmap bitmap) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "Image_" + System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            OutputStream outputStream = context.getContentResolver().openOutputStream(
                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            );

            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

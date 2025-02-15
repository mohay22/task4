package com.example.task4;

public class FileModel {
    private String imageBase64;

    // Default constructor for Firebase
    public FileModel() {}

    // Constructor with the imageBase64 field
    public FileModel(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    // Getter and setter
    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}

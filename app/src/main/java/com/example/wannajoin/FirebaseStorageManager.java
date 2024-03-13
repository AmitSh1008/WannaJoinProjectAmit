package com.example.wannajoin;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FirebaseStorageManager {
    private static final String TAG = "FirebaseStorageManager";

    // Method to retrieve all files and their access tokens from a specific subfolder in Firebase Storage and return the map
    public interface FirebaseStorageCallback {
        void onFilesReceived(Map<String, String> fileTokenMap);
        void onFailure(Exception e);
    }

    public static void getFilesFromSubfolder(String subfolderName, FirebaseStorageCallback callback) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(subfolderName);

        storageRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    Map<String, String> fileTokenMap = new HashMap<>();

                    for (StorageReference item : task.getResult().getItems()) {
                        String fileName = item.getName().replace(".jpeg", "");

                        // Get the download URL (access token) for each file
                        item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> uriTask) {
                                if (uriTask.isSuccessful()) {
                                    String downloadUrl = uriTask.getResult().toString();
                                    fileTokenMap.put(fileName, downloadUrl);

                                    // Check if all files are processed
                                    if (fileTokenMap.size() == task.getResult().getItems().size()) {
                                        // Return the map via the callback
                                        callback.onFilesReceived(fileTokenMap);
                                    }
                                } else {
                                    callback.onFailure(uriTask.getException());
                                }
                            }
                        });
                    }
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }
}

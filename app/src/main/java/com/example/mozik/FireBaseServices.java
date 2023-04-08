package com.example.mozik;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseServices {
    private static FireBaseServices Instance;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirebaseAuth getmAuth() {
        return mAuth;
    }
    public FirebaseFirestore getDb() {
        return db;
    }

    public static FireBaseServices getinstance() {
        if (Instance == null) {
            Instance = new FireBaseServices();
        }
        return Instance;
    }
}


package com.example.mozik;

import com.google.firebase.auth.FirebaseAuth;

public class FireBaseServices {
    private static FireBaseServices Instance;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public static FireBaseServices getinstance() {
        if (Instance == null) {
            Instance = new FireBaseServices();
        }
        return Instance;
    }
}


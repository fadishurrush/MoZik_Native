package com.example.mozik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextOfApplication = getApplicationContext();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.framelayout, new Sign_In_Frag());
//        ft.commit();
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, new HomePage_Frag());
        ft.commit();
    }

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
}
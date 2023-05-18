package com.example.mozik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        contextOfApplication = getApplicationContext();

    }

    public NotificationManager notificationManager;
    private BottomNavigationView bn;


    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayShowHomeEnabled(true);

            FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.framelayout, new Sign_In_Frag());
            ft.commit();
        bn = findViewById(R.id.bottomNavBar);

        bn.setOnItemSelectedListener(item ->{

            switch (item.getTitle().toString()){

                case "Profile":
                {
                    replaceFragment(new ProfileFragment());
                    return true;

                }
                case "Home":
                default:{
                    replaceFragment(new HomePage_Frag());
                    return true;

                }

            }
    });
    }
        private void replaceFragment(Fragment fragment){
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.framelayout,fragment);
            fragmentTransaction.commit();
        }
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(App.CHANNEL_ID,"Mozik",notificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null ){
                manager.createNotificationChannel(serviceChannel);
            }

        }
    }

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
}
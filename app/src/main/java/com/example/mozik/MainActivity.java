package com.example.mozik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

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

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayShowHomeEnabled(true);

            FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.framelayout, new Sign_In_Frag());
            ft.commit();
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
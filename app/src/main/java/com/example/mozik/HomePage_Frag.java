package com.example.mozik;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePage_Frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePage_Frag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Variables
    private RecyclerView recyclerView;
    private TextView tvNomusic;
    private ArrayList<AudioModel> songslist = new ArrayList<>();
    public HomePage_Frag() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        connect();
        if(checkPermission()== false){
            requestPermission();
            return;
        }
        Context applicationContext = MainActivity.getContextOfApplication();
        applicationContext.getContentResolver();
        String[] projection={
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";
        Cursor cursor = applicationContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
        while (cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if(new File(songData.getPath()).exists()){
                songslist.add(songData);
            }

        }
        if(songslist.size()==0){
            tvNomusic.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new MusicListAdapter(songslist,applicationContext));
        }
    }

    private void connect() {
        recyclerView=getView().findViewById(R.id.RecyclerView);
        tvNomusic=getView().findViewById(R.id.tvNoSongs);
    }
    public boolean checkPermission(){
        int result= ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }
    public void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){

        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
        }

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePage_Frag.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePage_Frag newInstance(String param1, String param2) {
        HomePage_Frag fragment = new HomePage_Frag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page_, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songslist, getActivity().getApplicationContext()));
        }
    }
}
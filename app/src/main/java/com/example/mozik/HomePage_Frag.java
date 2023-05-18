package com.example.mozik;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.mozik.Sign_In_Frag.SHARED_PREF;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private SearchView searchView;
    private ArrayList<AudioModel> songslist = new ArrayList<>();
    private ArrayList<AudioModel> favorites = new ArrayList<>();
    private MusicListAdapter musicListAdapter;
    private FirebaseFirestore db = FireBaseServices.getinstance().getDb();
    private FirebaseAuth mAuth = FireBaseServices.getinstance().getmAuth();

    private BottomNavigationView bn ;
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });
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
            recyclerView.setAdapter(musicListAdapter);
        }

    }

    private void read() {
        db.collection("Favs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    if (document.get("user").equals(mAuth.getUid())) {
                                        Log.d(TAG, "same user: "+document);
                                        for (AudioModel song: songslist
                                             ) {
                                            if (song.getTitle().equals(document.get("fav"))){
                                                favorites.add(song);
                                            }
                                        }
                                    }
                                }
                            }
                            if (favorites.size()==0){
                                tvNomusic.setVisibility(View.VISIBLE);
                            }else{
                                tvNomusic.setVisibility(View.GONE);

                                musicListAdapter.setSongslist(favorites);

                            }
                            Log.d(TAG, "onComplete: " + favorites);
                        } else {
                            tvNomusic.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void filterList(String text) {
     ArrayList<AudioModel> filteredList = new ArrayList<>();
        for (AudioModel song:
             songslist) {
            if (song.getTitle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(song);
            }
            if (filteredList.isEmpty()){
                musicListAdapter.setFilteredList(filteredList);
                tvNomusic.setVisibility(View.VISIBLE);
            }else{
                musicListAdapter.setFilteredList(filteredList);
                tvNomusic.setVisibility(View.GONE);

            }
        }
    }

    private void connect() {
        recyclerView=getView().findViewById(R.id.RecyclerView);
        tvNomusic=getView().findViewById(R.id.tvNoSongs);
        bn= getActivity().findViewById(R.id.bottomNavBar);
        bn.setVisibility(View.VISIBLE);
        searchView=getView().findViewById(R.id.search_bar);
        searchView.clearFocus();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        });
        searchView.setQueryHint("Search here");
        musicListAdapter=new MusicListAdapter(songslist,getContext());

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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("Favorites");
        menu.add("in Device");
        menu.add("log out");


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getTitle().toString()){
            case "Favorites" :{
               read();
                Toast.makeText(getContext(), " fav selected", Toast.LENGTH_SHORT).show();
                return true;
            }
            case "in Device" :{
                musicListAdapter.setSongslist(songslist);
                Toast.makeText(getContext(), " device selected", Toast.LENGTH_SHORT).show();
                return true;

            }
            case "log out" :{
                mAuth.signOut();
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name" , "false");
                editor.apply();
                FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.framelayout, new Sign_In_Frag());
                ft.commit();
                Toast.makeText(getContext(), "signed out.", Toast.LENGTH_SHORT).show();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
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
            musicListAdapter.setFilteredList(songslist);
        }
    }
}
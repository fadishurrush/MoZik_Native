package com.example.mozik;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    // variables

    private FireBaseServices fbs = FireBaseServices.getinstance();

    private FirebaseFirestore db = fbs.getDb();

    private StorageReference storageReference;

    private FirebaseAuth mAuth=fbs.getmAuth();

    private ProgressBar progressBar;

    private Uri selectedImage;

    ProgressDialog progressDialog;

    boolean flag = true;
    private ImageView avatar;
    private EditText name;
    private Button gotoFav,save,edit;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        connect();
        pagesetup();
//        editlistner();
        savelistener();
        setavatar();
        profileimage();


    }

    private void editlistner() {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFav.setVisibility(View.GONE);
                name.setFocusable(true);
                name.setClickable(true);
                avatar.setFocusable(true);
                avatar.setClickable(true);

            }
        });
    }

    private void connect() {
        avatar = getView().findViewById(R.id.profileImage);
        gotoFav = getView().findViewById(R.id.goTofav);
        save = getView().findViewById(R.id.Save);
        edit = getView().findViewById(R.id.edit);
        name = getView().findViewById(R.id.nickname);
//        name.setFocusable(false);
//        name.setClickable(false);
        save.setVisibility(View.GONE);
        gotoFav.setVisibility(View.VISIBLE);
        progressBar=getView().findViewById(R.id.loadingProfile);
        if (flag){
            progressBar.setVisibility(View.VISIBLE);

        }else{
            progressBar.setVisibility(View.GONE);
        }
    }
    private void pagesetup() {
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if (document != null){
                                    if (document.get("Uid").equals(mAuth.getUid())){
                                        if (document.get("name") == null){
                                            name.setText("");
                                            name.setHint("name...");
                                        }else {
                                            name.setText(document.get("name").toString());
                                        }

                                    }
                                }
                            }
                        }
                    }
                });
    }
    private void savelistener() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "you left the name field empty :(", Toast.LENGTH_SHORT).show();
                }
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("saving...");
                progressDialog.show();
                db.collection(("Users")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    if (document.get("Uid").equals(mAuth.getUid())) {
                                        db.collection("Users").document(document.getId()).update("name", name.getText().toString());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });

                storageReference = fbs.getStorage().getReference("avatars/" + mAuth.getUid() + ".jpg");
                if (selectedImage == null) {
                } else {

                try {
                    storageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getContext(), "saved.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getContext(), "failed to save.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "saving image excep : ", e);
                }
            }
            }

        });
    }
    private void setavatar() {
        if (flag){
            flag=false;
            storageReference= fbs.getStorage().getReference("avatars/"+mAuth.getUid()+".jpg");
            try {
                File localfile = File.createTempFile("tempfile",".jpg");
                storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        Log.d(TAG, "setavatar() called" + bitmap);
                        progressBar.setVisibility(View.GONE);
                        avatar.setImageBitmap(bitmap);
                    }

                });


            }catch (IOException e){
                Log.e(TAG, "setavatar: exep" , e );
            }
        }
    }
    private void profileimage() {
//        avatar.setClickable(false);
//        avatar.setFocusable(false);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = false;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
                progressBar.setVisibility(View.GONE);
            }
        });


    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null){
            selectedImage = data .getData();
            avatar.setImageURI(selectedImage);

        }
    }
}
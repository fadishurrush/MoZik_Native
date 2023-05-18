package com.example.mozik;

import static android.content.ContentValues.TAG;

import static com.example.mozik.Sign_In_Frag.SHARED_PREF;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Register_Frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Register_Frag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Variables
    private FireBaseServices fbs = FireBaseServices.getinstance();
    private FirebaseUser mUser;
    private FirebaseFirestore db=fbs.getDb();
    private Button btnRegister,btnGoToLogin;
    private EditText etemailRegister,etpasswordRegister,etconfirmpasswordRegister;
    private FirebaseAuth mAuth=fbs.getmAuth();
    private BottomNavigationView bn ;

    public Register_Frag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Register_Frag.
     */
    // TODO: Rename and change types and number of parameters
    public static Register_Frag newInstance(String param1, String param2) {
        Register_Frag fragment = new Register_Frag();
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
        return inflater.inflate(R.layout.fragment_register_, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        connectComponents();
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.framelayout, new Sign_In_Frag());
                ft.commit();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });
    }
    public void Check(){
        String email = etemailRegister.getText().toString().trim();
        String password = etpasswordRegister.getText().toString().trim();
        String confirmpassword = etconfirmpasswordRegister.getText().toString().trim();
        valid(email,password,confirmpassword);

    }
    private void valid(String email,String password,String confpassword) {

        if(email.isEmpty()){
            Toast.makeText(getContext(), "email field is empty", Toast.LENGTH_SHORT).show();
            etemailRegister.requestFocus();
            return;
        }
        if(password.isEmpty()){
            Toast.makeText(getContext(), "password field is empty", Toast.LENGTH_SHORT).show();
            etpasswordRegister.requestFocus();
            return;
        }if(confpassword.isEmpty()){
            Toast.makeText(getContext(), "confirm password field is empty", Toast.LENGTH_SHORT).show();
            etconfirmpasswordRegister.requestFocus();
            return;
        }
        if(!confpassword.equals(password)){
            Toast.makeText(getContext(), "confirm password does not match", Toast.LENGTH_LONG).show();
            etconfirmpasswordRegister.requestFocus();
            return;
        }
        Log.d(TAG, "valid() called with: email = [" + email + "], password = [" + password + "], confpassword = [" + confpassword + "]");
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getContext(), "acc created", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name" , "true");
                        editor.apply();
                        CreateUser(getView());
                        Log.d(TAG, "onSuccess: acc created");
                        FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.framelayout, new HomePage_Frag());
                        ft.commit();
                        bn.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: failed");
                    }
                });

    }
    private void connectComponents() {
        etemailRegister = getView().findViewById(R.id.etEmailRegister);
        etpasswordRegister = getView().findViewById(R.id.etPasswordRegister);
        etconfirmpasswordRegister = getView().findViewById(R.id.etPasswordConfirm);
        btnRegister = getView().findViewById(R.id.btnRegister);
        btnGoToLogin = getView().findViewById(R.id.btnBackToLogin);
        bn = getActivity().findViewById(R.id.bottomNavBar);
        bn.setVisibility(View.GONE);
    }
    public void CreateUser(View view) {
        Map<String, Object> user = new HashMap<>();
        user.put("name",mAuth.getCurrentUser().getDisplayName());
        user.put("Uid", mAuth.getUid());


        db.collection("Users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}

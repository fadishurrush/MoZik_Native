package com.example.mozik;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Sign_In_Frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sign_In_Frag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Variables
    private TextView tvRegister;
    private EditText etEmail,etPassword;
    private Button btnSignIn;
    private FireBaseServices fbs = FireBaseServices.getinstance();
    public static final String SHARED_PREF = "sharedPrefs";

    public Sign_In_Frag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Sign_In_Frag.
     */
    // TODO: Rename and change types and number of parameters
    public static Sign_In_Frag newInstance(String param1, String param2) {
        Sign_In_Frag fragment = new Sign_In_Frag();
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
        CheckUser();
    }

    private void CheckUser() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF,Context.MODE_PRIVATE);
        String check = sharedPreferences.getString("name" , "");
        if (check.equals("true")){
            FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.framelayout, new HomePage_Frag());
            ft.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign__in_, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        connect();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               valid();

            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister(view);
            }
        });
    }

    private void valid() {
        String email,password;
        email=etEmail.getText().toString().trim();
        password=etPassword.getText().toString().trim();
        if(email.isEmpty()){
            Toast.makeText(getContext(), "email field is empty", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            Toast.makeText(getContext(), "password field is empty", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }
        fbs.getmAuth().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("name" , "true");
                    editor.apply();
                    FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.framelayout, new HomePage_Frag());
                    ft.commit();
                    Toast.makeText(getContext(), "Logged in successfully (Y)", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "log in failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void goToRegister(View view) {
        FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, new Register_Frag());
        ft.commit();
    }

    public void connect(){

        etEmail= getView().findViewById(R.id.etEmail);
        etPassword= getView().findViewById(R.id.etPassword);
        btnSignIn= getView().findViewById(R.id.btnSignIn);
       tvRegister = getView().findViewById(R.id.tvRegister);
    }
}
package com.example.tuitionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TuitionPostActivity extends AppCompatActivity {

    EditText subjects , salary , notes;
    Spinner spinner_preference , spinner_days;

    String prefered_gender , days_per_week;

    String[] mPreference = {"Any" , "Male" , "Female"};
    String[] mDays = {"1 day" , "2 days", "3 days", "4 days", "5 days", "6 days", "7 days"};
    
    Button post;
    
    private FirebaseAuth mAuth;

    String fname , lname , medium , address , region , clas;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuition_post);

        spinner_preference = findViewById(R.id.preference);
        spinner_days = findViewById(R.id.days);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,mPreference);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_preference.setAdapter(arrayAdapter);
        spinner_preference.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String pref_gender = adapterView.getItemAtPosition(i).toString();
                prefered_gender = pref_gender;
                Toast.makeText(TuitionPostActivity.this, prefered_gender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter arrayAdapter1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,mDays);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_days.setAdapter(arrayAdapter1);
        spinner_days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String dpw = adapterView.getItemAtPosition(i).toString();
                days_per_week = dpw;
                Toast.makeText(TuitionPostActivity.this, days_per_week, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        
        subjects = findViewById(R.id.subjects);
        salary = findViewById(R.id.salary);
        notes = findViewById(R.id.notes);
        
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myref = mFirebaseDatabase.getReference();

        getDataFromProfile();
        
        post = findViewById(R.id.button_post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptPost();
            }
        });
        
        

    }

    private void attemptPost() {
        
        String sub = subjects.getText().toString();
        String sal = salary.getText().toString();

        int note_len = notes.getText().toString().length();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(sub)){
            subjects.setError(getString(R.string.error_field_required));
            focusView = subjects;
            cancel =true;
        }
        if(TextUtils.isEmpty(sal)){
            salary.setError(getString(R.string.error_field_required));
            focusView = salary;
            cancel = true;
        }
        if(note_len > 100){
            notes.setError(getString(R.string.error_note_too_long));
            focusView = salary;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // TODO: Call create FirebaseUser() here
            createPostInFirebase();
            Toast.makeText(this, String.valueOf(note_len), Toast.LENGTH_SHORT).show();

        }
    }

    private void getDataFromProfile() {
        String userId = mAuth.getCurrentUser().getUid();

        myref.child("Users").child("Student").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fname = dataSnapshot.child("First Name").getValue().toString();
                lname = dataSnapshot.child("Last Name").getValue().toString();
                medium = dataSnapshot.child("Medium").getValue().toString();
                address = dataSnapshot.child("Address").getValue().toString();
                region = dataSnapshot.child("Region").getValue().toString();
                clas = dataSnapshot.child("Class").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createPostInFirebase() {


        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("TuitionPosts").child(userId);




        String desired_subjects = subjects.getText().toString();
        String desired_salary = salary.getText().toString();
        String desired_days = days_per_week;
        String desired_gender = prefered_gender;
        String desired_note = notes.getText().toString();

        HashMap<String,String> offerMap = new HashMap<>();
        offerMap.put("Subjects",desired_subjects);
        offerMap.put("Days",desired_days);
        offerMap.put("Preferred Gender",desired_gender);
        offerMap.put("Salary",desired_salary);
        offerMap.put("Notes",desired_note);

        offerMap.put("First Name",fname);
        offerMap.put("Last Name",lname);
        offerMap.put("Medium",medium);
        offerMap.put("Class",clas);
        offerMap.put("Region",region);
        offerMap.put("Address",address);


        current_user_db.setValue(offerMap);

        showSuccessDialog("Offer Posted");

    }

    private void showSuccessDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Congratulation")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(TuitionPostActivity.this, StudentLandingActivity.class);
                        finish();
                        startActivity(intent);

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}

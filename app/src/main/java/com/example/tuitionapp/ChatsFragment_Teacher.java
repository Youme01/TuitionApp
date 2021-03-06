package com.example.tuitionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment_Teacher extends Fragment {

    private RecyclerView recyclerView;
    private com.example.tuitionapp.UserAdapter userAdapter;
    private ArrayList<com.example.tuitionapp.UserContacts> muser;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    private List<String> userlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats_fragment__teacher, container, false);

        recyclerView = view.findViewById(R.id.Chats_Teacher_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userlist = new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("Chats");

        FloatingActionButton floatingActionButton1 = view.findViewById(R.id.Chat_teacher_button);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), Current_Student.class);
                startActivity(intent);

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Chat chat = dataSnapshot1.getValue(Chat.class);
                    if( chat.getSender().equals(firebaseUser.getUid())){
                        userlist.add(chat.getReceiver());
                    }
                    if( chat.getReceiver().equals(firebaseUser.getUid())){
                        userlist.add(chat.getSender());
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void readChats(){

        muser = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Student");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                muser.clear();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    com.example.tuitionapp.UserContacts userContacts = dataSnapshot1.getValue(com.example.tuitionapp.UserContacts.class);

                    //display user from chats
                    if(userContacts.getId() != null) {
                        for (String id : userlist) {
                            if (userContacts.getId().equals(id)) {
                                if (muser.size() != 0) {
                                    for (com.example.tuitionapp.UserContacts user : new ArrayList<com.example.tuitionapp.UserContacts>(muser)) {
                                        if (!userContacts.getId().equals(user.getId())) {
                                            muser.add(userContacts);
                                        }

                                    }
                                } else {
                                    muser.add(userContacts);
                                }
                            }
                        }
                    }
                }
                userAdapter = new com.example.tuitionapp.UserAdapter(getContext(),muser);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}


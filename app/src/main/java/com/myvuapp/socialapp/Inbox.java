package com.myvuapp.socialapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myvuapp.socialapp.Adapter.RecyclerAdapter;
import com.myvuapp.socialapp.Model.messages;
import com.myvuapp.socialapp.util.Constants;

import java.util.ArrayList;

import com.myvuapp.socialapp.R;;

public class Inbox extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    ArrayList<messages> arrayList;
    TextView tvsearchIc;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        arrayList = new ArrayList<messages>();
        getInbox();
    }

    private void getInbox() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("UserChats").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String inboxID = snap.child("InboxID").getValue(String.class);
                    Log.e("InboxID", inboxID);
                    getchats(inboxID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getchats(String inboxID) {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Chats").child(inboxID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages messages = snapshot.getValue(messages.class);
                arrayList.add(messages);
                Log.e("CheckMessage",messages.getSendername());
                recyclerView.setLayoutManager(new LinearLayoutManager(Inbox.this));
                adapter = new RecyclerAdapter(Inbox.this, arrayList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


//    private void setupList() {
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        arrayList = new ArrayList<messages>();
//
////        for (int i=0;i<10;i++){
////            messages msgs=new messages("","ABC Name","Hwllo world","Today");
////            arrayList.add(msgs);
////
////        }
//        adapter = new RecyclerAdapter(this, arrayList);
//        recyclerView.setAdapter(adapter);
//    }

}
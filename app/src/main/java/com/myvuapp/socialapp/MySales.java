package com.myvuapp.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myvuapp.socialapp.Adapter.SaleAdapter;
import com.myvuapp.socialapp.Model.checkout;
import com.myvuapp.socialapp.util.Constants;

import java.util.ArrayList;

import com.myvuapp.socialapp.R;;

public class MySales extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<checkout> arrayList;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sales);
        recyclerView=findViewById(R.id.recyclersales);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getcheckouts();

    }
    private void getcheckouts(){
        DatabaseReference reference= FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("CheckoutUser").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap: snapshot.getChildren()){
                    String checkoutID=snap.child("checkoutID").getValue(String.class);
                    getcheckoutdetails(checkoutID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getcheckoutdetails(String checkoutID){
        DatabaseReference reference= FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Checkouts").child(checkoutID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList=new ArrayList<checkout>();
                checkout checkout=snapshot.getValue(checkout.class);
                arrayList.add(checkout);
                recyclerView.setLayoutManager(new LinearLayoutManager(MySales.this));
                SaleAdapter saleAdapter = new SaleAdapter(MySales.this, arrayList);
                recyclerView.setAdapter(saleAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
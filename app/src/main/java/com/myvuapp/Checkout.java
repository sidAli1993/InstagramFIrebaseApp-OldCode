package com.myvuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myvuapp.socialapp.Model.checkout;
import com.myvuapp.socialapp.Model.ngodetailmodel;
import com.myvuapp.socialapp.Model.ngomodel;
import com.myvuapp.socialapp.R;
import com.myvuapp.socialapp.util.Constants;
import com.myvuapp.socialapp.util.MySingleton;
import com.myvuapp.socialapp.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Checkout extends AppCompatActivity {

    FirebaseUser firebaseUser;
    String postid, bookname, authorname, price, pubid;
    EditText edtaddress, edtcity, edtphone;
    TextView tvbookname, tvauthorname, tvprice, tvsaletype, tvsellercity;
    LinearLayout linearngo;
    List<ngomodel> mList;
    List<String> mListname;
    Spinner spinnerngo;
    String selectedngo;
    String sname = "";

    TextView tvpname, tvaddr, tvaccno, tvbankdetails, tvphone;
    Button btncheckout;
    double lat, lng;
    String type = "";
    Utils utils;
    String ngoID;
    String buyerName = "";
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAyC-IgKQ:APA91bHyFE5cPHXGdBp3NS81_s1cF5YDOcEVxRxTFw8L6-8csU7NRGbM9maZR8nbGgWiEDtyq9EexhNAvmJH-fg7-B7pjTMjIFw5vdyzvzwJN1ENGFUyTL0Yc_7bey_KbdH1oiEyWaLg\t\n";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        init();
    }

    private void init() {
        edtaddress = findViewById(R.id.edtaddress);
        edtcity = findViewById(R.id.edtcity);
        edtphone = findViewById(R.id.edtphone);
        tvbookname = findViewById(R.id.tvbookname);
        tvauthorname = findViewById(R.id.tvauthorname);
        tvprice = findViewById(R.id.tvprice);
        btncheckout = findViewById(R.id.btncheckout);
        tvsaletype = findViewById(R.id.tvsaletype);
        spinnerngo = findViewById(R.id.spinnerngo);
        tvpname = findViewById(R.id.tvrelevantpersonname);
        tvaddr = findViewById(R.id.tvngoaddress);
        tvaccno = findViewById(R.id.tvaccountno);
        tvbankdetails = findViewById(R.id.tvbankname);
        tvphone = findViewById(R.id.tvngophone);
        linearngo = findViewById(R.id.linearngo);
        tvsellercity = findViewById(R.id.tvsellercity);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        utils = new Utils(this);
        getpreviousdata();
    }

    private void getpreviousdata() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            postid = extras.getString("postid");
            bookname = extras.getString("bookname");
            authorname = extras.getString("authorname");
            price = extras.getString("price");
            type = extras.getString("type");
            pubid = extras.getString("postpubid");
            tvbookname.setText("Book: " + bookname);
            tvprice.setText("Price: " + price + " /RS");
            tvauthorname.setText("Author: " + authorname);
            tvsaletype.setText("Sale type: " + type);

            edtphone.setVisibility(View.VISIBLE);
            edtaddress.setVisibility(View.VISIBLE);
            edtcity.setVisibility(View.VISIBLE);
            linearngo.setVisibility(View.GONE);
            btncheckout.setText("Checkout");
            getpubdata();
            getUserData();
            clicks();

        }
    }

    private void getUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String address = snapshot.child("address").getValue(String.class);
                String city = snapshot.child("city").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                buyerName = snapshot.child("userName").getValue(String.class);
                //lat=Double.parseDouble(snapshot.child("lat").getValue(String.class));
                // lng=Double.parseDouble(snapshot.child("lng").getValue(String.class));

                edtaddress.setText(address);
                edtcity.setText(city);
                edtphone.setText(phone);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getpubdata() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(pubid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String city = snapshot.child("city").getValue(String.class);
                sname = snapshot.child("userName").getValue(String.class);
                tvsellercity.setText("Seller City: " + city);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clicks() {
        btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference refrence = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Checkouts");
                String checkoutid = refrence.push().getKey();
                checkout checkout = new checkout(checkoutid, bookname, authorname, price, postid, type, edtaddress.getText().toString(), edtcity.getText().toString(), edtphone.getText().toString(), firebaseUser.getUid(), utils.getdate(), utils.gettime(), buyerName);
                refrence.child(checkoutid).setValue(checkout);

                DatabaseReference refrence2 = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("CheckoutUser").child(firebaseUser.getUid());
                refrence2.push().child("checkoutID").setValue(checkoutid);

                DatabaseReference refrence3 = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("CheckoutUser").child(pubid);
                refrence3.push().child("checkoutID").setValue(checkoutid);
                utils.showSuccessToast("Congrats...! success");

                DatabaseReference reference4 = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(pubid);
                reference4.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String deviceID = snapshot.child("deviceID").getValue(String.class);
                        Log.e("DeviceID", deviceID);
                        Log.e("pubID", pubid);
                        Log.e("sendID", firebaseUser.getUid());
                        send("Congratulations", "you have one new Order, Please check your order section.", deviceID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }


    private void getngospinner() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("NGO");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 1;
                mList = new ArrayList<ngomodel>();
                mListname = new ArrayList<String>();
                mListname.add("Select NGO");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = snapshot.child(String.valueOf(i)).getValue(String.class);
                    Log.e("MYName", name);
                    ngomodel ngomodel = new ngomodel(String.valueOf(i), name);
                    mList.add(ngomodel);
                    Log.e("FirstList", mList.get(0).getNgoname());
                    i++;
                }
                for (int j = 0; j < mList.size(); j++) {
                    mListname.add(mList.get(j).getNgoname());
                    Log.e("AddedList", mListname.get(j));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Checkout.this, R.layout.support_simple_spinner_dropdown_item, mListname);
                spinnerngo.setAdapter(arrayAdapter);
                spinnerngo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedngo = parent.getItemAtPosition(position).toString();
                        Log.e("selectedNGO", selectedngo);
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getNgoname().equals(selectedngo)) {
                                Log.e("NGOID", mList.get(i).getNgoid());
                                getNGODetails(mList.get(i).getNgoid());
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNGODetails(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("NGODetails");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
//                Log.e("NGODETAILS",snapshot.child("phone").getValue(String.class));
                for (DataSnapshot snapshot : snap.getChildren()) {
                    String ngID = snapshot.child("ngoID").getValue(String.class);
                    if (ngID.equals(id)) {
                        ngodetailmodel ngodetailmodel = snapshot.getValue(ngodetailmodel.class);
                        tvpname.setText(ngodetailmodel.getName());
                        tvaccno.setText("" + Long.parseLong(ngodetailmodel.getAccountNo()));
                        tvaddr.setText(ngodetailmodel.getAddress());
                        tvbankdetails.setText(ngodetailmodel.getBankName());
                        tvphone.setText("" + Long.parseLong(ngodetailmodel.getPhone()));
                        ngoID = id;
                        Log.e("NgoId", ngoID);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void send(String title, String message, String sendto) {
        TOPIC = sendto; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = title;
        NOTIFICATION_MESSAGE = message;

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("body", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("notification", notifcationBody);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: " + response.toString());


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Checkout.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
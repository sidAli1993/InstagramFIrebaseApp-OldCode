package com.myvuapp.socialapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import com.myvuapp.socialapp.Adapter.MessageListAdapter;
import com.myvuapp.socialapp.Model.Message;
import com.myvuapp.socialapp.util.Constants;
import com.myvuapp.socialapp.util.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.myvuapp.socialapp.R;;

public class Chat extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Message> arrayList;
    MessageListAdapter messageListAdapter;
    EditText edtmsg;
    ImageButton imvSend;
    int count = 1;
    String  name, inboxid;
    String currentDate;
    FirebaseUser firebaseUser;
    TextView tvname;
    String profilepicsender,profilepicreciever;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAyC-IgKQ:APA91bHyFE5cPHXGdBp3NS81_s1cF5YDOcEVxRxTFw8L6-8csU7NRGbM9maZR8nbGgWiEDtyq9EexhNAvmJH-fg7-B7pjTMjIFw5vdyzvzwJN1ENGFUyTL0Yc_7bey_KbdH1oiEyWaLg\t\n";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    String deviceID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        getPrevData();

    }


    private void getPrevData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            inboxid = extras.getString("inboxid");
            name = extras.getString("name");
            tvname.setText(name);
            getchats(inboxid);
            getrecieverID(inboxid);
        }
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<Message>();
        imvSend = findViewById(R.id.imv_msg_send);
        edtmsg = findViewById(R.id.edt_chat_msg);
        tvname=findViewById(R.id.tvname);
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        messageListAdapter = new MessageListAdapter(this, arrayList);
        recyclerView.setAdapter(messageListAdapter);

        clicks();
    }

    private void getchats(String inboxID) {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("ChatMessages").child(inboxID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                //messages messages = snapshot.getValue(messages.class);
                arrayList.clear();
                for (DataSnapshot snapshot:snap.getChildren()){
                    Message message=new Message();
                    String senderID=snapshot.child("senderID").getValue(String.class);
                    Log.e("SenderID",senderID);
                    Log.e("UserID",firebaseUser.getUid());
                    if (senderID.equals(firebaseUser.getUid())){
                        message.setImg("");
                        message.setMessage(snapshot.child("message").getValue(String.class));
                        message.setMsgTime(snapshot.child("msgTime").getValue(String.class));
                        message.setSenderID(snapshot.child("senderID").getValue(String.class));
                        message.setIscurrentUser(true);
                    }else {

                        message.setImg("");
                        message.setMessage(snapshot.child("message").getValue(String.class));
                        message.setMsgTime(snapshot.child("msgTime").getValue(String.class));
                        message.setSenderID(snapshot.child("senderID").getValue(String.class));
                        message.setIscurrentUser(false);
                    }
                    arrayList.add(message);
                }
//                recyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));

                messageListAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getBottom());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    private String getrecieverprofile(String UserID){
//
//        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(UserID);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snap) {
//                //messages messages = snapshot.getValue(messages.class);
//                profilepicreciever=snap.child("profileImage").getValue(String.class);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        return profilepicreciever;
//    }

    private void clicks() {
        imvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message(edtmsg.getText().toString(), "", firebaseUser.getUid(), currentDate, true);
                arrayList.add(message);
                DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("ChatMessages").child(inboxid);
                String messageID = reference.push().getKey();
                reference.child(messageID).setValue(message);
                messageListAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getBottom());
                count++;
                edtmsg.setText("");
                sendNotification(deviceID,name);
            }
        });
    }
    private void getrecieverID(String inboxID){
        DatabaseReference refrence=FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Chats").child(inboxID);
        refrence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String recieverID=snapshot.child("recieverID").getValue(String.class);
                getUserToken(recieverID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getUserToken(String userID){
        DatabaseReference reference=FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(userID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deviceID=snapshot.child("deviceID").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendNotification(String deviceeid,String username){
        send(username,"you have new message,please check your inbox.",deviceeid);

    }

    private void send(String title,String message,String sendto){
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
            notification.put("data",notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
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
                        Toast.makeText(Chat.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onErrorResponse: Didn't work");
                    }
                }){
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
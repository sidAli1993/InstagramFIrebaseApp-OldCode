package com.myvuapp.socialapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myvuapp.socialapp.Model.reportpostmodel;
import com.myvuapp.socialapp.util.Constants;
import com.myvuapp.socialapp.util.Utils;

import java.util.HashMap;

import com.myvuapp.socialapp.R;;

public class OptionsActivity extends AppCompatActivity {

    public TextView logout, report, rating, tvsales;
    Utils utils;
    String userid, profileid;
    Toolbar toolbar;
    ImageView imvreport, imvrating, imvsales;
    String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


        init();

    }

    private void init() {
        logout = findViewById(R.id.logout);
        report = findViewById(R.id.report);
        rating = findViewById(R.id.rateuser);
        tvsales = findViewById(R.id.tvslaes);
        utils = new Utils(this);
        toolbar = findViewById(R.id.toolbar);
        imvreport = findViewById(R.id.imvreport);
        imvrating = findViewById(R.id.imvrating);
        imvsales = findViewById(R.id.imvsales);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getPrevData();

        clicks();
    }

    private void getPrevData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            profileid = extras.getString("profileid");
            userid = extras.getString("userid");
            userName=getUserNameByID(userid);
            Log.e("ProfileID", profileid);
            Log.e("userID", userid);
            if (profileid.equals(userid)) {
                report.setVisibility(View.GONE);
                rating.setVisibility(View.GONE);
                imvrating.setVisibility(View.GONE);
                imvreport.setVisibility(View.GONE);
                imvsales.setVisibility(View.VISIBLE);
                tvsales.setVisibility(View.VISIBLE);
            } else {
                report.setVisibility(View.GONE);
                rating.setVisibility(View.GONE);
                imvrating.setVisibility(View.GONE);
                imvreport.setVisibility(View.GONE);
                imvsales.setVisibility(View.GONE);
                tvsales.setVisibility(View.GONE);
            }
        }
    }

    private void clicks() {


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                getIntent();
                startActivity(new Intent(OptionsActivity.this,
                        MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setreport(profileid, userid);

            }
        });
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setrating(profileid, userid);
            }
        });
        tvsales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, MySales.class));
            }
        });
    }

    private void setreport(String profileID, String userID) {
        final EditText taskEditText = new EditText(OptionsActivity.this);
        taskEditText.setHint("Report Details...");
        AlertDialog dialog = new AlertDialog.Builder(OptionsActivity.this)
                .setTitle("Report")
                .setMessage("What do u find wrong in this User..")
                .setView(taskEditText)
                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("ReportUser").child(profileID);
                        reportpostmodel reportpostmodel = new reportpostmodel(task, utils.getdate(), utils.gettime(), profileID, userID);
                        reference.child(userID).setValue(reportpostmodel);
                        utils.showSuccessToast("User Reported");

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void setrating(String profileID, String userid) {


        Dialog rankDialog = new Dialog(OptionsActivity.this, R.style.CustomRatingBar);
        rankDialog.setContentView(R.layout.dialog);
        rankDialog.setCancelable(true);
        RatingBar ratingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("UserRating").child(profileID).child(userid);
                reference.child("rating").setValue(rating);

                DatabaseReference reference1 = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("UserRating").child(profileID);

                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int totalpostedrating = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            totalpostedrating += dataSnapshot.child("rating").getValue(Integer.class);
                        }
                        Log.e("totalposted", "" + totalpostedrating);

                        long count = snapshot.getChildrenCount();
                        Log.e("total reviews count", "" + count);
                        float a = count * 5;
                        float b = totalpostedrating / a;
                        Log.e("AAAAA", "" + a);
                        Log.e("BBBB", "" + b);
                        float average = b * 5;
                        Log.e("Average", "" + average);
                        FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(profileID).child("AvgRating").setValue(average);
                        Toast.makeText(OptionsActivity.this, "rating updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        //now that the dialog is set up, it's time to show it
        rankDialog.show();
    }

    private void setfeedback(String UserID) {
        final EditText taskEditText = new EditText(this);
        taskEditText.setHint("Set Feedback about app...");
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Feedback")
                .setMessage("Please enter your feedback")
                .setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String feedback = String.valueOf(taskEditText.getText());

                        Toast.makeText(OptionsActivity.this, ""+userName, Toast.LENGTH_SHORT).show();
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();

                        hashMap.put("feedback", feedback);
                        hashMap.put("userName",userName);

                        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Feedback");
                        String pushKey = reference.push().getKey();
                        reference.child(pushKey).setValue(hashMap);


                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private String getUserNameByID(String uid) {

        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(uid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return userName;
    }

}
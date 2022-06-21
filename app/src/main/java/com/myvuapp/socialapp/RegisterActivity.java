package com.myvuapp.socialapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myvuapp.socialapp.Model.User;
import com.myvuapp.socialapp.util.Constants;
import com.myvuapp.socialapp.util.PrefManager;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import com.myvuapp.socialapp.R;;

public class RegisterActivity extends AppCompatActivity {

    private EditText username_register, password_register, email_register,edtaddr,edtcity,edtphone;
    private Button register;
    private TextView login_text;
    private ImageView back;

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog pd;
    private FirebaseAuth.AuthStateListener authStateListener;
    double lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // init fields
        username_register = findViewById(R.id.username_register);
        email_register = findViewById(R.id.email_register);
        password_register = findViewById(R.id.password_register);
        register = findViewById(R.id.login_button);
        login_text = findViewById(R.id.login_text);
        edtaddr=findViewById(R.id.edtaddress);
        edtcity=findViewById(R.id.edtcity);
        edtphone=findViewById(R.id.edtphone);
        auth = FirebaseAuth.getInstance();




        getPrevData();
        // when user click the login text, just direct them to login page
        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        edtaddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LocationPickerActivity.class);
                startActivityForResult(i, 13);
            }
        });


        back = findViewById(R.id.register_back_to_main);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getPrevData(){
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            String uid=extras.getString("uid");
            String name=extras.getString("name");
            String email=extras.getString("email");
            username_register.setEnabled(false);
            email_register.setEnabled(false);
            password_register.setEnabled(false);
            username_register.setVisibility(View.GONE);
            email_register.setVisibility(View.GONE);
            password_register.setVisibility(View.GONE);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrefManager prefManager=new PrefManager(RegisterActivity.this);

                        User user=new User(uid,name,edtaddr.getText().toString(),edtcity.getText().toString(),edtphone.getText().toString(),""+lat,""+lng,prefManager.getTagtoken());
                        reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(uid);

                        Log.e("enter: ", "into firebase database user create");
                        reference.setValue(user);
                        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);


                }
            });
        }else {
            // when user click the register button
            username_register.setEnabled(true);
            email_register.setEnabled(true);
            password_register.setEnabled(true);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // show the waiting message
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setMessage("Please wait...");
                    pd.show();

                    // get the info from user typing
                    String str_username = username_register.getText().toString().trim();
                    String str_email = email_register.getText().toString().trim();
                    String str_password = password_register.getText().toString().trim();


                    // if some info not correct
                    if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                        Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                        if(pd.isShowing()){
                            pd.dismiss();
                        }
                    }
                    else if (str_password.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "Password must have at least 6 characters!", Toast.LENGTH_LONG).show();
                        if(pd.isShowing()){
                            pd.dismiss();
                        }
                    }else if (TextUtils.isEmpty(edtaddr.getText().toString())){
                        edtaddr.setError("Address is empty");
                    }else if (TextUtils.isEmpty(edtphone.getText().toString())){
                        edtphone.setError("Phone is empty");
                    }else if (lat==0){
                        Toast.makeText(RegisterActivity.this, "Lat is zero", Toast.LENGTH_SHORT).show();
                    }else if (lng==0){
                        Toast.makeText(RegisterActivity.this, "Longitude is zero", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // all info correct, just go to register
                        Log.e("register user: ", "enter into register method");
                        register( str_email, str_password);
                    }
                }
            });
        }
    }
    // register process
    private void register( String email, final String password) {
        // create user for firebase auth using email and password
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("create user: ", "task success");
                           sendVerificationEmail();

                        }
                        else {
                            // register failed
                            pd.dismiss();
                            Log.e("failed: ", "onComplete: Failed=" + task.getException().getMessage());
                            Toast.makeText(RegisterActivity.this, "Please try another email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 13) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    String address = data.getStringExtra(MapUtility.ADDRESS);

                    lat = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    lng = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    edtaddr.setText(address);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            // create the user for firebase database (different from firebase auth)
                            reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(userid);
                            Log.d("Register", "User id for new registered user is " + userid);


                            // set the fields for current user, and redirect to start page (code below could be replaced by the commented part)
//                            User newUser = new User(userid, username);
                            PrefManager prefManager=new PrefManager(RegisterActivity.this);

                            User user=new User(userid,username_register.getText().toString(),edtaddr.getText().toString(),edtcity.getText().toString(),edtphone.getText().toString(),""+lat,""+lng,prefManager.getTagtoken());

                            Log.e("enter: ", "into firebase database user create");
                            reference.setValue(user);
                            pd.dismiss();
                           // Intent intent = new Intent(RegisterActivity.this, ProfileInitActivity.class);
                           // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                           // startActivity(intent);

                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }
}
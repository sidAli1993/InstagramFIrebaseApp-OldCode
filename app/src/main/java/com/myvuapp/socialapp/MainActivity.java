package com.myvuapp.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myvuapp.socialapp.Adapter.SplashAdapter;
import com.myvuapp.socialapp.ScrollActivity.ScrollLayoutManager;

import com.myvuapp.socialapp.R;;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button login;
    private Button register;
    public FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // check if user is null - to keep this device's user keep logged in
        if (firebaseUser != null) {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }

        // set background
        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setAdapter(new SplashAdapter(MainActivity.this));
        mRecyclerView.setLayoutManager(new ScrollLayoutManager(MainActivity.this));
        mRecyclerView.smoothScrollToPosition(Integer.MAX_VALUE / 2);
        login = findViewById(R.id.slideLogin);
        register = findViewById(R.id.slideRegister);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });

    }

    public void register(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
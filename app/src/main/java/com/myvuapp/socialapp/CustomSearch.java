package com.myvuapp.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myvuapp.socialapp.Adapter.PostAdapter;
import com.myvuapp.socialapp.Model.Post;
import com.myvuapp.socialapp.util.Constants;

import java.util.ArrayList;
import java.util.List;

import com.myvuapp.socialapp.R;;

public class CustomSearch extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    private List<String> followingList;
    public String signOnUserID;
    EditText edtsearch;
    public ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_search);
        recyclerView = findViewById(R.id.recycle_view);
        edtsearch=findViewById(R.id.edtsearch);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(this, postLists);
        postAdapter.setHasStableIds(true);
        recyclerView.setAdapter(postAdapter);

//        final SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                checkFollowing();
//                postAdapter.notifyDataSetChanged();
//                refreshLayout.setRefreshing(false);
//            }
//        });

        progressBar = findViewById(R.id.progress_circular);
        searchclick();
        checkFollowing();

    }
    private void searchclick(){
        edtsearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    postAdapter.getFilter().filter(edtsearch.getText().toString());

                    return true;
                }
                return false;
            }
        });
    }

    // check current user's following, load followings and self posts
    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    followingList.add(datasnapshot.getKey());
                }
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // read posts from db
    private  void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postLists.clear();
                signOnUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot datasnapshot: snapshot.getChildren()){
                    Post post = datasnapshot.getValue(Post.class);
                    // check is curUser
                    Log.e("Postis",post.getPostTitle());
                    if(signOnUserID.equals(post.getPostPublisher())) {
                        postLists.add(post);

                    } else {
                        for(String id: followingList){
                            if(post.getPostPublisher().equals(id)){
                                postLists.add(post);
                            }

                        }
                    }
                }
                postAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
package com.myvuapp.socialapp.ui.postDetail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class PostDetailFragment extends Fragment {

    String postID;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    // show the post detail after clicking

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        // get the post id from the parameter passing in

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postID = preferences.getString("postID", "none");

        Log.e("post id is", postID);

        // init the view

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        // read the post
        readPost();

        return view;
    }

    private void readPost() {
        // get the actual post from database
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // add the data to the layout
                postList.clear();
                Post post = snapshot.getValue(Post.class);
                postList.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
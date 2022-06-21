package com.myvuapp.socialapp.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myvuapp.socialapp.Adapter.MyPhotoAdapter;
import com.myvuapp.socialapp.Adapter.MySaveAdapter;
import com.myvuapp.socialapp.Chat;
import com.myvuapp.socialapp.EditProfileActivity;
import com.myvuapp.socialapp.FollowersActivity;
import com.myvuapp.socialapp.Model.Post;
import com.myvuapp.socialapp.Model.User;
import com.myvuapp.socialapp.Model.UserTwo;
import com.myvuapp.socialapp.Model.messages;
import com.myvuapp.socialapp.OptionsActivity;
import com.myvuapp.socialapp.util.Constants;
import com.myvuapp.socialapp.util.MySingleton;
import com.myvuapp.socialapp.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myvuapp.socialapp.R;;


public class ProfileFragment extends Fragment {

    // init the top part
    ImageView image_profile, options;
    TextView posts, followers, following, username, display_liked_count;
    Button edit_profile, btnsendmessage;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAyC-IgKQ:APA91bHyFE5cPHXGdBp3NS81_s1cF5YDOcEVxRxTFw8L6-8csU7NRGbM9maZR8nbGgWiEDtyq9EexhNAvmJH-fg7-B7pjTMjIFw5vdyzvzwJN1ENGFUyTL0Yc_7bey_KbdH1oiEyWaLg\t\n";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    String deviceID;

    // init the posts
    RecyclerView recyclerView;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> postList;

    // things related to saving
    private List<String> mySaves;
    RecyclerView recyclerView_saves;
    MySaveAdapter mySaveAdapter_saves;
    List<Post> postList_saves;

    FirebaseUser firebaseUser;
    String profileid;
    String profileurl = "";
    ImageButton my_photos, saved_photos;
    Utils utils;
    User user;
    UserTwo userTwo;
    RatingBar ratingBar;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // set view and init all variables

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // get parsed in userid, e.g. search user and jump to profile
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        username = view.findViewById(R.id.username);
        edit_profile = view.findViewById(R.id.edit_profile);
        btnsendmessage = view.findViewById(R.id.send_message);
        ratingBar=view.findViewById(R.id.ratingBar);
        display_liked_count = view.findViewById(R.id.display_liked_count);

        my_photos = view.findViewById(R.id.my_photos);
        saved_photos = view.findViewById(R.id.saved_photos);

        recyclerView = view.findViewById(R.id.recycler_view);
        utils=new Utils(getActivity());

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myPhotoAdapter);


        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        // StaggeredGrid will make the layout messy - So finally use GridLayout
        // StaggeredGridLayoutManager linearLayoutManager_saves = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 2);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        mySaveAdapter_saves = new MySaveAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(mySaveAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);


        // get user info and display
        userInfo();
        userInfoTwo();
        getFollowers();
        getNrPosts();
        getMyPhotos();
        getMySaves();
        getMyLikesCount();

        // if the user see own profile, then display edit button
        if (profileid.equals(firebaseUser.getUid())) {
            edit_profile.setText("Edit Profile");
            btnsendmessage.setVisibility(View.GONE);
            Log.e("FirebaseUserq", "" + firebaseUser.getUid());
            Log.e("Profileidq", "" + profileid);
        } else {
            Log.e("FirebaseUser", "" + firebaseUser.getUid());
            Log.e("Profileid", "" + profileid);
            checkFollow();
            saved_photos.setVisibility(View.GONE);
            btnsendmessage.setVisibility(View.VISIBLE);
        }

        // if the user see others' profile, then display follow / unfollow button
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")) {
                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(intent);

                } else if (btn.equals("follow")) {
                    FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);

                    getUserToken(profileid);
                    addNotifications();
                } else if (btn.equals("following")) {
                    FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        // logout & setting
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                intent.putExtra("profileid",profileid);
                intent.putExtra("userid",firebaseUser.getUid());
                startActivity(intent);
            }
        });

        my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
                saved_photos.setImageResource(R.drawable.ic_save);
                my_photos.setImageResource(R.drawable.ic_photos_green);
            }
        });
        saved_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
                saved_photos.setImageResource(R.drawable.ic_saved);
                my_photos.setImageResource(R.drawable.ic_grid);
            }
        });

        // click on followers/following - see details
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });
        btnsendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Chats");
                String inboxID = reference.push().getKey();
                messages messages = new messages(userTwo.getProfileImage(),userTwo.getUserName(),firebaseUser.getUid(),"",utils.gettime(),inboxID,profileid,username.getText().toString(),profileurl);
                reference.child(inboxID).setValue(messages);

                DatabaseReference refrence2 = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("UserChats").child(firebaseUser.getUid());
                refrence2.push().child("InboxID").setValue(inboxID);

                DatabaseReference refrence3 = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("UserChats").child(profileid);
                refrence3.push().child("InboxID").setValue(inboxID);

                Intent intent = new Intent(getActivity(), Chat.class);
                intent.putExtra("inboxid", inboxID);
                startActivity(intent);
            }
        });
        return view;
    }

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", firebaseUser.getUid());
        hashMap.put("comment_text", "started following you");
        hashMap.put("postID", "");
        hashMap.put("isPost", false);

        reference.push().setValue(hashMap);


    }


    // get user and display username & profile image
    private void userInfo() {
        Log.e("user profile id is ", profileid);

        // get user from database using profile id
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }

                user = snapshot.getValue(User.class);

                // display the username and user profile image
                if (user.getProfileImage() == null) {
                    profileurl = "";
                } else {
                    Glide.with(getContext()).load(Uri.parse(user.getProfileImage())).into(image_profile);
                    profileurl = user.getProfileImage();
                }
                username.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfoTwo() {
        Log.e("user profile id is ", profileid);

        // get user from database using profile id
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                userTwo = snapshot.getValue(UserTwo.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Follow")
                .child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()) {
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // display followers & followings number
    private void getFollowers() {
        // get the followers from db, set text
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Follow")
                .child(profileid).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // get the followings from db, set text
        DatabaseReference referencel = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Follow")
                .child(profileid).child("Following");
        referencel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMyLikesCount() {
        // get the likes count from db, set text
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users")
                .child(profileid).child("userBeingLiked");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                display_liked_count.setText("" + snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get posts number from db and display
    private void getNrPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Post post = child.getValue(Post.class);
                    if (post.getPostPublisher().equals(profileid)) {
                        i++;
                    }
                }

                posts.setText("" + i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get photos from db and display
    private void getMyPhotos() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPostPublisher().equals(profileid)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // get the saves and display
    private void getMySaves() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mySaves.add(dataSnapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList_saves.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    for (String id : mySaves) {
                        if (post.getPostID().equals(id)) {
                            postList_saves.add(post);
                        }
                    }
                }
                mySaveAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    @Override
//    public void onResume(){
//        super.onResume();
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//                if (keyCode==KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
//                    return true;
//                }return false;
//            }});
//    }

    public void onKeyDownChild(int keyCode, KeyEvent event) {

    }
    private void getUserToken(String userID){
        DatabaseReference reference=FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(userID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deviceID=snapshot.child("deviceID").getValue(String.class);
                sendNotification(deviceID,"Follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendNotification(String deviceeid,String username){
        send(username,"you have new follower.",deviceeid);

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
                        Toast.makeText(getActivity(), "Request error", Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }
}

package com.myvuapp.socialapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myvuapp.Checkout;
import com.myvuapp.socialapp.CommentActivity;
import com.myvuapp.socialapp.FollowersActivity;
import com.myvuapp.socialapp.Model.Post;
import com.myvuapp.socialapp.Model.User;
import com.myvuapp.socialapp.Model.reportpostmodel;
import com.myvuapp.socialapp.ui.postDetail.PostDetailFragment;
import com.myvuapp.socialapp.ui.profile.ProfileFragment;
import com.myvuapp.socialapp.util.Constants;
import com.myvuapp.socialapp.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.myvuapp.socialapp.R;;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements Filterable {

    public Context mContext;
    public List<Post> mPost;
    public List<Post> mPostFilter;

    private FirebaseUser firebaseUser;
    private Utils utils;
    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
        this.mPostFilter=mPost;

    }

    @Override
    public long getItemId(int position) {
        return UUID.nameUUIDFromBytes(mPostFilter.get(position).getPostID().getBytes()).getMostSignificantBits();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        utils=new Utils(mContext);
        // get current post
        final Post post = mPostFilter.get(position);

        if (mContext != null) {
            Glide.with(mContext.getApplicationContext()).load(post.getPostImage()).into(holder.postImage);
        }

        // for pre-load pics but the pic will be the same size as the preload one - so remove this part
//        Glide.with(mContext).load(post.getPostImage())
//                .apply(new RequestOptions().placeholder(R.drawable.ic_tempura))
//                .into(holder.postImage);

        if (post.getPostContent().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getPostContent());
        }
        holder.bookName.setText(post.getPostTitle());
        holder.time.setText(post.getPostTime());
        if (post.getSaleType().equals("Rent")) {
            holder.imvtype.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_rent));
        } else if (post.getSaleType().equals("Sale")) {
            holder.imvtype.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sale_tag));
        }
        holder.tvprice.setText(post.getPrice() + "/Rs");
        holder.tvshelve.setText("Shelve: " + post.getShelve());
        holder.tvauthorname.setText("Written By: " + post.getAuthorName());
        // display publisher information
        publisherInfo(holder.imageProfile, holder.username, post.getPostPublisher(), position);

        // check like status
        isLiked(post.getPostID(), holder.like);
        postLikesDisplay(holder.likes, post.getPostID());

        // get comments
        getComments(post.getPostID(), holder.comments);

        // click save button
        isSaved(post.getPostID(), holder.save);

        // if user clicks the profile image of a post, then direct to the publisher's profile
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPostPublisher());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new ProfileFragment()).addToBackStack(null).commit();
            }
        });
        holder.imvtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Checkout.class);
                intent.putExtra("postid", post.getPostID());
                intent.putExtra("bookname", post.getPostTitle());
                intent.putExtra("authorname", post.getAuthorName());
                intent.putExtra("price", post.getPrice());
                intent.putExtra("type", post.getSaleType());
                intent.putExtra("postpubid", post.getPostPublisher());
                mContext.startActivity(intent);
            }
        });

        // if user clicks the publisher useername of a post, then direct to the publisher's profile
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPostPublisher());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new ProfileFragment()).addToBackStack(null).commit();
            }
        });

        // if user clicks the image of a post, then direct to the detail of that post
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("", "you clicked the post image in post adapter!");
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postID", post.getPostID());
                boolean successPut = editor.commit();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new PostDetailFragment()).addToBackStack(null).commit();
            }
        });

        // click save button
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostID()).setValue(true);
                    updatePostBeingSaved(post);
                } else {
                    FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostID()).removeValue();
                    updatePostBeingSavedCancelled(post);
                }
            }
        });

        // click like button
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("likeOnClick", "clicked");
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Likes").child(post.getPostID())
                            .child(firebaseUser.getUid()).setValue(true);
                    updateUserBeingLiked(post);
                    addNotifications(post.getPostPublisher(), post.getPostID());
                } else {
                    FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Likes").child(post.getPostID())
                            .child(firebaseUser.getUid()).removeValue();
                    updateUserBeingLikedCancelled(post);
                }
            }
        });

        // click likes text - see who likes this post
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPostID());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });

        // click comment button
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postID", post.getPostID());
                intent.putExtra("publisherID", post.getPostPublisher());
                mContext.startActivity(intent);
            }
        });

        // click comments text - see more comments or send comments
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postID", post.getPostID());
                intent.putExtra("publisherID", post.getPostPublisher());
                mContext.startActivity(intent);
            }
        });

        // if the user click the "more" (edit or post)
        if (!post.getPostPublisher().equals(firebaseUser.getUid())) {
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // if click edit, direct to edit method
                            if (item.getItemId() == R.id.report) {
                                setreport(post.getPostID(),firebaseUser.getUid());
                                return true;
                                // not click anything
                            } else if (item.getItemId() == R.id.postrating) {
                                setrating(post.getPostID(),firebaseUser.getUid());
                                return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.inflate(R.menu.post_menu_two);
                    popupMenu.show();
                }
            });
        } else {
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // if click edit, direct to edit method
                            if (item.getItemId() == R.id.edit) {
                                editPost(post.getPostID());
                                return true;
                                // not click anything
                            }
                            return false;
                        }
                    });
                    popupMenu.inflate(R.menu.post_menu);
                    // if the user is not the publisher of the post, then hide the edit menu
                    if (!post.getPostPublisher().equals(firebaseUser.getUid())) {
                        popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    }
                    popupMenu.show();
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mPostFilter.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    filterResults.count = mPost.size();
                    filterResults.values = mPost;

                }else{
                    List<Post> resultsModel = new ArrayList<>();
//                    String searchStr = constraint.toString().toLowerCase();
                    String searchStr = constraint.toString();

                    for(Post itemsModel:mPost){
                        if(itemsModel.getPostTitle().contains(searchStr) || itemsModel.getAuthorName().contains(searchStr)
                                || itemsModel.getShelve().contains(searchStr)
                                || itemsModel.getPostCity().contains(searchStr)
                                || itemsModel.getPrice().contains(searchStr)){
                            resultsModel.add(itemsModel);

                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }


                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mPostFilter = (List<Post>) results.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageProfile, postImage, like, comment, save, more, imvtype;
        public TextView username, bookName, likes, description, comments, time, tvauthorname, tvshelve, tvprice;
        public RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            username = itemView.findViewById(R.id.username);
            imvtype = itemView.findViewById(R.id.type);

            postImage = itemView.findViewById(R.id.postImage);
            bookName = itemView.findViewById(R.id.bookName);
            tvauthorname = itemView.findViewById(R.id.authorname);
            tvshelve = itemView.findViewById(R.id.shelve);
            tvprice = itemView.findViewById(R.id.bookprice);

            ratingBar = itemView.findViewById(R.id.ratingBar1);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);

            likes = itemView.findViewById(R.id.likes);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            time = itemView.findViewById(R.id.time);
        }
    }

    // load comments
    private void getComments(String postID, final TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Comments").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // set to invisible is troublesome
                if (snapshot.getChildrenCount() == 0) {
                    comments.setVisibility(View.GONE);
                } else {
                    comments.setVisibility(View.VISIBLE);
                    comments.setText("View all " + snapshot.getChildrenCount() + " comments");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // display if the post is liked by the current logon user
    private void isLiked(String postid, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firebaseUser != null) {
                    if (snapshot.child(firebaseUser.getUid()).exists()) {
                        imageView.setImageResource(R.drawable.ic_liked_green);
                        imageView.setTag("liked");
                    } else {
                        imageView.setImageResource(R.drawable.ic_like);
                        imageView.setTag(("like"));
                    }
//                    reference.removeEventListener(this);

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    // add like notification to database
    private void addNotifications(String userID, String postID) {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Notifications").child(userID);
        if (!firebaseUser.getUid().equals(userID)) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userID", firebaseUser.getUid());
            hashMap.put("comment_text", "liked your post");
            hashMap.put("postID", postID);
            hashMap.put("isPost", true);

            reference.push().setValue(hashMap);

        }
    }

    // display how many likes the post received
    private void postLikesDisplay(final TextView likes, final String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    likes.setVisibility(View.GONE);
                } else {
                    likes.setVisibility(View.VISIBLE);
                    likes.setText(snapshot.getChildrenCount() + " likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // display publisher information
    private void publisherInfo(final ImageView imageProfile, final TextView username, final String userId, final int position) {
        final DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
//                    final Context  context = mContext.getApplicationContext();

//                    if (isValidContextForGlide(context)) {
                    // Load image via Glide lib using context

                    Glide.with(mContext.getApplicationContext()).load(user.getProfileImage()).into(imageProfile);
                    username.setText(user.getUserName());
//                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // display if the post is saved by the current logon user
    private void isSaved(final String postid, final ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
                reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // post liked, userBeingLiked count + 1
    public void updateUserBeingLiked(Post post) {
        final DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Users")
                .child(post.getPostPublisher()).child("userBeingLiked");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer prevCount = 0;
                prevCount = snapshot.getValue(Integer.class);
                // in case data change will call this listener again
                reference.removeEventListener(this);
                try {
                    reference.setValue(prevCount + 1);

                } catch (NullPointerException ex) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // post unliked, userBeingLiked count -1
    public void updateUserBeingLikedCancelled(Post post) {
        final DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Users")
                .child(post.getPostPublisher()).child("userBeingLiked");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer prevCount = 0;
                prevCount = snapshot.getValue(Integer.class);
                reference.removeEventListener(this);
                try {
                    reference.setValue(prevCount - 1);
                } catch (NullPointerException exception) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // post being saved, postBeingSaved +1
    public void updatePostBeingSaved(Post post) {
        final DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts")
                .child(post.getPostID()).child("postBeingSaved");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer prevCount = snapshot.getValue(Integer.class);
                reference.removeEventListener(this);
                reference.setValue(prevCount + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // post being unsaved, postBeingSaved -1
    public void updatePostBeingSavedCancelled(Post post) {
        final DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts")
                .child(post.getPostID()).child("postBeingSaved");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer prevCount = snapshot.getValue(Integer.class);
                reference.removeEventListener(this);
                reference.setValue(prevCount - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // edit the post
    private void editPost(final String postid) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        // get the origin post content
        getText(postid, editText);

        // when the user click "Edit" button
        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // update the post content to db
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("postContent", editText.getText().toString());

                FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts").child(postid).updateChildren(hashMap);
            }
        });

        // when the user click "Cancel" button, don't update
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void editPosttwo(final String postid) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        // get the origin post content
        getText(postid, editText);

        // when the user click "Edit" button
        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // update the post content to db
                // HashMap<String, Object> hashMap = new HashMap<>();
                // hashMap.put("report", editText.getText().toString());

                //  FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts").child(postid).updateChildren(hashMap);
            }
        });

        // when the user click "Cancel" button, don't update
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }


    // get the origin post text when editing post
    private void getText(String postid, final EditText editText) {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts").child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editText.setText(snapshot.getValue(Post.class).getPostContent());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setreport(String postID,String userID) {
        final EditText taskEditText = new EditText(mContext);
        taskEditText.setHint("Report Details...");
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("Report")
                .setMessage("What do u find wrong in this post..")
                .setView(taskEditText)
                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("ReportPost").child(postID);
                        reportpostmodel reportpostmodel=new reportpostmodel(task,utils.getdate(),utils.gettime(),postID,userID);
                        reference.child(userID).setValue(reportpostmodel);
                        utils.showSuccessToast("Post Reported");

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    private void setrating(String postid,String userid){


       Dialog rankDialog = new Dialog(mContext, R.style.CustomRatingBar);
        rankDialog.setContentView(R.layout.dialog);
        rankDialog.setCancelable(true);
        RatingBar ratingBar = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                DatabaseReference reference=FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("PostRating").child(postid).child(userid);
                reference.child("rating").setValue(rating);

                DatabaseReference reference1=FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("PostRating").child(postid);

                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int totalpostedrating=0;
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            totalpostedrating+=dataSnapshot.child("rating").getValue(Integer.class);
                        }
                        Log.e("totalposted",""+totalpostedrating);

                        long count= snapshot.getChildrenCount();
                        Log.e("total reviews count",""+count);
                        float a=count*5;
                        float b=totalpostedrating/a;
                        Log.e("AAAAA",""+a);
                        Log.e("BBBB",""+b);
                        float average= b*5;
                        Log.e("Average",""+average);
                        FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Posts").child(postid).child("AvgRating").setValue(average);
                        Toast.makeText(mContext, "rating updated", Toast.LENGTH_SHORT).show();
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

}


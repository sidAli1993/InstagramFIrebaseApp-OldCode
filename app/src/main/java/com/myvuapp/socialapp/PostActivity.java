package com.myvuapp.socialapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.myvuapp.socialapp.Model.Post;
import com.myvuapp.socialapp.Model.User;
import com.myvuapp.socialapp.util.Constants;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myvuapp.socialapp.R;;


public class PostActivity extends AppCompatActivity {
    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageReference;

    private ImageView edit_post_photo_add, edit_post_cancel, edit_post_tip;
    private EditText edit_post_enter_title, edit_post_description, edtauthorname, edtprice;
    private Button edit_post_submit_button, tip_close;
    private Dialog edit_post_tip_dialog;
    private Spinner spinnershelve, spinnersale;
    private String selectedshelve, selectedsale;
    private ProgressDialog pd;
    private String currentDate;
    private List<String> mList, mListsale;
    User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_post);
        init();
        clicks();


    }

    private void init() {
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        edit_post_tip_dialog = new Dialog(this);
        edit_post_photo_add = findViewById(R.id.edit_post_photo_add);
        edit_post_enter_title = findViewById(R.id.edit_post_enter_title);
        edit_post_description = findViewById(R.id.edit_post_description);
        edit_post_cancel = findViewById(R.id.edit_post_cancel);
        edit_post_tip = findViewById(R.id.edit_post_tip);
        edtauthorname = findViewById(R.id.edtauthorname);
        edtprice = findViewById(R.id.edtprice);
        spinnershelve = findViewById(R.id.spinnershelves);
        spinnersale = findViewById(R.id.spinnertype);
        // open tip dialog
        edit_post_submit_button = findViewById(R.id.edit_post_submit_button);

        storageReference = FirebaseStorage.getInstance(Constants.Storage_URL).getReference("posts");

        // get photo information from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("imagePath")) {
            Log.e("get image Uri ", "" + Uri.parse(extras.getString("imagePath")));
            imageUri = Uri.parse(extras.getString("imagePath"));
            edit_post_photo_add.setImageURI(imageUri);
        }
        getUserCity();
        getSale();
        getshelves();

    }

    private void clicks() {
        // if edit cancelled, go back to home
        edit_post_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, StartActivity.class));
                finish();
            }
        });

        // show post tip
        edit_post_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        // submit post
        edit_post_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });
    }

    private void getUserCity(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference=FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child("Users").child(userID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user=snapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // upload post to database
    private void uploadPost() {
        pd = new ProgressDialog(PostActivity.this);
        pd.setMessage("Working hard to get your post uploaded...");
        pd.show();
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getMimeTypeFromUrl(imageUri));
            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Posts");
                        String postID = reference.push().getKey();


                        Post newPost = new Post(postID, user.getUserID(),
                                myUri, edit_post_enter_title.getText().toString(), edit_post_description.getText().toString(), currentDate, 0, edtauthorname.getText().toString(), edtprice.getText().toString(), selectedsale, selectedshelve,user.getCity());

                        reference.child(postID).setValue(newPost);
                        updateUserPostHistory(newPost);

                        // after post uploaded, go back to home
                        startActivity(new Intent(PostActivity.this, StartActivity.class));
                        finish();
                    } else {
                        Toast.makeText(PostActivity.this, "Something is wrong...Maybe try again?", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(PostActivity.this, "Something is wrong with the image! Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMimeTypeFromUrl(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // show post tips
    public void showDialog() {
        edit_post_tip_dialog.setContentView(R.layout.dialog_tip);
        edit_post_tip_dialog.setTitle("Some tips");

        tip_close = (Button) edit_post_tip_dialog.findViewById(R.id.tip_close);
        tip_close.setEnabled(true);

        tip_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_post_tip_dialog.cancel();
            }
        });

        edit_post_tip_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        edit_post_tip_dialog.show();
    }

    private void updateUserPostHistory(final Post newPost) {
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Users");
        Map<String, Object> postValues = newPost.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(newPost.getPostPublisher() + "/userPostHistory/" + newPost.getPostID(), postValues);
        reference.updateChildren(childUpdates);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (edit_post_tip_dialog != null) {
            edit_post_tip_dialog.dismiss();
        }
        if (pd != null) {
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (edit_post_tip_dialog != null) {
            edit_post_tip_dialog.dismiss();
        }
        if (pd != null) {
            pd.dismiss();
        }
    }

    private void getshelves() {
        // get user from database using profile id
        DatabaseReference reference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference("Shelves");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mList = new ArrayList<String>();
//                Toast.makeText(PostActivity.this, "" + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
//                String shelveName=snapshot.child("1").getValue(String.class);
//                Toast.makeText(PostActivity.this, ""+shelveName, Toast.LENGTH_SHORT).show();
                int i = 1;
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String shelveName=snapshot.child(String.valueOf(i)).getValue(String.class);
                    Toast.makeText(PostActivity.this, ""+shelveName, Toast.LENGTH_SHORT).show();
                    i++;
                    mList.add(shelveName);

                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PostActivity.this, R.layout.support_simple_spinner_dropdown_item, mList);
                spinnershelve.setAdapter(arrayAdapter);
                spinnershelve.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.e("SelectedShelve1",selectedsale);
                        selectedshelve = parent.getItemAtPosition(position).toString();
                        Log.e("SelectedShelve2",selectedsale);
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

    private void getSale() {
        mListsale = new ArrayList<String>();
        mListsale.add("Sale");
        mListsale.add("Rent");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PostActivity.this, R.layout.support_simple_spinner_dropdown_item, mListsale);
        spinnersale.setAdapter(arrayAdapter);
        spinnersale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedsale = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}

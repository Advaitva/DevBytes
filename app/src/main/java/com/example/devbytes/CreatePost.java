package com.example.devbytes; //TODO image compression

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.PointerIcon;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CreatePost extends AppCompatActivity {

    private Toolbar createPostToolbar;
    private ImageView new_postImage;
    private EditText new_postTitle;
    private EditText new_postBody;
    private Button postBtn;

    private Uri postImageUri=null;
    private ProgressBar postProgress;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        createPostToolbar=(Toolbar) findViewById(R.id.createPostToolbar);
        setSupportActionBar(createPostToolbar);

        getSupportActionBar().setTitle("Post a Byte");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new_postImage=findViewById(R.id.newpostImage);
        new_postTitle=findViewById(R.id.createPostTitle);
        new_postBody=findViewById(R.id.createPostDesc);
        postBtn=findViewById(R.id.createPostBtn);
        postProgress=findViewById(R.id.postProgressBar);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser().getUid();


        new_postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(2,1)
                        .start(CreatePost.this);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title=new_postTitle.getText().toString();
                final String body=new_postBody.getText().toString();
                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body) && postImageUri!=null){
                    postProgress.setVisibility(View.VISIBLE);
                    final String randomName = UUID.randomUUID().toString();;
                    StorageReference path=storageReference.child("post_byte").child(randomName+".jpg");
                    path.putFile(postImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
//                      Toast.makeText(Setup.this,"URL generated!",Toast.LENGTH_SHORT).show();
                                            String imageUrl = uri.toString();
                                            Map<String,Object> postMap=new HashMap<>();
                                            postMap.put("post_image",imageUrl);
                                            postMap.put("post_title",title);
                                            postMap.put("post_body",body);
                                            postMap.put("user_id",currentUser);
                                            postMap.put("timestamp", FieldValue.serverTimestamp());

                                            firebaseFirestore.collection("Posts").add(postMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(CreatePost.this,"Byte Posted!",Toast.LENGTH_SHORT).show();
                                                    Intent intent=new  Intent(CreatePost.this,MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    String errorMsg=e.getMessage();
                                                    Toast.makeText(CreatePost.this,"Error:",Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(CreatePost.this,"Error: "+errorMsg,Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            postProgress.setVisibility(View.INVISIBLE);

                                        }
                                    });
                                }

                        }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreatePost.this, "Error occurred while posting:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            postProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else{
                    Toast.makeText(CreatePost.this,"Please add all required items for post.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri=result.getUri();
                new_postImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(CreatePost.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

}
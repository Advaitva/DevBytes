package com.example.devbytes;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BytesRecyclerAdapter extends RecyclerView.Adapter<BytesRecyclerAdapter.ViewHolder> {

    private List<PostByte> postByteList;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BytesRecyclerAdapter(List<PostByte> postByteList){
        this.postByteList=postByteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.byte_item,parent,false);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        context=parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String bytePostId=postByteList.get(position).BytePostId;
        final String currentUserId=firebaseAuth.getCurrentUser().getUid();

        String titleData=postByteList.get(position).getPost_title();
        holder.setByteTitle(titleData);
        String imageData=postByteList.get(position).getPost_image();
        holder.setByteImage(imageData);
        String bodyData=postByteList.get(position).getPost_body();
        holder.setByteBody(bodyData);
        long milliseconds=postByteList.get(position).getTimestamp().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(milliseconds));
        holder.setDate(dateString);

        final String user_id=postByteList.get(position).getUser_id();

        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userData=document.getData();
                        if(user_id.equals(userData.get("user_id"))){
                            String name=(String)userData.get("user_name");
                            String photo=(String)userData.get("user_image") ;
                            holder.setUserData(photo,name);
                        }
                    }

                }
                else {
                    String errorMsg = task.getException().getMessage();
                    Log.e("ErrorAddapter",errorMsg);
                }
            }
        });


        firebaseFirestore.collection("Posts/" + bytePostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!value.isEmpty()){
                    int count=value.size();
                    holder.updateLikesCount(count);

                }
                else{
                    holder.updateLikesCount(0);
                }
            }
        });

        firebaseFirestore.collection("Posts/" + bytePostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    holder.byteLikeImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_byte_card_like_active));
                }
                else{
                    holder.byteLikeImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_byte_card_like));
                }
            }
        });

        holder.byteLikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/" + bytePostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){

                            Map<String,Object> likeMap=new HashMap<>();
                            likeMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + bytePostId + "/Likes").document(currentUserId).set(likeMap);

                        }
                        else{
                            firebaseFirestore.collection("Posts/" + bytePostId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return postByteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private  View mView;
        private TextView byteTitle,byteBody,userName,byteDate,byteLikeCount;
        private ImageView byteImage,byteLikeImage;
        private CircleImageView userImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            byteLikeImage=mView.findViewById(R.id.likeBtn);
        }
        public void setByteTitle(String titleText){
            byteTitle=mView.findViewById(R.id.byte_title);
            byteTitle.setText(titleText);
        }
        public void setByteImage(String imageUri){
            byteImage=mView.findViewById(R.id.byte_image);
            Glide.with(context).load(imageUri).into(byteImage);
        }
        public void setByteBody(String bodyText){
            byteBody=mView.findViewById(R.id.byte_body);
            byteBody.setText(bodyText);
        }
        public void setUserData(String userImageUri,String userNameText){

            RequestOptions placeHolder=new RequestOptions().placeholder(R.drawable.user_default);
            userImage=mView.findViewById(R.id.byteUserImage);
            Glide.with(context).applyDefaultRequestOptions(placeHolder).load(userImageUri).into(userImage);

            userName=mView.findViewById(R.id.byte_username);
            userName.setText(userNameText);
        }
        public void setDate(String date){
            byteDate=mView.findViewById(R.id.byte_date);
            byteDate.setText(date);
        }
        public void updateLikesCount(int likeCount){
            byteLikeCount=mView.findViewById(R.id.likeCount);
            byteLikeCount.setText(likeCount + " Likes");
        }
    }
}

package com.example.devbytes;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

    private RecyclerView postrecyclerView;

    private List<PostByte> byteList=new ArrayList<>();

    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private BytesRecyclerAdapter bytesRecyclerAdapter;

    public PostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                        if (error != null) {
                            System.err.println("Listen failed:" + error);
                            return;
                        }
                        for(DocumentChange doc: value.getDocumentChanges()){

                            if(doc.getType()== DocumentChange.Type.ADDED){
                                String bytePostId=doc.getDocument().getId();
                                PostByte postByte=doc.getDocument().toObject(PostByte.class).withId(bytePostId);

                                byteList.add(postByte);
                                bytesRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );

        View view =inflater.inflate(R.layout.fragment_posts,container,false);

        postrecyclerView=view.findViewById(R.id.byte_post_recycler);

        bytesRecyclerAdapter=new BytesRecyclerAdapter(byteList);
        postrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postrecyclerView.setHasFixedSize(true);
        postrecyclerView.setAdapter(bytesRecyclerAdapter);



        postrecyclerView=getActivity().findViewById(R.id.byte_post_recycler);
        return view;
    }
}
package com.example.devbytes;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BytePostId {
    @Exclude
    public  String BytePostId;

    public <T extends BytePostId> T withId(@NonNull final String id){
        this.BytePostId=id;
        return (T) this;
    }
}

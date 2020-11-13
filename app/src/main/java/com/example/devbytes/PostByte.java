package com.example.devbytes;


import java.util.Date;

public class PostByte extends BytePostId {
    public String user_id, post_image,post_title, post_body;
    Date timestamp;

    public PostByte(){

    }

    public PostByte( String post_title, String post_image, String user_id,String post_body,Date timestamp) {
        this.user_id = user_id;
        this.post_image = post_image;
        this.post_title = post_title;
        this.post_body = post_body;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_body() {
        return post_body;
    }

    public void setPost_body(String post_body) {
        this.post_body = post_body;
    }

    public Date getTimestamp() {
        return timestamp;

    }

    public void setTimestamp(Date timestamp) {
        this.timestamp=timestamp;
    }



}

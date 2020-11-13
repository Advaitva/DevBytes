package com.example.devbytes;

public class TechAPIModel {

    public String author_name,post_title,post_body,post_image;

    public TechAPIModel(){
        //Empty constructor
    }
    public TechAPIModel(String author_name, String post_title, String post_body, String post_image) {
        this.author_name = author_name;
        this.post_title = post_title;
        this.post_body = post_body;
        this.post_image = post_image;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
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

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }
}

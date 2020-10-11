package com.example.devbytes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class CreatePost extends AppCompatActivity {

    private Toolbar createPostToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        createPostToolbar=(Toolbar) findViewById(R.id.createPostToolbar);
        setSupportActionBar(createPostToolbar);

        getSupportActionBar().setTitle("Post a byte");

    }
}
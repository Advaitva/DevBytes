package com.example.devbytes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;


public class ReadTech extends AppCompatActivity {
    private Toolbar readByteToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_tech);

        readByteToolbar=findViewById(R.id.readByteToolbar);
        setSupportActionBar(readByteToolbar);

        TextView textView = findViewById(R.id.textView);
        String update = getIntent().getStringExtra("Tech");
        getSupportActionBar().setTitle(update);
        textView.setText("Content goes here...");
    }
}

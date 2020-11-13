package com.example.devbytes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity{

    private Toolbar mainActivityToolbar;
    private FloatingActionButton addPostBtn,feedBtn;
    private String current_user_Id;

    private FirebaseAuth mAuth;



    private FirebaseFirestore db;

    private BottomNavigationView bottomNavigationView;

    private PostsFragment postsFragment;
    private  NotificationFragment notificationFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser()!=null) {


            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            //Fragments
            postsFragment = new PostsFragment();
            notificationFragment = new NotificationFragment();
            replaceFragment(postsFragment);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_nav_bytes:
                            replaceFragment(postsFragment);
                            return true;

                        case R.id.bottom_nav_notif:
                            replaceFragment(notificationFragment);
                            return true;
                        default:
                            return false;
                    }
                }
            });

            mainActivityToolbar = (Toolbar) findViewById(R.id.mainActivityToolbar);
            setSupportActionBar(mainActivityToolbar);

            getSupportActionBar().setTitle("DevBytes");

            addPostBtn = findViewById(R.id.action_add);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CreatePost.class);
                    startActivity(intent);
                }
            });
            feedBtn=findViewById(R.id.action_feed);
            feedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                }
            });

        }

    };



    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null){
            sendToLogin();
        }
        else{
            current_user_Id=mAuth.getCurrentUser().getUid();
            db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (DocumentSnapshot document : task.getResult()) {
                            if(!document.exists()){  //TODO Modifications required
                                sendToSetup();
                            }
                        }

                    }
                    else{
                        String errorMsg=task.getException().getMessage();
                        Toast.makeText(MainActivity.this,"Error: "+errorMsg,Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout_btn:
                logout();
                return true;

            case R.id.action_setting_btn:
                sendToSetup();
                return true;

            default:return false;
        }
    }


    public void sendToLogin(){
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void sendToSetup(){
        Intent intent=new Intent(MainActivity.this,Setup.class);
        startActivity(intent);
        finish();
    }
    public void logout(){
        mAuth.signOut();
        sendToLogin();
    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragContainer,fragment);
        fragmentTransaction.commit();
    }
}
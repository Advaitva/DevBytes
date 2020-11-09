package com.example.devbytes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickInterface{


    private Toolbar mainActivityToolbar;
    private FloatingActionButton addPostBtn;

    private FirebaseAuth mAuth;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    List<String> techUpdateTitle;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        mainActivityToolbar=(Toolbar) findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(mainActivityToolbar);

        getSupportActionBar().setTitle("DevBytes");

        addPostBtn=findViewById(R.id.action_add);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CreatePost.class);
                startActivity(intent);
            }
        });

        techUpdateTitle = new ArrayList<>();
        RequestQueue requestQueue=Volley.newRequestQueue(this);

        String api="https://devbytes-api.herokuapp.com/data";
//        String api="https://randomuser.me/api";


        JsonArrayRequest objectRequest= new JsonArrayRequest(Request.Method.GET, api, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0;i<response.length();i++){
                                JSONObject obj=response.getJSONObject(i);
                                techUpdateTitle.add(obj.getString("post_title"));

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                 },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API response",error.toString());
                        Toast.makeText(MainActivity.this, "Something went wrong during api call", Toast.LENGTH_SHORT).show();
                    }
        });
        requestQueue.add(objectRequest);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(techUpdateTitle, this);
        recyclerView.setAdapter(recyclerAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }
    String deletedTech = null;
    List<String> archivedTech = new ArrayList<>();
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    deletedTech = techUpdateTitle.get(position);
                    techUpdateTitle.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Snackbar.make(recyclerView, deletedTech, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    techUpdateTitle.add(position, deletedTech);
                                    recyclerAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    final String tech = techUpdateTitle.get(position);
                    archivedTech.add(tech);

                    techUpdateTitle.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, tech + ", Archived.", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    archivedTech.remove(archivedTech.lastIndexOf(tech));
                                    techUpdateTitle.add(position, tech);
                                    recyclerAdapter.notifyItemInserted(position);
                                }
                            }).show();

                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(MainActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark))
                    .addSwipeRightActionIcon(R.drawable.ic_archive_black_24dp)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };



    //    These are the interface Methods from our custom RecyclerViewClickInterface
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ReadTech.class);
        intent.putExtra("Tech", techUpdateTitle.get(position));
        startActivity(intent);
    }
    @Override
    public void onLongItemClick(final int position) {
//        techUpdateTitle.remove(position);
//        recyclerAdapter.notifyItemRemoved(position);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null){
            sendToLogin();
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
}
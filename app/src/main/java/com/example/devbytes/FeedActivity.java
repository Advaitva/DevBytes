package com.example.devbytes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FeedActivity extends AppCompatActivity  {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    List<TechAPIModel> techUpdate;

    SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar feedActivityToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        feedActivityToolbar=(Toolbar) findViewById(R.id.feedActivityToolbar);
        setSupportActionBar(feedActivityToolbar);

        getSupportActionBar().setTitle("Tech Feed");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        techUpdate = new ArrayList<>();
        RequestQueue requestQueue= Volley.newRequestQueue(FeedActivity.this);

        String api="https://devbytes-api.herokuapp.com/data";
//        String api="https://randomuser.me/api";

        JsonArrayRequest objectRequest= new JsonArrayRequest(Request.Method.GET, api, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for(int i=0;i<response.length();i++){
                        JSONObject obj=response.getJSONObject(i);
                        Gson gson=new Gson();
                        TechAPIModel techAPIModel=new TechAPIModel();
                        techAPIModel=gson.fromJson(obj.toString(),TechAPIModel.class);
                        techUpdate.add(techAPIModel);//Do here
                        recyclerAdapter.notifyDataSetChanged();
                        Log.e("test",techAPIModel.toString());

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
                    }
                });
        requestQueue.add(objectRequest);
        Log.e("Data", String.valueOf(techUpdate));
        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(techUpdate);
        recyclerView.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);
//
//
//        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//
//                recyclerAdapter.notifyDataSetChanged();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

    }



//    String deletedMovie = null;
//    List<String> archivedMovies = new ArrayList<>();
//
//    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
//
//            final int position = viewHolder.getAdapterPosition();
//
//            switch (direction) {
//                case ItemTouchHelper.LEFT:
//                    deletedMovie = techUpdate.get(position);
//                    techUpdate.remove(position);
//                    recyclerAdapter.notifyItemRemoved(position);
//                    Snackbar.make(recyclerView, deletedMovie, Snackbar.LENGTH_LONG)
//                            .setAction("Undo", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    techUpdate.add(position, deletedMovie);
//                                    recyclerAdapter.notifyItemInserted(position);
//                                }
//                            }).show();
//                    break;
//                case ItemTouchHelper.RIGHT:
//                    final String tech = techUpdate.get(position);
//                    archivedMovies.add(tech);
//
//                    techUpdate.remove(position);
//                    recyclerAdapter.notifyItemRemoved(position);
//
//                    Snackbar.make(recyclerView, tech + ", Archived.", Snackbar.LENGTH_LONG)
//                            .setAction("Undo", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    archivedMovies.remove(archivedMovies.lastIndexOf(tech));
//                                    techUpdate.add(position, tech);
//                                    recyclerAdapter.notifyItemInserted(position);
//                                }
//                            }).show();
//
//                    break;
//            }
//        }
//
//        @Override
//        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//
//            new RecyclerViewSwipeDecorator.Builder(FeedActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(FeedActivity.this, R.color.colorAccent))
//                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
//                    .addSwipeRightBackgroundColor(ContextCompat.getColor(FeedActivity.this, R.color.colorPrimaryDark))
//                    .addSwipeRightActionIcon(R.drawable.ic_archive_black_24dp)
//                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
//                    .create()
//                    .decorate();
//
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        }
//    };



//    //    These are the interface Methods from our custom RecyclerViewClickInterface
//    @Override
//    public void onItemClick(int position) {
//        Intent intent = new Intent(this, ReadTech.class);
//        intent.putExtra("Tech", techUpdate.get(position));
//        startActivity(intent);
//    }
//
//    @Override
//    public void onLongItemClick(final int position) {
////        techUpdate.remove(position);
////        recyclerAdapter.notifyItemRemoved(position);
//    }
}

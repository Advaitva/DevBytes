package com.example.devbytes;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FeedFragment extends Fragment implements RecyclerViewClickInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    List<String> techUpdateTitle;
    SwipeRefreshLayout swipeRefreshLayout;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_feed,container,false);
        techUpdateTitle = new ArrayList<>();
        RequestQueue requestQueue= Volley.newRequestQueue(container.getContext());

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
                    }
                });
        requestQueue.add(objectRequest);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(techUpdateTitle, this);
        recyclerView.setAdapter(recyclerAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return inflater.inflate(R.layout.fragment_feed, container, false);
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
            new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark))
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
        Intent intent = new Intent(getContext(), ReadTech.class);
        intent.putExtra("Tech", techUpdateTitle.get(position));
        startActivity(intent);
    }
    @Override
    public void onLongItemClick(final int position) {
//        techUpdateTitle.remove(position);
//        recyclerAdapter.notifyItemRemoved(position);
    }
}

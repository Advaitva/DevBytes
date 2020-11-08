package com.example.devbytes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import java.util.ArrayList;
import java.util.List;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickInterface{


    private Toolbar mainActivityToolbar;
    private FloatingActionButton addPostBtn;

    private FirebaseAuth mAuth;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    List<String> techUpdate;
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

        techUpdate = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(techUpdate, this);
        recyclerView.setAdapter(recyclerAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        techUpdate.add("Tech Update Title 1");
        techUpdate.add("Tech Update Title 2");
        techUpdate.add("Tech Update Title 3");
        techUpdate.add("Tech Update Title 4");
        techUpdate.add("Tech Update Title 5");
        techUpdate.add("Tech Update Title 6");
        techUpdate.add("Tech Update Title 7");
        techUpdate.add("Tech Update Title 8");
        techUpdate.add("Tech Update Title 9");
        techUpdate.add("Tech Update Title 10");
        techUpdate.add("Tech Update Title 11");
        techUpdate.add("Tech Update Title 12");
        techUpdate.add("Tech Update Title 13");
        techUpdate.add("Tech Update Title 14");
        techUpdate.add("Tech Update Title 15");
        techUpdate.add("Tech Update Title 16");
        techUpdate.add("Tech Update Title 17");
        techUpdate.add("Tech Update Title 18");
        techUpdate.add("Tech Update Title 19");
        techUpdate.add("Tech Update Title 20");
        techUpdate.add("Tech Update Title 21");
        techUpdate.add("Tech Update Title 22");
        techUpdate.add("Tech Update Title 23");

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                techUpdate.add("Tech Update Title 24");
                techUpdate.add("Tech Update Title 25");
                techUpdate.add("Tech Update Title 26");
                techUpdate.add("Tech Update Title 27");
                techUpdate.add("Tech Update Title 28");

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
                    deletedTech = techUpdate.get(position);
                    techUpdate.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    Snackbar.make(recyclerView, deletedTech, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    techUpdate.add(position, deletedTech);
                                    recyclerAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    final String tech = techUpdate.get(position);
                    archivedTech.add(tech);

                    techUpdate.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, tech + ", Archived.", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    archivedTech.remove(archivedTech.lastIndexOf(tech));
                                    techUpdate.add(position, tech);
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
        intent.putExtra("Tech", techUpdate.get(position));
        startActivity(intent);
    }
    @Override
    public void onLongItemClick(final int position) {
//        techUpdate.remove(position);
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
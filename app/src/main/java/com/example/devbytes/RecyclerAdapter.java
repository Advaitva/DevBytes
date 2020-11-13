package com.example.devbytes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    public Context context;
    private static final String TAG = "RecyclerAdapter";
    List<TechAPIModel> techUpdate;

    public RecyclerAdapter(List<TechAPIModel> techUpdate) {
        this.techUpdate = techUpdate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        context=parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.rowCountTextView.setText(String.valueOf(position));
        Glide.with(context).load(techUpdate.get(position).getPost_image()).into(holder.imageView);
        holder.title.setText(techUpdate.get(position).getPost_title());
        holder.author.setText(techUpdate.get(position).getAuthor_name());
        holder.body.setText(techUpdate.get(position).getPost_body());


    }

    @Override
    public int getItemCount() {
        return techUpdate.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, author,body;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.feedImage);
            title = itemView.findViewById(R.id.feedTitle);
            author = itemView.findViewById(R.id.feedAuthor);
            body=itemView.findViewById(R.id.feedBody);

        }

    }

}
















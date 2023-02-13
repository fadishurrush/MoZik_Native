package com.example.mozik;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder>{

    ArrayList<AudioModel> songslist;
    Context context;

    public MusicListAdapter(ArrayList<AudioModel> songslist, Context context) {
        this.songslist = songslist;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new MusicListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
    AudioModel songsData = songslist.get(position);
    holder.tvTitleMusic.setText(songsData.getTitle());
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MyMediaPlayer.getInstance().reset();
            MyMediaPlayer.currentIndex= holder.getAdapterPosition();
            Intent intent = new Intent(context,MusicPlayerActivity.class);
            intent.putExtra("LIST" , songslist);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    });
    if (MyMediaPlayer.currentIndex== holder.getAdapterPosition()){
        holder.tvTitleMusic.setTextColor(Color.parseColor("#FF0000"));
    }else{
        holder.tvTitleMusic.setTextColor(Color.parseColor("#000000"));

    }
    }

    @Override
    public int getItemCount() {
        return songslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitleMusic;
        private ImageView iconImage;
        public ViewHolder(View itemView){
            super(itemView);
            tvTitleMusic = itemView.findViewById(R.id.tvmusicTitle);
            iconImage = itemView.findViewById(R.id.icon_view);

        }
    }
}

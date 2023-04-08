package com.example.mozik;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    FireBaseServices fbs = FireBaseServices.getinstance();
    FirebaseAuth mAuth = fbs.getmAuth();
    FirebaseFirestore db = fbs.getDb();
    ArrayList<AudioModel> songslist;
    Context context;
    Boolean flag = true;

    public MusicListAdapter(ArrayList<AudioModel> songslist, Context context) {
        this.songslist = songslist;
        this.context = context;
    }

    public void setFilteredList(ArrayList<AudioModel> filteredList) {
        this.songslist = filteredList;
        notifyDataSetChanged();
    }

    public void setSongslist(ArrayList<AudioModel> list) {
        this.songslist = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MusicListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AudioModel songsData = songslist.get(position);
        holder.d3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, holder);
            }
        });
        holder.tvTitleMusic.setText(songsData.getTitle());
        holder.tvTitleMusic.setSelected(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MyMediaPlayer.previousSong.equals(songslist.get(holder.getAdapterPosition()).getTitle())) {
                    MyMediaPlayer.getInstance().reset();
                    MyMediaPlayer.currentTitle = songslist.get(holder.getAdapterPosition()).getTitle();
                    MyMediaPlayer.currentIndex = holder.getAdapterPosition();
                }
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putExtra("LIST", songslist);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        if (holder.tvTitleMusic.getText().toString().toLowerCase().equals(MyMediaPlayer.currentTitle.toLowerCase())) {
            holder.tvTitleMusic.setTextColor(Color.parseColor("#FF0000"));
        } else {
            holder.tvTitleMusic.setTextColor(Color.parseColor("#000000"));

        }
    }

    public void showPopup(View v, ViewHolder holder) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.d3, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "Fav":{
                        add(holder.tvTitleMusic.getText().toString());
                        return true;
                    }
                    default:
                        return false;
                }
            }

            ;
        });
        popup.show();
    }

    public void add(String text) {
        read(text);
    }

    private void read(String text) {
        db.collection("Favs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    if (document.get("user").equals(mAuth.getUid())) {
                                        if (document.get("fav").equals(text)) {
                                            flag = false;
                                            db.collection("Favs").document(document.getId())
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(context, "removed from fav", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, "failed to remove", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                            if (flag){
                                Map<String, Object> Fav = new HashMap<>();
                                Fav.put("user", mAuth.getUid());
                                Fav.put("fav", text);



                                db.collection("Favs")
                                        .add(Fav)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                flag = true;
                                                Toast.makeText(context, "added to fav", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "didn't succeed", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }
                                flag= true;



                        } else {

                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return songslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitleMusic;
        private ImageView iconImage;
        private ImageButton d3;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitleMusic = itemView.findViewById(R.id.tvmusicTitle);
            iconImage = itemView.findViewById(R.id.icon_view);
            d3 = itemView.findViewById(R.id.d3);

        }
    }

}



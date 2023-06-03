package com.capstone481p.snoozeyoulose.ui.users;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone481p.snoozeyoulose.R;
import com.capstone481p.snoozeyoulose.ui.chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;



public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.UserHolder> {

    Context context;
    FirebaseAuth firebaseAuth;
    String uid;

    List<ModelUsers> list;

    public AdapterUsers(Context context, List<ModelUsers> list) {
        this.context = context;
        this.list = list;
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, final int position) {
        final String fid = list.get(position).getUid();
        String userImage = list.get(position).getImage();
        String userName = list.get(position).getName();
        String userEmail = list.get(position).getEmail();
        holder.name.setText(userName);
        holder.email.setText(userEmail);
        try {
            // finds the image tag within the drawable resources
            int userImageID = context.getResources().getIdentifier(userImage, "drawable", context.getPackageName());
            Glide.with(context).load(userImageID).into(holder.profile);
        } catch (Exception ignored) {
        }

        // Clicking on a user card will start the chat activity and add the user to the
        // current user's chat list
        holder.itemView.setOnClickListener(v -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference reference = database.getReference("ChatList");

            reference.child(uid).child(fid).child("id").setValue(fid);
            Intent intent = new Intent(context, ChatActivity.class);

            intent.putExtra("uid", fid);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView name, email;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.imageu);
            name = itemView.findViewById(R.id.nameu);
            email = itemView.findViewById(R.id.emailu);
        }
    }
}
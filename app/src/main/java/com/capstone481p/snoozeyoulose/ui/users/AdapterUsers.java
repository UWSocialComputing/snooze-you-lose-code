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
import com.capstone481p.snoozeyoulose.ui.chat.ModelChatList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

// import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    FirebaseAuth firebaseAuth;
    String uid;

    public AdapterUsers(Context context, List<ModelUsers> list) {
        this.context = context;
        this.list = list;
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
    }

    List<ModelUsers> list;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final String hisuid = list.get(position).getUid();
        String userImage = list.get(position).getImage();
        String username = list.get(position).getName();
        String usermail = list.get(position).getEmail();
        holder.name.setText(username);
        holder.email.setText(usermail);
        try {
            Glide.with(context).load(userImage).into(holder.profiletv);
        } catch (Exception e) {
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                // store the value in Database in "Users" Node
                DatabaseReference reference = database.getReference("ChatList");

                // storing the value in Firebase
                reference.child(uid).setValue(hisuid);
                Intent intent = new Intent(context, ChatActivity.class);

                // putting uid of user in extras
                intent.putExtra("uid", hisuid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView profiletv;
        TextView name, email;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profiletv = itemView.findViewById(R.id.imagep);
            name = itemView.findViewById(R.id.namep);
            email = itemView.findViewById(R.id.emailp);
        }
    }
}
package com.capstone481p.snoozeyoulose.ui.chat;

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
import com.capstone481p.snoozeyoulose.ui.users.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.ChatListHolder> {

    Context context;
    FirebaseAuth firebaseAuth;
    String uid;

    public AdapterChatList(Context context, List<ModelUsers> users) {
        this.context = context;
        this.usersList = users;
        lastMessageMap = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
    }

    List<ModelUsers> usersList;
    private HashMap<String, String> lastMessageMap;

    @NonNull
    @Override
    public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new ChatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListHolder holder, final int position) {

        final String fid = usersList.get(position).getUid();
        String userImage = usersList.get(position).getImage();
        String userName = usersList.get(position).getName();
        String lastMessage = lastMessageMap.get(fid);
        holder.name.setText(userName);

        // Handling for chats with no last messages
        if (lastMessage == null || lastMessage.equals("default")) {
            holder.lastMessage.setVisibility(View.GONE);
        } else {
            holder.lastMessage.setVisibility(View.VISIBLE);
            holder.lastMessage.setText(lastMessage);
        }
        try {
            // finding the profile image in drawable
            int userImageId= context.getResources().getIdentifier(userImage, "drawable", context.getPackageName());
            Glide.with(context).load(userImageId).into(holder.profile);
        } catch (Exception ignored) {

        }

        // redirecting to chat activity on item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);

                // putting uid of user in extras
                intent.putExtra("uid", fid);
                context.startActivity(intent);
            }
        });

    }

    // setting last message sent by users.
    public void setLastMessageMap(String userId, String lastMessage) {
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class ChatListHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView name, lastMessage;

        public ChatListHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profileimage);
            name = itemView.findViewById(R.id.namechat);
            lastMessage = itemView.findViewById(R.id.lastmessge);
        }
    }
}

package com.capstone481p.snoozeyoulose.ui.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone481p.snoozeyoulose.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ChatHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> list;
    String imageURL;
    FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<ModelChat> list, String imageURL) {
        this.context = context;
        this.list = list;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // Handles different layouts based on the sender and receiver
        if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
        }
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int pos) {
        final int position = pos;
        String message = list.get(position).getMessage();
        String timeStamp = list.get(position).getTimestamp();
        String type = list.get(position).getType();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String timeDate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.message.setText(message);
        holder.time.setText(timeDate);
        try {
            Glide.with(context).load(imageURL).into(holder.image);
        } catch (Exception ignored) {

        }
        if (type.equals("text")) {
            holder.message.setVisibility(View.VISIBLE);
            holder.messageImage.setVisibility(View.GONE);
            holder.message.setText(message);
        } else {
            holder.message.setVisibility(View.GONE);
            holder.messageImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(message).into(holder.messageImage);
        }

        holder.msgLayout.setOnClickListener(v -> {
            // On clicking a message, builds an alert that prompts the user on whether or not
            // they want to delete the message
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Message");
            builder.setMessage("Are You Sure To Delete This Message");
            builder.setPositiveButton("Delete", (dialog, which) -> deleteMsg(position));
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    private void deleteMsg(int position) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timestamp = list.get(position).getTimestamp();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        Query query = reference.orderByChild("timestamp").equalTo(timestamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handles message deletion in the database
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("sender").getValue().equals(uid)) {
                        dataSnapshot1.getRef().removeValue();
                    } else {
                        Toast.makeText(context, "Unable to delete another person's message", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageView messageImage;
        TextView message, time, isSee;
        LinearLayout msgLayout;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profileimage);
            message = itemView.findViewById(R.id.msgc);
            time = itemView.findViewById(R.id.timetv);
            isSee = itemView.findViewById(R.id.isSeen);
            msgLayout = itemView.findViewById(R.id.msglayout);
            messageImage = itemView.findViewById(R.id.images);
        }
    }
}

package com.capstone481p.snoozeyoulose.ui.chat;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone481p.snoozeyoulose.R;
import com.capstone481p.snoozeyoulose.ui.users.AddUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatList> chatListList;
    List<com.capstone481p.snoozeyoulose.ui.users.ModelUsers> usersList;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    AdapterChatList adapterChatList;
    List<ModelChat> chatList;

    public ChatFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        Button addNew = view.findViewById(R.id.add_chat_button);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.chatlistrecycle);
        // The list of people the user is chatting with
        chatListList = new ArrayList<>();
        // The list of chats the user has
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListList.clear();
                // Loading people who the user is currently chatting with
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChatList modelChatList = ds.getValue(ModelChatList.class);
                    if (!modelChatList.getId().equals(firebaseUser.getUid())) {
                        chatListList.add(modelChatList);
                    }

                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Add user button starts the Add User Activity
        addNew.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddUserActivity.class);
            requireActivity().startActivity(intent);
        });

        return view;
    }

    /**
     * Loads all of the friends the current user is engaged in chats with
     */
    private void loadChats() {
        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                // Loading people who the user is currently chatting with
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    com.capstone481p.snoozeyoulose.ui.users.ModelUsers user = dataSnapshot1.getValue(com.capstone481p.snoozeyoulose.ui.users.ModelUsers.class);
                    for (ModelChatList chatList : chatListList) {
                        assert user != null;
                        if (user.getUid() != null && user.getUid().equals(chatList.getId())) {
                            usersList.add(user);
                            break;
                        }
                    }
                    adapterChatList = new AdapterChatList(getActivity(), usersList);
                    recyclerView.setAdapter(adapterChatList);

                    for (int i = 0; i < usersList.size(); i++) {
                        lastMessage(usersList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Retrieves the last message with friends in the chat list
     * @param uid id of the friend
     */
    private void lastMessage(final String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Gets the relevant information from the chats in the database
                // to display on the fragment
                String lastMessage = "default";
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelChat chat = dataSnapshot1.getValue(ModelChat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null) {
                        continue;
                    }
                    if (chat.getReceiver().equals(firebaseUser.getUid()) &&
                            chat.getSender().equals(uid) ||
                            chat.getReceiver().equals(uid) &&
                                    chat.getSender().equals(firebaseUser.getUid())) {
                        if (chat.getType().equals("images")) {
                            lastMessage = "Sent a Photo";
                        } else {
                            lastMessage = chat.getMessage();
                        }
                    }
                }
                adapterChatList.setLastMessageMap(uid, lastMessage);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

}
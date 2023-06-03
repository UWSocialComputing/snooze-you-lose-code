package com.capstone481p.snoozeyoulose.ui.users;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone481p.snoozeyoulose.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddUserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUsers> usersList;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addusers);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclek);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddUserActivity.this));
        usersList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        getAllUsers();
    }

    /**
     * Retrieves all registered users from the database and starts adding sets of information
     * per user to the recycler view
     */
    private void getAllUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelUsers modelUsers = dataSnapshot1.getValue(ModelUsers.class);
                    assert modelUsers != null;
                    if (modelUsers.getUid() != null) {
                        assert firebaseUser != null;
                        if (!modelUsers.getUid().equals(firebaseUser.getUid())) {
                            usersList.add(modelUsers);
                        }
                    }
                    adapterUsers = new AdapterUsers(AddUserActivity.this, usersList);
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}

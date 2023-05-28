package com.capstone481p.snoozeyoulose.ui.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.capstone481p.snoozeyoulose.R;
import com.capstone481p.snoozeyoulose.ui.GlobalVars;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private Context context;
    private Button awakeButton;
    private Button settingsButton;
    private String userName;
    private String accountability;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d("CONTEXT", "Context for the fragment: " + getContext().getPackageName());

        context = getContext();

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Firebase", snapshot.toString()); // Log the snapshot for debugging
                userName = snapshot.child("name").getValue(String.class);
                Log.d("Firebase", "UserName: " + userName); // Log the retrieved username
                Log.d("Firebase", "accountability: " + GlobalVars.accountabilityType); // Log the retrieved username
                TextView welcomeUserText = view.findViewById(R.id.welcomeUserText);
                welcomeUserText.setText("Welcome back, " + userName);

                // accountability = snapshot.child("accountability").getValue(String.class);
                TextView currentSettings = view.findViewById(R.id.currentSettings);
                String multiTxt = "Wake Up Time \t8:30am\n" +
                        "BedTime\t10:25pm\n" +
                        "Accountability\t" + GlobalVars.accountabilityType;
                currentSettings.setText(multiTxt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userName = "could not retrieve name";
                accountability = "could not retrieve accountability";
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
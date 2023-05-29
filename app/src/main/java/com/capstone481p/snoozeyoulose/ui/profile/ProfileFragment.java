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
import android.widget.Toast;

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
    private Button sleepButton;
    private String userName;
    private String wakeUpTime;
    private String bedTime;
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
                bedTime = snapshot.child("bedTime").getValue(String.class);
                wakeUpTime = snapshot.child("wakeupTime").getValue(String.class);
                Log.d("Firebase", "UserName: " + userName); // Log the retrieved username
                Log.d("Firebase", "accountability: " + GlobalVars.accountabilityType); // Log the retrieved username
                TextView welcomeUserText = view.findViewById(R.id.welcomeUserText);
                welcomeUserText.setText("Welcome back, " + userName);

                // accountability = snapshot.child("accountability").getValue(String.class);
                TextView currentSettings = view.findViewById(R.id.currentSettings);
                String multiTxt = "Wake Up Time:\t\t" + wakeUpTime +"\n" +
                        "BedTime:\t\t" + bedTime + "\n" +
                        "Accountability:\t\t" + GlobalVars.accountabilityType;
                currentSettings.setText(multiTxt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userName = "could not retrieve name";
                accountability = "could not retrieve accountability";
            }
        });

        awakeButton = view.findViewById(R.id.awakeButton);
        awakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awakeMessage();
            }
        });

        sleepButton = view.findViewById(R.id.sleepButton);
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sleepMessage();
            }
        });
    }

    public void awakeMessage() {
        Toast.makeText(context, "Congrats on waking up!\nRemember to slay the day 💅", Toast.LENGTH_SHORT).show();
    }

    public void sleepMessage() {
        Toast.makeText(context, "Remember to get that beauty sleep\nand go to bed soon 😪", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
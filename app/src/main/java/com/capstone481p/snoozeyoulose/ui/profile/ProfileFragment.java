package com.capstone481p.snoozeyoulose.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone481p.snoozeyoulose.R;
import com.capstone481p.snoozeyoulose.ui.GlobalVars;
import com.capstone481p.snoozeyoulose.ui.chat.ModelChatList;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    private Context context;
    private Button awakeButton;
    private Button sleepButton;
    private String userName;
    private String wakeUpTime;
    private String bedTime;
    private String accountability;

    private String awakeCount;
    private String sleepCount;
    private String counter;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

//        awakeCount = sharedPreferences.getString("awakeCount", "0");
//        sleepCount = sharedPreferences.getString("sleepCount", "0");
//        counter = sharedPreferences.getString("counter", "0");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Firebase", snapshot.toString()); // Log the snapshot for debugging
                userName = snapshot.child("name").getValue(String.class);
                bedTime = snapshot.child("bedTime").getValue(String.class);
                wakeUpTime = snapshot.child("wakeupTime").getValue(String.class);
                awakeCount = snapshot.child("awakeCount").getValue(String.class);
                sleepCount = snapshot.child("sleepCount").getValue(String.class);
                counter = snapshot.child("counter").getValue(String.class);
                Log.d("Firebase", "UserName: " + userName); // Log the retrieved username
                Log.d("Firebase", "accountability: " + GlobalVars.accountabilityType); // Log the retrieved username
                TextView welcomeUserText = view.findViewById(R.id.welcomeUserText);
                welcomeUserText.setText("Welcome back, " + userName);

                // accountability = snapshot.child("accountability").getValue(String.class);
                // setting the default text views for settings
                TextView currentSettings = view.findViewById(R.id.currentSettings);
                currentSettings.setText("current settings info");
                TextView wakeupTimeTxt = view.findViewById(R.id.wakeupTimeTxt);
                wakeupTimeTxt.setText("wakeup time");
                TextView bedTimeTxt = view.findViewById(R.id.bedTimeTxt);
                bedTimeTxt.setText("bed time");
                TextView accountabilityTxt = view.findViewById(R.id.accountabilityTxt);
                accountabilityTxt.setText("accountability type");

                // making and setting the variable text views for settings
                TextView wakeupTimeNum = view.findViewById(R.id.wakeupTimeNum);
                wakeupTimeNum.setText(wakeUpTime);
                TextView bedTimeNum = view.findViewById(R.id.bedTimeNum);
                bedTimeNum.setText(bedTime);
                TextView accountabilitySelected = view.findViewById(R.id.accountabilitySelected);
                accountabilitySelected.setText(GlobalVars.accountabilityType);


//                String multiTxt = "Wake Up Time:\t\t" + wakeUpTime +"\n" +
//                        "BedTime:\t\t" + bedTime + "\n" +
//                        "Accountability:\t\t" + GlobalVars.accountabilityType;
//                currentSettings.setText(multiTxt);

                int tempAwakeNum = Integer.valueOf(awakeCount);
                int tempSleepNum = Integer.valueOf(sleepCount);
                int tempCountNum;
                if (tempAwakeNum == tempSleepNum) {
                    tempCountNum = tempAwakeNum;
                } else {
                    tempCountNum = Math.min(tempAwakeNum, tempSleepNum);
                }
                counter = String.valueOf(tempCountNum);
                ref.child("counter").setValue(counter);
                editor.putString("counter", counter);
                editor.apply();

                // setting the default text views for progress
                TextView currentProgress = view.findViewById(R.id.progress);
                currentProgress.setText("current progress");
                TextView totalCyclesTxt = view.findViewById(R.id.totalCyclesTxt);
                totalCyclesTxt.setText("total cycles");
                TextView awakeButtonTxt = view.findViewById(R.id.awakeButtonTxt);
                awakeButtonTxt.setText("times woken up");
                TextView sleepButtonTxt = view.findViewById(R.id.sleepButtonTxt);
                sleepButtonTxt.setText("times slept");

                // making and setting the variable text views for progress
                TextView totalCyclesNum = view.findViewById(R.id.totalCyclesNum);
                totalCyclesNum.setText(counter);
                TextView awakeButtonNum = view.findViewById(R.id.awakeButtonNum);
                awakeButtonNum.setText(awakeCount);
                TextView sleepButtonNum = view.findViewById(R.id.sleepButtonNum);
                sleepButtonNum.setText(sleepCount);


//                String progressTxt = "Current Progress\n" +
//                        "Total Cycles: " + counter + "\n" +
//                        "Woken Up " + awakeCount + " times\n" +
//                        "Slept " + sleepCount + " times";
//                currentProgress.setText(progressTxt);
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
                String tempAwake = String.valueOf(Integer.valueOf(awakeCount) + 1);
                awakeCount = tempAwake;
                ref.child("awakeCount").setValue(tempAwake);
                editor.putString("awakeCount", awakeCount);
                editor.apply();
            }
        });

        sleepButton = view.findViewById(R.id.sleepButton);
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sleepMessage();
                String tempSleep = String.valueOf(Integer.valueOf(sleepCount) + 1);
                sleepCount = tempSleep;
                ref.child("sleepCount").setValue(tempSleep);
                editor.putString("sleepCount", sleepCount);
                editor.apply();
            }
        });
    }

    public void awakeMessage() {
        String message = "Congrats on waking up! Remember to slay the day 💅. " +
                "Do want to share that you've achieved this goal with friends?";

        // Creates a popup with a share button
        Snackbar snack = Snackbar.make(awakeButton, message, Snackbar.LENGTH_SHORT).setAction("Share", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("ChatList");

                // Gets "friends" from chat list
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DataSnapshot ds = dataSnapshot.child(uid);

                        String finalMessage = "I woke up on time today! Thanks for helping me improve my routine!";

                        for (DataSnapshot ds2 : ds.getChildren()) {
                            ModelChatList friend = ds2.getValue(ModelChatList.class);
                            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference();
                            String timestamp = String.valueOf(System.currentTimeMillis());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", uid);
                            hashMap.put("receiver", friend.getId());
                            hashMap.put("message", finalMessage);
                            hashMap.put("timestamp", timestamp);
                            hashMap.put("dilihat", false);
                            hashMap.put("type", "text");
                            ref3.child("Chats").push().setValue(hashMap);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        snack.setTextMaxLines(7);
        snack.show();
        //Toast.makeText(context, "Congrats on waking up!\nRemember to slay the day 💅", Toast.LENGTH_SHORT).show();
    }

    public void sleepMessage() {
        String message = "Remember to get that beauty sleep and go to bed soon 😪. "+
                "Do want to share that you've achieved this goal with friends?";

        // Creates a popup with a share button
        Snackbar snack = Snackbar.make(awakeButton, message, Snackbar.LENGTH_LONG).setAction("Share", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("ChatList");

                // Gets "friends" from chat list
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DataSnapshot ds = dataSnapshot.child(uid);

                        String finalMessage = "I'm going to bed now and turning off my phone! Please "
                                + "don't send me any messages that could keep me up!";

                        for (DataSnapshot ds2 : ds.getChildren()) {
                            ModelChatList friend = ds2.getValue(ModelChatList.class);
                            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference();
                            String timestamp = String.valueOf(System.currentTimeMillis());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", uid);
                            hashMap.put("receiver", friend.getId());
                            hashMap.put("message", finalMessage);
                            hashMap.put("timestamp", timestamp);
                            hashMap.put("dilihat", false);
                            hashMap.put("type", "text");
                            ref3.child("Chats").push().setValue(hashMap);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        snack.setTextMaxLines(7);
        snack.show();
        //Toast.makeText(context, "Remember to get that beauty sleep\nand go to bed soon 😪", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
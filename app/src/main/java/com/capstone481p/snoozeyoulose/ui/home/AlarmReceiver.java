package com.capstone481p.snoozeyoulose.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.capstone481p.snoozeyoulose.MainActivity;
import com.capstone481p.snoozeyoulose.R;
import com.capstone481p.snoozeyoulose.ui.chat.ModelChatList;
import com.capstone481p.snoozeyoulose.ui.users.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

//adding comment here to push this as well


public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_NAME = "Alarm Channel";
    private Context context;

    private boolean alarmType;


    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO: How do we handle different text for different alarms??
        this.context = context;

        this.alarmType = intent.getBooleanExtra("alarm_type", true);
        createNotificationChannel();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Setting notification text based on alarm type
        String notif_text = "It's time to ";
        if(alarmType) {
            notif_text += "go to bed!";
        } else {
            notif_text += "wake up!";
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Alarm")
                .setContentText(notif_text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Play default notification sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        // Set notification color
        builder.setColor(Color.RED);

        // Show the notification
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: finish permission handling
            Toast.makeText(context, "Jail, notifications are illegal", Toast.LENGTH_SHORT).show();
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
        sendNotificationToFriend(context);
    }
    private void sendNotificationToFriend(Context context) {
        // Code to send a notification to your friend
        // This is only done if you have send a text to your friend
        // as an accountability type

        // accountability selected t/f, name
        final String[] sendText = {"f",""};


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users");

        // Read in user info
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.child(uid);
                ModelUsers user = ds.getValue(ModelUsers.class);
                Log.d("TEXT_DEBUG", "READING DATA NOW BEEP BOOP");
                if(user.getAccountability().equals("Send a text to my friend")){
                    sendText[0] = "t";
                }
                sendText[1] = user.getName();

                Log.d("TEXT_DEBUG", "Reading: "+sendText[0]+ ", "+sendText[1]);
                Log.d("TEXT_DEBUG", "Did we get the accountability method: "+sendText[0]);
                Log.d("TEXT_DEBUG", "Did we get the name: "+sendText[1]);


                // Only sends a text if the correct accountability method is selected
                if(sendText[0].equals("t")) {

                    String wakeup_message = " to wake up! " +
                            "Could you check in on them and make sure they're meeting their goal?";

                    String bedtime_message = " to go to bed! " +
                            "If you see that they are still on their phone, please to remind them "+
                            "to stick to their goal and get some sleep.";

                    String message = "Hello! It is time for your friend " + sendText[1];

                    if(alarmType){
                        message += bedtime_message;
                    } else {
                        message += wakeup_message;
                    }

                    String finalMessage = message;

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList");

                    // Gets "friends" from chat list
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataSnapshot ds = dataSnapshot.child(uid);

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
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });




    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}

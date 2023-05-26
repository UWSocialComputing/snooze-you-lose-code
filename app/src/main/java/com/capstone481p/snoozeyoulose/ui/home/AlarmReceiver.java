package com.capstone481p.snoozeyoulose.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

//adding comment here to push this as well

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Perform any actions you want when the alarm goes off
        // For example, you can send a notification to your friend
        sendNotificationToFriend(context);
    }

    private void sendNotificationToFriend(Context context) {
        // Code to send a notification to your friend
        // This can be achieved using various methods like FCM, SMS, email, etc.
        // Implement the appropriate method based on your requirements
        // For example, sending a notification using Toast:
        Toast.makeText(context, "I woke up!", Toast.LENGTH_SHORT).show();
    }
}

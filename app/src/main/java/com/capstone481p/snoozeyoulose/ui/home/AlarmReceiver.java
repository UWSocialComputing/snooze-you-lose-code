package com.capstone481p.snoozeyoulose.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.capstone481p.snoozeyoulose.R;

//adding comment here to push this as well


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
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

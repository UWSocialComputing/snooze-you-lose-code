package com.capstone481p.snoozeyoulose.ui.home;

import static android.text.format.DateFormat.is24HourFormat;
import static java.text.DateFormat.*;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.capstone481p.snoozeyoulose.DashboardActivity;
import com.capstone481p.snoozeyoulose.MainActivity;
import com.capstone481p.snoozeyoulose.R;
import com.capstone481p.snoozeyoulose.databinding.FragmentHomeBinding;

import com.capstone481p.snoozeyoulose.ui.GlobalVars;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;

// added this for the alarm manager
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;


public class HomeFragment extends Fragment {

    //private FragmentHomeBinding binding;

    private TextView tvTimer1, tvTimer2;
    private int t1Hour, t1Minute, t2Hour, t2Minute;

    private Button awakeButton;
    private Spinner dropDown;
    private String dropDownTxt;
    private Context context;

    // for alarm manager
    private static final int ALARM_REQUEST_CODE = 123;

    private static final int PERMISSION_REQUEST_CODE = 321;

    private TimePicker timePicker;
    private Button setAlarmButtonW;

    private Button setAlarmButtonB;

    // Notification channel ID and name
    private static final String CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_NAME = "Alarm Channel";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d("CONTEXT", "Context for the fragment: " + getContext().getPackageName());

        context = getContext();

        createNotificationChannel(); // Create notification channel for Android 8.0 and above

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // for time manager
        // Initialize views
        //timePicker = view.findViewById(R.id.timePicker);
        setAlarmButtonW = view.findViewById(R.id.setAlarmButtonW);
        setAlarmButtonB = view.findViewById(R.id.setAlarmButtonB);


        // Set click listener for the button
        /** setAlarmButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        setAlarm();
        awakeMessage();
        }

        });**/


        tvTimer1 = view.findViewById(R.id.tv_timer1);
        tvTimer2 = view.findViewById(R.id.tv_timer2);

        setAlarmButtonW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm(tvTimer1);
            }
        });

        setAlarmButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm(tvTimer2);
            }
        });
        tvTimer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvTimer1);
            }
        });

        tvTimer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvTimer2);
            }
        });

        /**
         tvTimer1.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
        getContext(),
        new TimePickerDialog.OnTimeSetListener() {
        @Override public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Initialize hour and minute
        t1Hour = hourOfDay;
        t1Minute = minute;
        //Initialize calendar
        Calendar calendar = Calendar.getInstance();
        //Set hour and  minute
        calendar.set(Calendar.HOUR_OF_DAY, t1Hour);
        calendar.set(Calendar.MINUTE, t1Minute);
        //calendar.set(0, 0, 0, t1Hour, t1Minute);
        //Set selected time on text view
        tvTimer1.setText(android.text.format.DateFormat.format("hh:mm aa", calendar));


        }
        }, 12, 0, false
        );
        //TODO DELETE THIS IT"S A PUSH TEST FOR BASIA


        //Displayed previous selected time
        Log.d("MyApp", "t1Hour: " + t1Hour);
        Log.d("MyApp", "t1Minute: " + t1Minute);
        timePickerDialog.updateTime(t1Hour, t1Minute);
        //Show dialog
        timePickerDialog.show();

        // Set click listener for the button
        setAlarmButtonW.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        // Initialize calendar
        Calendar calendar = Calendar.getInstance();
        // Set hour and minute
        Log.d("MyApp", "t1Hour: " + t1Hour);
        Log.d("MyApp", "t1Minute: " + t1Minute);
        calendar.set(Calendar.HOUR_OF_DAY, t1Hour);
        calendar.set(Calendar.MINUTE, t1Minute);
        //calendar.set(0, 0, 0, t1Hour, t1Minute);

        // Set up the AlarmManager
        AlarmManager alarmManagerW = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        Log.d("MyApp", "t1Hour: " + t1Hour);
        Log.d("MyApp", "t1Minute: " + t1Minute);
        Log.d("AlarmTime", "Calendar time in millis: " + calendar.getTimeInMillis());
        // Set the alarm to trigger at the selected time
        alarmManagerW.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Set Alarm!", Toast.LENGTH_SHORT).show();
        }

        });
        //
        //                if (!tvTimer1.equals(null)) {
        //                    Users users = new Users(tvTimer1, tvTimer2);
        //                }
        }
        });

         tvTimer2.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
        getContext(),
        new TimePickerDialog.OnTimeSetListener() {
        @Override public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Initialize hour and minute
        t2Hour = hourOfDay;
        t2Minute = minute;
        //Initialize calendar
        Calendar calendar2 = Calendar.getInstance();
        //Set hour and  minute
        calendar2.set(0, 0, 0, t2Hour, t2Minute);
        //Set selected time on text view
        tvTimer2.setText(android.text.format.DateFormat.format("hh:mm aa", calendar2));

        // Set click listener for the button
        setAlarmButtonB.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        // Set up the AlarmManager
        AlarmManager alarmManagerB = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent2, PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm to trigger at the selected time
        alarmManagerB.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pendingIntent2);
        Toast.makeText(context, "Set Alarm!", Toast.LENGTH_SHORT).show();
        }

        });
        }
        }, 12, 0, false
        );
        //TODO DELETE THIS IT"S A PUSH TEST FOR BASIA


        //Displayed previous selected time
        timePickerDialog.updateTime(t2Hour, t2Minute);
        //Show dialog
        timePickerDialog.show();
        //
        //                if (!tvTimer1.equals(null)) {
        //                    Users users = new Users(tvTimer1, tvTimer2);
        //                }
        }
        });

         */


        awakeButton = view.findViewById(R.id.awake_button);
        awakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click here
                // You can communicate with the main activity or perform any desired action
                awakeMessage();
            }
        });

        dropDown = view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.accountability_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dropDown.setAdapter(adapter);
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   parent.getItemAtPosition(position);
                                                   FirebaseDatabase database = FirebaseDatabase.getInstance();

                                                   // store the value in Database in "Users" Node
                                                   DatabaseReference ref = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                   dropDownTxt = parent.getItemAtPosition(position).toString();
                                                   Log.d("Firebase", "spinner: " + dropDownTxt); // Log the accountability for debugging
                                                   GlobalVars.accountabilityType = dropDownTxt;
                                                   ref.child("accountability").setValue(parent.getItemAtPosition(position).toString());
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {
                                                   parent.getFirstVisiblePosition();
                                               }
                                           }

        );
    }

    /**
     * for alarm manager
     */
    private void setAlarm(final TextView textView) {
        String selectedTime = textView.getText().toString();
        if (!selectedTime.isEmpty()) {
            // Extract hour and minute from the selected time
            int hour = Integer.parseInt(selectedTime.substring(0, 2));
            int minute = Integer.parseInt(selectedTime.substring(3, 5));

            // Create a calendar instance and set the selected hour and minute
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            // Set up the AlarmManager
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

            // Set the alarm to trigger at the selected time
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Toast.makeText(context, "Alarm Set!", Toast.LENGTH_SHORT).show();

            // Show notification
            showNotification(calendar);
        } else {
            Toast.makeText(context, "Please select a time first.", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     * For timePicker
     */
    private void showTimePickerDialog(final TextView textView) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        t1Hour = hourOfDay;
                        t1Minute = minute;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, t1Hour);
                        calendar.set(Calendar.MINUTE, t1Minute);
                        //calendar.set(0, 0, 0, t1Hour, t1Minute);

                        Log.d("AlarmTime", "Calendar time in millis: " + calendar.getTimeInMillis());
                        textView.setText(android.text.format.DateFormat.format("hh:mm aa", calendar));
                    }
                },
                12, 0, false
        );

        timePickerDialog.updateTime(t1Hour, t1Minute);
        timePickerDialog.show();
    }

    private void showNotification(Calendar calendar) {
        // Create an explicit intent for the activity to be launched when the notification is clicked
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Alarm")
                .setContentText("It's time to wake up!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Play default notification sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        // Set notification color
        builder.setColor(Color.RED);

        Toast.makeText(context, "showNotification() called", Toast.LENGTH_SHORT).show(); // Display a Toast message


                // Show the notification
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                    Toast.makeText(context, "Requesting permission", Toast.LENGTH_SHORT).show();
                } else {
                    notificationManager.notify(ALARM_REQUEST_CODE, builder.build());
                }


        /*
        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            Toast.makeText(context, "in the if", Toast.LENGTH_SHORT).show();
        } else {
            notificationManager.notify(ALARM_REQUEST_CODE, builder.build());
        }

         */
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, proceed with your logic
                setAlarm(tvTimer1);
            } else {
                // Permission is denied, handle accordingly (e.g., show a message, disable functionality)
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void awakeMessage() {
        Toast.makeText(context, "Congrats on waking up! Remember to rate your sleep", Toast.LENGTH_SHORT).show();
    }
}
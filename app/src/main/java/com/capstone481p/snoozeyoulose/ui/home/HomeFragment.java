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
import android.content.DialogInterface;
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

    private TextView tvTimer1, tvTimer2, Timer;
    private int t1Hour, t1Minute, t2Hour, t2Minute;
    private int initialHour, initialMinute;

    //private Button awakeButton;
    private Spinner dropDown;
    private String dropDownTxt;
    private int lastPos;

    private String wakeUpTxt;
    private String bedTxt;

    private Context context;

    // for alarm manager
    private static final int ALARM_REQUEST_CODE = 0;

    private static final int PERMISSION_REQUEST_CODE = 0;

    private TimePicker timePicker;
    private Button setAlarmButtonW;

    private Button setAlarmButtonB;

    // Notification channel ID and name
    private static final String CHANNEL_ID = "alarm_channel";
    private static final String CHANNEL_NAME = "Alarm Channel";

    //for the saving accountability on home
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        context = getContext();


        createNotificationChannel();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // for time manager
        // Initialize views
        setAlarmButtonW = view.findViewById(R.id.setAlarmButtonW);
        setAlarmButtonB = view.findViewById(R.id.setAlarmButtonB);

        tvTimer1 = view.findViewById(R.id.tv_timer1);
        tvTimer2 = view.findViewById(R.id.tv_timer2);

        t1Hour = sharedPreferences.getInt("t1Hour", 12);
        t1Minute = sharedPreferences.getInt("t1Minute", 0);
        t2Hour = sharedPreferences.getInt("t2Hour", 12);
        t2Minute = sharedPreferences.getInt("t2Minute", 0);

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

        // Set the initial time values in the TextViews
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, t1Hour);
        calendar1.set(Calendar.MINUTE, t1Minute);
        tvTimer1.setText(android.text.format.DateFormat.format("hh:mm aa", calendar1));

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, t2Hour);
        calendar2.set(Calendar.MINUTE, t2Minute);
        tvTimer2.setText(android.text.format.DateFormat.format("hh:mm aa", calendar2));

        String wakeTemp = (String) android.text.format.DateFormat.format("hh:mm aa", calendar1);
        String bedTemp = (String) android.text.format.DateFormat.format("hh:mm aa", calendar2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child("wakeupTime").setValue(wakeTemp);
        ref.child("bedTime").setValue(bedTemp);

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


//        awakeButton = view.findViewById(R.id.awake_button);
//        awakeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle button click here
//                // You can communicate with the main activity or perform any desired action
//                awakeMessage();
//            }
//        });



        // Retrieve the last selected position from SharedPreferences
        lastPos = sharedPreferences.getInt("lastPos", 0);

        dropDown = view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.accountability_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dropDown.setAdapter(adapter);

        // Set the spinner selection to the last selected position
        Log.d("Firebase", "curr pos: " + lastPos);
        dropDown.setSelection(lastPos);
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   parent.getItemAtPosition(position);
                                                   FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                   lastPos = position;
                                                   editor.putInt("lastPos", lastPos);
                                                   editor.apply();

                                                   Log.d("Firebase", "new pos: " + lastPos);

                                                   // store the value in Database in "Users" Node
                                                   DatabaseReference ref = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                   dropDownTxt = parent.getItemAtPosition(position).toString();
                                                   Log.d("Firebase", "spinner: " + dropDownTxt); // Log the accountability for debugging
                                                   GlobalVars.accountabilityType = dropDownTxt;
                                                   ref.child("accountability").setValue(parent.getItemAtPosition(position).toString());
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {
                                                   // dropDown.setSelection(lastPos);
                                                   Log.d("Firebase", "nothing selected: " + lastPos);
                                                   dropDown.setSelection(lastPos);
                                               }
                                           }
        );

        if (savedInstanceState != null) {
            lastPos = savedInstanceState.getInt("lastPos", 0); // Restore the last selected position
            Log.d("Firebase", "old pos: " + lastPos);
        }

    }

    /**
     * for alarm manager
     */

    private void setAlarm(final TextView textView) {

        String selectedTime = textView.getText().toString();

        // differentiate between alarm types, true if bedtime, false if wake up
        boolean isBedtimeAlarm = textView.getId() != R.id.tv_timer1;

        if (!selectedTime.isEmpty()) {
            // Extract hour and minute from the selected time
            int hour = Integer.parseInt(selectedTime.substring(0, 2));
            int minute = Integer.parseInt(selectedTime.substring(3, 5));

            // Handle conversion to 24 hr time
            if(selectedTime.contains("PM") && hour != 12) {
                hour += 12;
            } else if (selectedTime.contains("AM") && hour == 12){
                hour = 0;
            }


            Log.d("TIME_DEBUG", "Time: "+hour+":"+minute+", Contains PM: "+selectedTime.contains("PM")
                    +", Contains AM: "+selectedTime.contains("AM"));

            // Create a calendar instance and set the selected hour and minute
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            Log.d("TIME_DEBUG", "Calendar val: "+calendar.getTime());
            Log.d("TIME_DEBUG", "Calendar ms: " +calendar.getTimeInMillis());

            // Set up the AlarmManager
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);

            // Send alarm type boolean with intent
            intent.putExtra("alarm_type",isBedtimeAlarm);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

            // Set the alarm to trigger at the selected time
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Toast.makeText(context, "Alarm Set!", Toast.LENGTH_SHORT).show();

            // Show notification
            // Note: Notification creation is currently being done in alarm receiver, in the future
            // it may be best to place it somewhere else
        } else {
            Toast.makeText(context, "Please select a time first.", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     * For timePicker
     */

    private void showTimePickerDialog(final TextView textView) {

        if (textView == tvTimer1) {
            initialHour = t1Hour;
            initialMinute = t1Minute;
        } else if (textView == tvTimer2) {
            initialHour = t2Hour;
            initialMinute = t2Minute;
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),

                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (textView == tvTimer1) {
                            t1Hour = hourOfDay;
                            t1Minute = minute;
                            editor.putInt("t1Hour", t1Hour);
                            editor.putInt("t1Minute", t1Minute);
                        } else if (textView == tvTimer2) {
                            t2Hour = hourOfDay;
                            t2Minute = minute;
                            editor.putInt("t2Hour", t2Hour);
                            editor.putInt("t2Minute", t2Minute);
                        }
                        editor.apply();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        //calendar.set(0, 0, 0, t1Hour, t1Minute);

                        Log.d("AlarmTime", "Calendar time in millis: " + calendar.getTimeInMillis());
                        textView.setText(android.text.format.DateFormat.format("hh:mm aa", calendar));

                        String tempT1 = String.valueOf(t1Hour);
                        if (t1Hour < 10) {
                            tempT1 = "0" + t1Hour;
                        }
                        if (t1Minute < 10) {
                            tempT1 += ":0" + t1Minute;
                        } else {
                            tempT1 += ":" + t1Minute;
                        }

                        String tempT2 = String.valueOf(t2Hour);
                        if (t2Hour < 10) {
                            tempT2 = "0" + t2Hour;
                        }
                        if (t2Minute < 10) {
                            tempT2 += ":0" + t2Minute;
                        } else {
                            tempT2 += ":" + t2Minute;
                        }
                        wakeUpTxt = tempT1;
                        bedTxt = tempT2;
                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        Log.d("Firebase", "times: " + wakeUpTxt + " & " + bedTxt);

                        // store the value in Database in "Users" Node
                        DatabaseReference ref = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        ref.child("bedTime").setValue(bedTxt);
                        ref.child("wakeupTime").setValue(wakeUpTxt);
                    }
                },
                initialHour, initialMinute, false
        );
        if (textView == tvTimer1) {
            timePickerDialog.updateTime(t1Hour, t1Minute);
        } else if (textView == tvTimer2) {
            timePickerDialog.updateTime(t2Hour, t2Minute);
        }

        timePickerDialog.show();
    }


    private void showNotification(Calendar calendar, TextView textView) {

        // Create an explicit intent for the activity to be launched when the notification is clicked
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            Toast.makeText(context, "Requesting permission", Toast.LENGTH_SHORT).show();
        }

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
        Timer = textView;
        notificationManager.notify(ALARM_REQUEST_CODE, builder.build());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, proceed
                setAlarm(Timer);
            } else {
                // Permission is denied, handle accordingly
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastPos", lastPos); // Save the last selected position
        Log.d("Firebase", "lastpos: " + lastPos);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            lastPos = savedInstanceState.getInt("lastPos", 0);
            if (dropDown != null) {
                dropDown.setSelection(lastPos);
            }
        }
    }

//    public void awakeMessage() {
//        Toast.makeText(context, "Congrats on waking up! Remember to rate your sleep", Toast.LENGTH_SHORT).show();
//    }
}
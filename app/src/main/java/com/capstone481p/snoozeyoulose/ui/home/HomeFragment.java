package com.capstone481p.snoozeyoulose.ui.home;

import android.app.TimePickerDialog;

import java.util.Calendar;
import java.util.Objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstone481p.snoozeyoulose.R;

import com.capstone481p.snoozeyoulose.ui.GlobalVars;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// added this for the alarm manager
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

public class HomeFragment extends Fragment {
    private TextView tvTimer1, tvTimer2;
    private int t1Hour, t1Minute, t2Hour, t2Minute;
    private int initialHour, initialMinute;
    private Spinner dropDown;
    private String dropDownTxt;
    private int lastPos;

    private String wakeUpTxt;
    private String bedTxt;

    private Context context;

    // Code for alarm manager
    private static final int ALARM_REQUEST_CODE = 0;

    private Button setAlarmButtonW;

    private Button setAlarmButtonB;

    // Used for saving accountability between screens
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Initialize timer manager views
        setAlarmButtonW = view.findViewById(R.id.setAlarmButtonW);
        setAlarmButtonB = view.findViewById(R.id.setAlarmButtonB);

        tvTimer1 = view.findViewById(R.id.tv_timer1);
        tvTimer2 = view.findViewById(R.id.tv_timer2);

        t1Hour = sharedPreferences.getInt("t1Hour", 12);
        t1Minute = sharedPreferences.getInt("t1Minute", 0);
        t2Hour = sharedPreferences.getInt("t2Hour", 12);
        t2Minute = sharedPreferences.getInt("t2Minute", 0);

        setAlarmButtonW.setOnClickListener(v -> setAlarm(tvTimer1));

        setAlarmButtonB.setOnClickListener(v -> setAlarm(tvTimer2));

        // Set the initial time values in the TextViews
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, t1Hour);
        calendar1.set(Calendar.MINUTE, t1Minute);
        tvTimer1.setText(android.text.format.DateFormat.format("hh:mm aa", calendar1));
        tvTimer1.setTextColor(Color.WHITE);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, t2Hour);
        calendar2.set(Calendar.MINUTE, t2Minute);
        tvTimer2.setText(android.text.format.DateFormat.format("hh:mm aa", calendar2));
        tvTimer2.setTextColor(Color.WHITE);

        String wakeTemp = (String) android.text.format.DateFormat.format("hh:mm aa", calendar1);
        String bedTemp = (String) android.text.format.DateFormat.format("hh:mm aa", calendar2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child("wakeupTime").setValue(wakeTemp);
        ref.child("bedTime").setValue(bedTemp);

        tvTimer1.setOnClickListener(v -> showTimePickerDialog(tvTimer1));

        tvTimer2.setOnClickListener(v -> showTimePickerDialog(tvTimer2));

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
        dropDown.setSelection(lastPos);
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   parent.getItemAtPosition(position);
                                                   FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                   lastPos = position;
                                                   editor.putInt("lastPos", lastPos);
                                                   editor.apply();

                                                   // store user info in the database
                                                   DatabaseReference ref = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                   dropDownTxt = parent.getItemAtPosition(position).toString();
                                                   GlobalVars.accountabilityType = dropDownTxt;
                                                   ref.child("accountability").setValue(parent.getItemAtPosition(position).toString());
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {
                                                   dropDown.setSelection(lastPos);
                                               }
                                           }
        );

        if (savedInstanceState != null) {
            // Restore the last selected position
            lastPos = savedInstanceState.getInt("lastPos", 0);
        }

    }

    /**
     * Used to set up the alarm for the given text view
     * @param textView the textview for the alarm
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

            // Create a calendar instance and set the selected hour and minute
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);


            // Set up the AlarmManager
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);

            // Send alarm type boolean with intent
            intent.putExtra("alarm_type",isBedtimeAlarm);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

            // Set the alarm to trigger at the selected time
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Toast.makeText(context, "Alarm Set!", Toast.LENGTH_SHORT).show();

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

                (view, hourOfDay, minute) -> {
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

                    // store user info
                    DatabaseReference ref = database.getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    ref.child("bedTime").setValue(bedTxt);
                    ref.child("wakeupTime").setValue(wakeUpTxt);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastPos", lastPos); // Save the last selected position
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
}
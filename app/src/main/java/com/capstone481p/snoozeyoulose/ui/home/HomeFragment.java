package com.capstone481p.snoozeyoulose.ui.home;

import android.app.TimePickerDialog;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.capstone481p.snoozeyoulose.DashboardActivity;
import com.capstone481p.snoozeyoulose.MainActivity;
import com.capstone481p.snoozeyoulose.R;
import com.capstone481p.snoozeyoulose.databinding.FragmentHomeBinding;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private TextView tvTimer1, tvTimer2;
    private int t1Hour, t1Minute, t2Hour, t2Minute;

    private Button awakeButton;
    private FirebaseAnalytics firebaseAnalytics;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);;

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Log.d("CONTEXT", "Context for the fragment: "+getContext().getPackageName());

        context = getContext();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTimer1 = view.findViewById(R.id.tv_timer1);
        tvTimer2 = view.findViewById(R.id.tv_timer2);

        tvTimer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //Initialize hour and minute
                                t1Hour = hourOfDay;
                                t1Minute = minute;
                                //Initialize calendar
                                Calendar calendar = Calendar.getInstance();
                                //Set hour and  minute
                                calendar.set(0, 0, 0, t1Hour, t1Minute);
                                //Set selected time on text view
                                tvTimer1.setText(android.text.format.DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false
                );
                //Displayed previous selected time
                timePickerDialog.updateTime(t1Hour, t1Minute);
                //Show dialog
                timePickerDialog.show();
//
//                if (!tvTimer1.equals(null)) {
//                    Users users = new Users(tvTimer1, tvTimer2);
//                }
            }
        });

        tvTimer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //Initialize hour and minute
                                t2Hour = hourOfDay;
                                t2Minute = minute;
                                //Store hour and minute in string
                                String time = t2Hour + ":" +t2Minute;
                                //Initialize 24 hours time format
                                SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24Hours.parse(time);
                                    //Initialize 12 hours time format
                                    SimpleDateFormat f12Hours = new SimpleDateFormat(
                                            "hh:mm aa"
                                    );
                                    //Set selected time on text view
                                    tvTimer2.setText(f12Hours.format(date));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }, 12, 0, false
                );
                //Set transparent background
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //Displayed previous selected time
                timePickerDialog.updateTime(t2Hour, t2Minute);
                //Show dialog
                timePickerDialog.show();
            }
        });

        awakeButton = view.findViewById(R.id.awake_button);
        awakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click here
                // You can communicate with the main activity or perform any desired action
                awakeMessage();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void awakeMessage() {
        Toast.makeText(context, "Congrats on waking up! Remember to rate your sleep", Toast.LENGTH_SHORT).show();
    }
}
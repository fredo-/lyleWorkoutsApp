package com.example.fredo.lylesworkoutsapp;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onEnterClicked(View view){
        StopwatchesFragment stopwatchesFrag = (StopwatchesFragment)getFragmentManager().findFragmentById(R.id.timersFrag);
        int numIntervals    = Integer.parseInt(((EditText)findViewById(R.id.numberOfIntervals)).getText().toString());
        int intervalDur     = Integer.parseInt(((EditText)findViewById(R.id.intervalDuration)).getText().toString());
        int restDur         = Integer.parseInt(((EditText)findViewById(R.id.restDuration)).getText().toString());
        stopwatchesFrag.setWorkoutDetails(numIntervals, intervalDur, restDur);
    }
}

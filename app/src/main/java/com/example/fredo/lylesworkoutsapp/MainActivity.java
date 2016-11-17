package com.example.fredo.lylesworkoutsapp;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements StopwatchesFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onEnterClicked(View view){
        StopwatchesFragment stopwatchesFrag = new StopwatchesFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        int numIntervals    = Integer.parseInt(((EditText)findViewById(R.id.numberOfIntervals)).getText().toString());
        int intervalDur     = Integer.parseInt(((EditText)findViewById(R.id.intervalDuration)).getText().toString());
        int restDur         = Integer.parseInt(((EditText)findViewById(R.id.restDuration)).getText().toString());
        stopwatchesFrag.setWorkoutDetails(numIntervals, intervalDur, restDur);
        fragmentTransaction.replace(R.id.timersFrag, stopwatchesFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
        stopwatchesFrag.runStopwatches();
    }
}

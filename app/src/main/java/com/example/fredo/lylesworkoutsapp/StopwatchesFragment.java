package com.example.fredo.lylesworkoutsapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StopwatchesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class StopwatchesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FlippingTimer";
    private OnFragmentInteractionListener mListener;
    private int timeElapsed = 0;
    private int interTimeElapsed = 0;
    private int restTimeElapsed = 0;
    private boolean running;
    private boolean intervalIsRunning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_timers, container, false);
        runStopwatches(layout);
        Button startBtn = (Button)layout.findViewById(R.id.startBtn);
        Button pauseBtn = (Button)layout.findViewById(R.id.pauseBtn);
        Button resetBtn = (Button)layout.findViewById(R.id.resetBtn);
        startBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        return layout;
    }

    private void runStopwatches(View layout) {
        final int restDur = 15; //seconds
        final int intervalDur = 5;  //seconds
        final int numIntervals = 10;
        final int totalDuration= (restDur + intervalDur) * numIntervals;
        final TextView numIntervalDisp    = (TextView)layout.findViewById(R.id.intervalsLeftDisplay);
        final TextView intervalDisp       = (TextView)layout.findViewById(R.id.intervalTimerDisplay);
        final TextView restDisp           = (TextView)layout.findViewById(R.id.restTimerDisplay);
        final TextView totalTimerDisp     = (TextView)layout.findViewById(R.id.totalTimerDisplay);

        String initIntervalTimeToDisp   = getTimeToDisplay(getUpdatedTimer(intervalDur, interTimeElapsed));
        String initRestTimeToDisp       = getTimeToDisplay(getUpdatedTimer(restDur, restTimeElapsed));
        intervalDisp.setText(initIntervalTimeToDisp);
        restDisp.setText(initRestTimeToDisp);

        final Handler handler = new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                int intervalsLeft   = getIntervalsLeft(numIntervals, timeElapsed, restDur, intervalDur);
                int intervalTimer   = getUpdatedTimer(intervalDur, interTimeElapsed);
                int restTimer       = getUpdatedTimer(restDur, restTimeElapsed);
                int totalTimer      = getUpdatedTimer(totalDuration, timeElapsed);

                numIntervalDisp.setText(String.format(Locale.US, "%d", intervalsLeft));

                String totalTimeToDisp      = getTimeToDisplay(totalTimer);
                String intervalTimeToDisp   = getTimeToDisplay(intervalTimer);
                String restTimeToDisp       = getTimeToDisplay(restTimer);

                totalTimerDisp.setText(totalTimeToDisp);
                intervalDisp.setText(intervalTimeToDisp);
                restDisp.setText(restTimeToDisp);

                if(running){
                    intervalIsRunning = checkTimersProgress(intervalTimer, restTimer, intervalIsRunning);
                    incrementTimesElapsed(intervalIsRunning);
                    if(timeElapsed==totalDuration)
                        running = false;
                }
                handler.postDelayed(this,1000);
            }
        });
    }

    private void incrementTimesElapsed(boolean runIntervalTimer) {
        if(runIntervalTimer) {
            interTimeElapsed++;
        }else {
            restTimeElapsed++;
        }
        timeElapsed++;
    }

    private boolean checkTimersProgress(int intervalTimer, int restTimer, boolean initialValue) {
        boolean intervalRunning = initialValue;
        if(intervalTimer==0) {
            intervalRunning = false;
            interTimeElapsed++;
        }else if(restTimer==0){
            intervalRunning = true;
            restTimeElapsed++;
        }
        return intervalRunning;
    }

    private int getIntervalsLeft(int numIntervals, int timeElapsed, int restDur, int intervalDur) {
        return numIntervals - timeElapsed/(restDur + intervalDur);
    }

    private int getUpdatedTimer(int intervalDur, int intervalTimeElapsed) {
        return intervalDur - (intervalTimeElapsed % (intervalDur+1));
    }

    private String getTimeToDisplay(int timer) {
        String timeFormat = "%d:%02d:%02d";
        return String.format(
                                Locale.US,
                                timeFormat,
                                (timer / 3600) % 24,
                                (timer / 60) % 60,
                                (timer % 60));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.startBtn:
                onClickStart();
                break;
            case R.id.pauseBtn:
                onClickPause();
                break;
            case R.id.resetBtn:
                onClickReset();
                break;
        }
    }

    private void onClickStart() {
        running = true;
        intervalIsRunning = true;
    }

    private void onClickPause() {
        running = false;
    }

    private void onClickReset() {
        timeElapsed = 0;
        interTimeElapsed = 0;
        restTimeElapsed = 0;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

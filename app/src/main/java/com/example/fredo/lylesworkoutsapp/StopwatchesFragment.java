package com.example.fredo.lylesworkoutsapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

    private OnFragmentInteractionListener mListener;
    private int timeElapsed = 0;
    private boolean running;

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
        int restDur = 180; //seconds
        final int intervalDur = 180;  //seconds
        final int numIntervals = 10;
        final TextView numIntervalDisp    = (TextView)layout.findViewById(R.id.intervalsLeftDisplay);
        final TextView intervalDisp       = (TextView)layout.findViewById(R.id.intervalTimerDisplay);
        TextView restDisp                 = (TextView)layout.findViewById(R.id.restTimerDisplay);
        final TextView totalTimerDisp     = (TextView)layout.findViewById(R.id.totalTimerDisplay);
        final Handler handler = new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                numIntervalDisp.setText(Integer.toString(numIntervals));
                String intervalTimeToDisp = String.format(
                        Locale.US,
                        "%d:%02d:%02d",
                        (intervalDur-timeElapsed)/3600,
                        (intervalDur-timeElapsed)/60,
                        (intervalDur-timeElapsed)%60);
                intervalDisp.setText(intervalTimeToDisp);
                if(running){
                    timeElapsed++;
                }
                handler.postDelayed(this,1000);
            }
        });
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
    }

    private void onClickPause() {
        running = false;
    }

    private void onClickReset() {
        timeElapsed = 0;
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

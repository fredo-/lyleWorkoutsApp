package com.example.fredo.lylesworkoutsapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


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
    private int restDur;
    private int intervalDur;
    private int numIntervals;
    private int totalDuration;
    private boolean running;
    private boolean intervalIsRunning;
    private Handler handler;

    TextView intervalDisp;
    TextView totalTimerDisp;

    private int totalInt;

    private Subscription totalSubscription;
    private Subscription intervalSubscription;
    private Observable<Long> totalObservable;
    private Observable<Long>  intervalObservable;
    private Observable<Long>   rangeObservable;

    public StopwatchesFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_timers, container, false);
//        runStopwatches(layout);
        Button startBtn = (Button)layout.findViewById(R.id.startBtn);
        Button pauseBtn = (Button)layout.findViewById(R.id.pauseBtn);
        Button resetBtn = (Button)layout.findViewById(R.id.resetBtn);
        startBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);

        intervalDisp        = (TextView)layout.findViewById(R.id.intervalTimerDisplay);
        totalTimerDisp      = (TextView)layout.findViewById(R.id.totalTimerDisplay);
        return layout;
    }

    public void runStopwatches() {
        View layout = getView();
//        final int restDur = 15; //seconds
//        final int intervalDur = 5;  //seconds
//        final int numIntervals = 10;
        final TextView numIntervalDisp    = (TextView)layout.findViewById(R.id.intervalsLeftDisplay);
        final TextView restDisp           = (TextView)layout.findViewById(R.id.restTimerDisplay);

        String initIntervalTimeToDisp   = getTimeToDisplay(getUpdatedTimer(intervalDur, interTimeElapsed));
        String initRestTimeToDisp       = getTimeToDisplay(getUpdatedTimer(restDur, restTimeElapsed));
        intervalDisp.setText(initIntervalTimeToDisp);
        restDisp.setText(initRestTimeToDisp);

        /*handler = new Handler();
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
                    if(timeElapsed==totalDuration) {
                        running = false;
                        handler.removeCallbacks(this);
                    }
                }
                handler.postDelayed(this,1000);
            }
        });*/
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
        startInterval();
        startTotalTimer();
    }

    private void onClickPause() {
        running = false;
    }

    private void onClickReset() {
        timeElapsed = 0;
        interTimeElapsed = 0;
        restTimeElapsed = 0;
    }

    public void setWorkoutDetails(int numIntervals, int intervalDur, int restDur) {
        this.numIntervals = numIntervals;
        this.intervalDur = intervalDur;
        this.restDur = restDur;
        this.totalDuration= (restDur + intervalDur) * numIntervals;

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


    public  void startInterval() {
        intervalSubscription =
        Observable.interval(1, TimeUnit.SECONDS)
               .take(intervalDur)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        //call startRest();
                        //onCompleted is only called when you have a .take(limit) means do limit number of emissions
                        intervalSubscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if(running){
                            int intervalTimer = getUpdatedTimer(intervalDur, interTimeElapsed);
                            intervalDisp.setText(getTimeToDisplay(intervalTimer));
                            interTimeElapsed++;
                        }
                    }
                });
    }



    public void startTotalTimer(){
        if(!totalSubscription.isUnsubscribed()){
            return;
        }
        totalSubscription =
        Observable.interval(1, TimeUnit.SECONDS)
                .take(totalDuration)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {

                if(running){
                    int intervalTimer = getUpdatedTimer(totalDuration, timeElapsed);
                    totalTimerDisp.setText(getTimeToDisplay(intervalTimer));
                    timeElapsed++;
                }
            }
        });


    }
}

package com.example.fredo.lylesworkoutsapp;

import android.content.Context;
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

public class StopwatchesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FlippingTimer";
    private int timeElapsed = 0;
    private int interTimeElapsed = 0;
    private int restTimeElapsed = 0;
    private int restDur;
    private int intervalDur;
    private int numIntervals;
    private int totalDuration;
    private boolean timersAreOn;
    private boolean inWorkout;
    private Handler handler;

    TextView intervalDisp;
    TextView totalTimerDisp;

    private int totalInt;

    private Subscription totalSubscription;
    private Subscription intervalSubscription;

    private Observable<Long> totalTimerObservable = Observable
            .interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread());
    private Subscriber<Long> totalSubscriber = new Subscriber<Long>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Long aLong) {
                    Log.d(TAG, "onNext: in total timer observable, timeElapsed: "+timeElapsed);
                    if (timersAreOn) {
                        int totalCountdown = getCountdownTimer(totalDuration, timeElapsed);
                        totalTimerDisp.setText(getTimerDisplay(totalCountdown));
                        timeElapsed++;
                    }
                    if (timeElapsed == totalDuration) {
                        resetTimers();
                    }
                }
            };

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
        final TextView numIntervalDisp    = (TextView)layout.findViewById(R.id.intervalsLeftDisplay);
        final TextView restDisp           = (TextView)layout.findViewById(R.id.restTimerDisplay);

        String initIntervalTimeToDisp   = getTimerDisplay(getCountdownTimer(intervalDur, interTimeElapsed));
        String initRestTimeToDisp       = getTimerDisplay(getCountdownTimer(restDur, restTimeElapsed));
        intervalDisp.setText(initIntervalTimeToDisp);
        restDisp.setText(initRestTimeToDisp);

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

    private int getCountdownTimer(int intervalDur, int intervalTimeElapsed) {
        return intervalDur - (intervalTimeElapsed % (intervalDur+1));
    }

    private String getTimerDisplay(int timer) {
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
                startTimers();
                break;
            case R.id.pauseBtn:
                pauseTimers();
                break;
            case R.id.resetBtn:
                resetTimers();
                break;
        }
    }

    private void startTimers() {
        timersAreOn = true;
        inWorkout = true;
        startWorkoutTimer();
        startTotalTimer();
    }

    private void pauseTimers() {
        timersAreOn = false;
        totalSubscription.unsubscribe();
        intervalSubscription.unsubscribe();
    }

    private void resetTimers() {
        timeElapsed = 0;
        interTimeElapsed = 0;
        restTimeElapsed = 0;
        pauseTimers();
    }

    public void setWorkoutDetails(int numIntervals, int intervalDur, int restDur) {
        this.numIntervals = numIntervals;
        this.intervalDur = intervalDur;
        this.restDur = restDur;
        this.totalDuration= (restDur + intervalDur) * numIntervals;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public  void startWorkoutTimer() {
        intervalSubscription = Observable
                .interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        //call startRest();
                        //onCompleted is only called when you have a .take(limit) means do limit number of emissions
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if(timersAreOn){
                            int workoutCountdown = getCountdownTimer(intervalDur, interTimeElapsed);
                            intervalDisp.setText(getTimerDisplay(workoutCountdown));
                            interTimeElapsed++;
                        }
                        if(interTimeElapsed==intervalDur) {
                            this.unsubscribe();
                            //startRestTimer();
                        }
                    }
                });
    }


    public void startTotalTimer(){
        if(totalSubscription ==  null) {
            totalSubscription = totalTimerObservable
                    .subscribe(totalSubscriber);
        }else{
            totalTimerObservable.retry();
        }
    }
}

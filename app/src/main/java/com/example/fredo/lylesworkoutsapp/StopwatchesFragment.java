package com.example.fredo.lylesworkoutsapp;

import android.content.Context;
import android.os.Bundle;
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
    private int workoutTimeElapsed = 0;
    private int restTimeElapsed = 0;
    int intervalsLeft;
    private int restDur;
    private int workoutDur;
    private int numIntervals;
    private int totalDuration;
    private boolean timersAreOn;
    private boolean inWorkout;

    TextView workoutDisp;
    TextView restDisp;
    TextView totalTimerDisp;
    TextView intervalsLeftDisp;

    private Subscription clockSubscription;

    public StopwatchesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_timers, container, false);
        Button startBtn = (Button) layout.findViewById(R.id.startBtn);
        Button pauseBtn = (Button) layout.findViewById(R.id.pauseBtn);
        Button resetBtn = (Button) layout.findViewById(R.id.resetBtn);
        startBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);

        workoutDisp = (TextView) layout.findViewById(R.id.intervalTimerDisplay);
        restDisp = (TextView) layout.findViewById(R.id.restTimerDisplay);
        totalTimerDisp = (TextView) layout.findViewById(R.id.totalTimerDisplay);
        intervalsLeftDisp = (TextView) layout.findViewById(R.id.intervalsLeftDisplay);

        setDefaultCountdownDisplays();
        startClock();

        return layout;
    }

    public void startClock() {
        clockSubscription = Observable
                .interval(1, TimeUnit.SECONDS)
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
                        Log.d(TAG, "onNext: in total timer observable, timeElapsed: " + timeElapsed);
                        if (timersAreOn) {
                            timeElapsed++;
                            updateCountdownDisplays(inWorkout);
                            inWorkout = stillInWorkoutCheck();
                            intervalsLeftDisp.setText(String.format(Locale.US, "%d", intervalsLeft));
                            int totalCountdown = getCountdownTimer(totalDuration, timeElapsed);
                            totalTimerDisp.setText(getTimerDisplay(totalCountdown));
                        }
                        if (timeElapsed == (totalDuration + 1)) {
                            resetAllTimers();
                        }
                    }
                });
    }

    private int getCountdownTimer(int intervalDur, int intervalTimeElapsed) {
        return intervalDur - (intervalTimeElapsed % (intervalDur + 1));
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
        switch (view.getId()) {
            case R.id.startBtn:
                startTimers();
                break;
            case R.id.pauseBtn:
                pauseTimers();
                break;
            case R.id.resetBtn:
                resetAllTimers();
                break;
        }
    }

    private void startTimers() {
        timersAreOn = true;
        inWorkout = true;
    }

    private void pauseTimers() {
        timersAreOn = false;
    }

    private void resetIntervalTimers() {
        workoutTimeElapsed = 0;
        restTimeElapsed = 0;
        workoutDisp.setText(getTimerDisplay(workoutDur));
        restDisp.setText(getTimerDisplay(restDur));
    }

    private void resetAllTimers() {
        timeElapsed = 0;
        workoutTimeElapsed = 0;
        restTimeElapsed = 0;
        intervalsLeft = numIntervals;
        pauseTimers();
        setDefaultCountdownDisplays();
    }

    public void setWorkoutDetails(int numIntervals, int intervalDur, int restDur) {
        this.numIntervals = numIntervals;
        this.intervalsLeft = numIntervals;
        this.workoutDur = intervalDur;
        this.restDur = restDur;
        this.totalDuration = (restDur + intervalDur) * numIntervals;
        setDefaultCountdownDisplays();
    }


    private void setDefaultCountdownDisplays() {
        intervalsLeftDisp.setText(String.format(Locale.US, "%d", numIntervals));
        workoutDisp.setText(getTimerDisplay(workoutDur));
        restDisp.setText(getTimerDisplay(restDur));
        totalTimerDisp.setText(getTimerDisplay(totalDuration));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clockSubscription.unsubscribe();
    }

    private void updateCountdownDisplays(boolean inWorkout) {
        if (inWorkout) {
            workoutTimeElapsed++;
            int timer = getCountdownTimer(workoutDur, workoutTimeElapsed);
            workoutDisp.setText(getTimerDisplay(timer));
        } else {
            restTimeElapsed++;
            int timer = getCountdownTimer(restDur, restTimeElapsed);
            restDisp.setText(getTimerDisplay(timer));
        }
    }


    private boolean stillInWorkoutCheck() {
        if (restTimeElapsed == restDur || workoutTimeElapsed == workoutDur) {
            inWorkout = !inWorkout;
            if (restTimeElapsed == restDur) {
                intervalsLeft = intervalsLeft - 1;
            }
            resetIntervalTimers();
        }
        return inWorkout;
    }
}

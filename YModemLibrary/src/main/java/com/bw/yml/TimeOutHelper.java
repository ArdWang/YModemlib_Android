package com.bw.yml;

import android.os.Handler;

/**
 * A timer util for counting the time past after we sent a package to the terminal
 */

class TimeOutHelper {

    private ITimeOut listener;

    private final Handler timeoutHandler = new Handler();

    private final Runnable timer = new Runnable() {
        @Override
        public void run() {
            stopTimer();
            if (listener != null) {
                listener.onTimeOut();
            }
        }
    };

    void startTimer(ITimeOut timeoutListener, long delay) {
        listener = timeoutListener;
        timeoutHandler.postDelayed(timer, delay);
    }

    void stopTimer() {
        timeoutHandler.removeCallbacksAndMessages(null);
    }

    void unRegisterListener() {
        listener = null;
    }

    public interface ITimeOut {
        void onTimeOut();
    }

}

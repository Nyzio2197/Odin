package com.axcdevelopment.Odin.Clock;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class InternalClock {

    private static final ArrayList<ClockListener> listeners = new ArrayList<>();

    public static void start() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HHmmss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                for (ClockListener clockListener : listeners) {
                    if (clockListener.check(dateFormat.format(new Date()))) {
                        clockListener.doAction();
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    public static void attachListener(ClockListener listener) {
        listeners.add(listener);
    }



}

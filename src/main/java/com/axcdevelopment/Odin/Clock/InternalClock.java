package com.axcdevelopment.Odin.Clock;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class InternalClock {

    private static final List<ClockListener> listeners = new ArrayList<>();

    public static void start() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HHmmss");
                TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
                dateFormat.setTimeZone(timeZone);
                for (ClockListener clockListener : new ArrayList<>(listeners)) {
                    int offset = 0;
                    // if (timeZone.inDaylightTime(new Date()))
                    //     offset = timeZone.getDSTSavings();
                    if (clockListener.check(dateFormat.format(new Date(new Date().getTime() + offset)))) {
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

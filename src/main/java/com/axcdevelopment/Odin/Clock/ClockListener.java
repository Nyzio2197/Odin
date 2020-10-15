package com.axcdevelopment.Odin.Clock;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public abstract class ClockListener {

    private final String condition;

    public ClockListener(String condition) {
        this.condition = condition;
    }

    protected boolean check(String date) {
        return date.endsWith(condition);
    }

    protected abstract void doAction();

}

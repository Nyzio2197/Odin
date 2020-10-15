package com.axcdevelopment.Odin.Support;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class BotData {

    private static String nextMaintenanceDate;
    private static String nextMaintenanceDuration;

    private static boolean inMaintenance;

    private static boolean initialized;

    public static String getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public static void setNextMaintenanceDate(String nextMaintenanceDate) {
        BotData.nextMaintenanceDate = nextMaintenanceDate;
    }

    public static String getNextMaintenanceDuration() {
        return nextMaintenanceDuration;
    }

    public static void setNextMaintenanceDuration(String nextMaintenanceDuration) {
        BotData.nextMaintenanceDuration = nextMaintenanceDuration;
    }

    public static boolean isInMaintenance() {
        return inMaintenance;
    }

    public static void setInMaintenance(boolean inMaintenance) {
        BotData.inMaintenance = inMaintenance;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initialized) {
        BotData.initialized = initialized;
    }
}

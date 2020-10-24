package com.axcdevelopment.Odin.Support;

import java.util.Scanner;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class BotData {

    private static String nextMaintenanceDate; // format: MM/dd/yyyy
    private static String nextMaintenanceDuration;

    private static String status;

    private static boolean inMaintenance;

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

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        BotData.status = status;
    }

    public static String asString() {
        String data = "";
        data += nextMaintenanceDate + ":::" + nextMaintenanceDuration + "\n";
        data += inMaintenance + "\n";
        data += status + "\n";
        return data;
    }

    public static void fromString(String data) {
        Scanner scanner = new Scanner(data);
        nextMaintenanceDate = data.split(":::")[0];
        nextMaintenanceDuration = data.split(":::")[1];
        scanner.nextLine();
        inMaintenance = Boolean.parseBoolean(scanner.nextLine());
        status = scanner.nextLine();
        scanner.close();
    }
}

package com.axcdevelopment.Odin;

import com.axcdevelopment.Odin.Clock.ClockListener;
import com.axcdevelopment.Odin.Clock.InternalClock;
import com.axcdevelopment.Odin.Discord.Discord;
import com.axcdevelopment.Odin.Dropbox.Dropbox;
import com.axcdevelopment.Odin.Server.Server;
import com.axcdevelopment.Odin.Support.BotData;
import com.axcdevelopment.Odin.Twitter.TwitterConnector;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Alan Xiao (axcdevelopment@gmail.com)
 */

public class Main {

    public static void main(String[] args) throws LoginException, InterruptedException {
        Discord.connect();
        Dropbox.sync();
        // TwitterConnector.connect();
        InternalClock.start();

        attachClockListeners();

        Thread.sleep(10 * 1000);
        Discord.getJda().addEventListener(Discord.MEMBER_LISTENER,
                Discord.MODERATOR_LISTENER,
                Discord.DEVELOPER_LISTENER,
                Discord.GUILD_JOIN_LISTENER);
        Discord.getJda().getPresence().setActivity(Activity.playing(BotData.getStatus()));
        Discord.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
    }

    private static void attachClockListeners() {
        // Daily -00
        InternalClock.attachListener(new ClockListener("000000") {
            @Override
            protected void doAction() {
                for (Server server : Discord.getServers()) {
                    server.sendMessage(Server.DAILY, "Kommandant, it is now 12:00 AM server side.\nDaily missions have reset.");
                }
            }
        });
        // PvP -12
        InternalClock.attachListener(new ClockListener("114500") {
            @Override
            protected void doAction() {
                for (Server server : Discord.getServers()) {
                    server.sendMessage(Server.PVP, "Kommandant, it is now 11:45 AM. PvP refreshes in 15 minutes.");
                }
            }
        });
        // PvP -18
        InternalClock.attachListener(new ClockListener("174500") {
            @Override
            protected void doAction() {
                for (Server server : Discord.getServers()) {
                    server.sendMessage(Server.DAILY, "Kommandant, it is now 5:45 PM. PvP refreshes in 15 minutes.");
                }
            }
        });
        // Night Commissions
        InternalClock.attachListener(new ClockListener("190000") {
            @Override
            protected void doAction() {
                for (Server server : Discord.getServers()) {
                    server.sendMessage(Server.DAILY, "Kommandant, night commissions are here. Please check them out in the Urgent tab.");
                }
            }
        });
        // Daily -23
        InternalClock.attachListener(new ClockListener("230000") {
            @Override
            protected void doAction() {
                for (Server server : Discord.getServers()) {
                    server.sendMessage(Server.DAILY, "Kommandant, it is now 11:00 PM.\nPlease finish your daily missions within the hour.");
                }
            }
        });
        // PvP -00
        InternalClock.attachListener(new ClockListener("234500") {
            @Override
            protected void doAction() {
                for (Server server : Discord.getServers()) {
                    server.sendMessage(Server.DAILY, "Kommandant, it is now 11:45 PM. PvP refreshes in 15 minutes.");
                }
            }
        });

        // Maintenance 12
        if (BotData.getNextMaintenanceDate() != null) {
            SimpleDateFormat yearTime = new SimpleDateFormat("MM/dd/yyyy");
            yearTime.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            Date maintenanceDate = null;
            try {
                if (BotData.getNextMaintenanceDate() != null) {
                    maintenanceDate = yearTime.parse(BotData.getNextMaintenanceDate());
                    System.out.println("next maintenance date: " + BotData.getNextMaintenanceDate());
                    System.out.println("Current date: " + yearTime.format(new Date()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date finalMaintenanceDate = maintenanceDate;
            if (finalMaintenanceDate == null)
                return;
            SimpleDateFormat standard = new SimpleDateFormat("MM/dd/yyyy HHmmss");
            standard.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            // maint -12
            InternalClock.attachListener(new ClockListener(standard.format(new Date(finalMaintenanceDate.getTime() - 12 * 60 * 60 * 1000))) {
                @Override
                protected void doAction() {
                    for (Server server : Discord.getServers()) {
                        server.sendMaintenance12();
                    }
                }
            });
            // maint -3
            InternalClock.attachListener(new ClockListener(standard.format(new Date(finalMaintenanceDate.getTime() - 3 * 60 * 60 * 1000))) {
                @Override
                protected void doAction() {
                    for (Server server : Discord.getServers()) {
                        server.sendMaintenance3();
                    }
                }
            });
            // maint -n
            for (int n = 11; n > 0 && n != 3; n--) {
                int finalN = n;
                InternalClock.attachListener(new ClockListener(standard.format(new Date(finalMaintenanceDate.getTime() - n * 60 * 60 * 1000))) {
                    @Override
                    protected void doAction() {
                        for (Server server : Discord.getServers()) {
                            server.sendMaintenanceN(finalN);
                        }
                    }
                });
            }
            // maint -0
            InternalClock.attachListener(new ClockListener(standard.format(new Date(finalMaintenanceDate.getTime()))) {
                @Override
                protected void doAction() {
                    BotData.setInMaintenance(true);
                    for (Server server : Discord.getServers()) {
                        server.sendMaintenanceStart();
                    }
                }
            });
        }
    }
}

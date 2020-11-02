package com.axcdevelopment.odin.Core;

import com.axcdevelopment.odin.Core.MessageEventListeners.DeveloperMessageEventListener;
import com.axcdevelopment.odin.Core.MessageEventListeners.MemberEventListener;
import com.axcdevelopment.odin.Core.MessageEventListeners.ModeratorEventListener;
import com.axcdevelopment.odin.ExternalAPIs.Dropbox.Dropbox;
import com.axcdevelopment.odin.ExternalAPIs.Twitter.Twitter;
import com.axcdevelopment.odin.Server.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;

import javax.security.auth.login.LoginException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static JDA jda;
    private volatile static ArrayList<Server> serverList;

    public volatile static String nextMaintenanceDate,
            nextMaintenanceTime;

    public volatile static boolean inMaintenance;

    public volatile static ArrayList<Message> lastSentMessages;

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(System.getenv("JDAToken"));
        builder.setActivity(Activity.playing("cycling"));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda = builder.build().awaitReady();
        serverList = new ArrayList<>();
        Dropbox.bootUp();
        Twitter.bootUp();
        TimerTask dailyRemindersTask = new TimerTask() {
            @Override
            public void run() {
                if (inMaintenance) {
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
                TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
                dateFormat.setTimeZone(timeZone);
                Date now = new Date(new Date().getTime() + dateFormat.getTimeZone().getDSTSavings());
                String currentTime = dateFormat.format(now);
                switch (currentTime) {
                    case "000000":
                        sendMessageToChannels("Kommandant, it is now 12:00 AM server side.\nDaily missions have reset.", Server.GENERAL, Server.DAILY);
                        sendMessageToChannels("Exercise has reset", Server.GENERAL, Server.PVP);
                        break;
                    case "120000":
                        sendMessageToChannels("Kommandant, it is now 12:00 PM server side.\nExercise has reset.", Server.GENERAL, Server.PVP);
                        break;
                    case "180000":
                        sendMessageToChannels("Kommandant, it is now 06:00 PM server side.\nExercise has reset.", Server.GENERAL, Server.PVP);
                        break;
                    case "190000":
                        sendMessageToChannels("Kommandant, night commissions are here. Please check them out in the Urgent tab.", Server.GENERAL, Server.NIGHT_COM);
                        break;
                    case "230000":
                        sendMessageToChannels("Kommandant, it is now 11:00 PM server side.\nPlease finish your daily missions within the hour.", Server.GENERAL, Server.DAILY);
                        break;
                }
            }
        };
        Timer dailyTimer = new Timer();
        dailyTimer.schedule(dailyRemindersTask, 0, 1000);
        SimpleDateFormat yearTime = new SimpleDateFormat("MM/dd/yyyy");
        yearTime.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        Date maintenanceDate = null;
        try {
            if (nextMaintenanceDate != null) {
                maintenanceDate = yearTime.parse(nextMaintenanceDate);
                System.out.println("next maintenance date: " + nextMaintenanceDate);
                System.out.println("Current date: " + yearTime.format(new Date()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date finalMaintenanceDate = maintenanceDate;
        TimerTask maintenanceReminderTask = new TimerTask() {
            @Override
            public void run() {
                if (finalMaintenanceDate == null)
                    return;
                SimpleDateFormat standard = new SimpleDateFormat("MM/dd/yyyy HHmmss");
                standard.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                String now = standard.format(new Date(new Date().getTime() + standard.getTimeZone().getDSTSavings()));
                if (now.equals(standard.format(new Date(finalMaintenanceDate.getTime() - 12 * 60 * 60 * 1000)))) {
                    sendMessageToChannels("@everyone Kommandant, be advised :\nAll servers will go into maintenance in 12 hours from now. Please do not forget to bind your account to Twitter/FB/YoStar in order to prevent the loss of your progress.\n\n", Server.ANNOUNCE, Server.MAINTENANCE);
                    sendMessageToChannels("https://i.imgur.com/roy6tih.jpg", Server.ANNOUNCE, Server.MAINTENANCE);
                } else if (now.equals(standard.format(new Date(finalMaintenanceDate.getTime() - 3 * 60 * 60 * 1000)))) {
                    sendMessageToChannels("@here Kommandant, be advised :\nAll servers will go into maintenance in 3 hours.", Server.ANNOUNCE, Server.MAINTENANCE);
                } else if (now.equals(standard.format(new Date(finalMaintenanceDate.getTime() - 60 * 60 * 1000)))) {
                    sendMessageToChannels("Kommandant, there is 1 hour left until maintenance begins.", Server.GENERAL, Server.MAINTENANCE);
                    Thread maintenanceStartThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(59*60*1000);
                                inMaintenance = true;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    maintenanceStartThread.start();
                } else if (now.equals(standard.format(finalMaintenanceDate))) {
                    sendMessageToChannels("Kommandant, maintenance has begun. It will last approximately MAINT hours.".replace("MAINT", nextMaintenanceTime == null ? "unknown" : nextMaintenanceTime), Server.GENERAL, Server.MAINTENANCE);
                } else {
                    for (int tMinus = 11; tMinus > 1; tMinus--) {
                        if (tMinus == 3)
                            continue;
                        if (now.equals(standard.format(new Date(finalMaintenanceDate.getTime() - tMinus * 60 * 60 * 1000)))) {
                            sendMessageToChannels("Kommandant, there are HOURS hours left until maintenance begins.".replace("HOURS", "" + tMinus), Server.GENERAL, Server.MAINTENANCE);
                            return;
                        }
                    }
                }
            }
        };
        Timer maintTimer = new Timer();
        maintTimer.schedule(maintenanceReminderTask, 0, 1000);

        Thread.sleep(15 * 1000);
        System.out.println("Attaching Listeners");
        jda.addEventListener(new MemberEventListener(),
                new ModeratorEventListener(),
                new DeveloperMessageEventListener());
        jda.getPresence().setActivity(Activity.playing("o.help"));
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
    }

    public static ArrayList<Server> getServerList() {
        return serverList;
    }

    public static JDA getJda() {
        return jda;
    }

    public static void sendMessageToChannels(String message, String channel, String category) {
        lastSentMessages = new ArrayList<>();
        ArrayList<Server> deadServers = new ArrayList<>();
        switch (channel) {
            case Server.GENERAL:
                for (Server server : serverList) {
                    if (Main.getJda().getGuildById(server.getGuildId()) == null)
                        deadServers.add(server);
                    server.sendMessageToGeneral(message, category);
                }
                break;
            case Server.ANNOUNCE:
                for (Server server : serverList) {
                    if (Main.getJda().getGuildById(server.getGuildId()) == null)
                        deadServers.add(server);
                    server.sendMessageToAnnouncement(message);
                }
                break;
            case Server.TWITTER:
                for (Server server : serverList) {
                    if (Main.getJda().getGuildById(server.getGuildId()) == null)
                        deadServers.add(server);
                    server.sendMessageToTwitterFeed(message);
                }
                break;
        }
        for (Server server : deadServers) {
            Dropbox.deleteServer(server);
            Main.getServerList().remove(server);
        }
    }



}

package Core;

import Core.MessageEventListeners.DeveloperMessageEventListener;
import Core.MessageEventListeners.MemberEventListener;
import Core.MessageEventListeners.ModeratorEventListener;
import ExternalAPIs.Dropbox.Dropbox;
import ExternalAPIs.Twitter.Twitter;
import Server.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static JDA jda;
    private volatile static ArrayList<Server> serverList;

    public volatile static String nextMaintenanceDate,
            nextMaintenanceTime;

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(System.getenv("JDAToken"));
        builder.setActivity(Activity.playing("o.help"));
        builder.addEventListeners(new MemberEventListener(),
                new ModeratorEventListener(),
                new DeveloperMessageEventListener());
        jda = builder.build().awaitReady();
        serverList = new ArrayList<>();

        Thread dropboxThread = new Thread(Dropbox::bootUp);
        dropboxThread.start();
        Thread twitterThread = new Thread(Twitter::bootUp);
        twitterThread.start();

        TimerTask dailyRemindersTask = new TimerTask() {
            @Override
            public void run() {
                String currentTime = new SimpleDateFormat("HHmmss").format(new Date());
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
        new Timer().schedule(dailyRemindersTask, 0, 1000);
        SimpleDateFormat yearTime = new SimpleDateFormat("MM/dd/yyyy");
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
                SimpleDateFormat standard = new SimpleDateFormat("dd/MM/yyyy HHmmss");
                String now = standard.format(new Date());
                if (now.equals(standard.format(new Date(finalMaintenanceDate.getTime() - 12 * 60 * 60 * 1000)))) {
                    sendMessageToChannels("@everyone Kommandant, be advised :\nAll servers will go into maintenance in 12 hours from now. Please do not forget to bind your account to Twitter/FB/YoStar in order to prevent the loss of your progress.", Server.ANNOUNCE, Server.MAINTENANCE);
                } else if (now.equals(standard.format(new Date(finalMaintenanceDate.getTime() - 3 * 60 * 60 * 1000)))) {
                    sendMessageToChannels("@here Kommandant, be advised :\nAll servers will go into maintenance in 3 hours.", Server.ANNOUNCE, Server.MAINTENANCE);
                } else if (now.equals(standard.format(new Date(finalMaintenanceDate.getTime() - 60 * 60 * 1000)))) {
                    sendMessageToChannels("Kommandant, there is 1 hour left until maintenance begins.", Server.GENERAL, Server.MAINTENANCE);
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
        new Timer().schedule(maintenanceReminderTask, 0, 1000);
    }

    public static ArrayList<Server> getServerList() {
        return serverList;
    }

    public static JDA getJda() {
        return jda;
    }

    public static void sendMessageToChannels(String message, String channel, String category) {
        switch (channel) {
            case Server.GENERAL:
                for (Server server : serverList) {
                    server.sendMessageToGeneral(message, category);
                }
                break;
            case Server.ANNOUNCE:
                for (Server server : serverList) {
                    server.sendMessageToAnnouncement(message);
                }
                break;
            case Server.TWITTER:
                for (Server server : serverList) {
                    server.sendMessageToTwitterFeed(message);
                }
                break;
        }
    }



}

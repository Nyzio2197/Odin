package Core.MessageEventListeners;

import Core.Main;
import ExternalAPIs.Dropbox.Dropbox;
import Server.Server;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DeveloperMessageEventListener extends OdinMessageEventListener{

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        super.onEvent(event);
        if (member == null)
            return;
        if (user.isBot() || user.isFake())
            return;
        if (command == null)
            return;
        if (server == null)
            return;
        if (messageReceivedEvent == null)
            return;
        if (!textChannel.getType().equals(ChannelType.TEXT))
            return;

        List<TextChannel> mentionedChannels = messageReceivedEvent.getMessage().getMentionedChannels();
        if (command.startsWith("setplaying ")) {
            Main.getJda().getPresence().setActivity(Activity.playing(command.substring(command.indexOf(" ") + 1)));
        } else if (command.startsWith("mainttime ")) {
            Main.nextMaintenanceTime = command.substring(command.indexOf(" ") + 1);
            System.out.println("mainttime set to " + Main.nextMaintenanceDate);
            Dropbox.syncMaintenance();
        } else if (command.startsWith("maintdate ")) {
            Main.nextMaintenanceDate = command.substring(command.indexOf(" ") + 1);
            System.out.println("maintdate set to " + Main.nextMaintenanceDate);
            Dropbox.syncMaintenance();
        }
        else if (command.startsWith("cycle ") && mentionedChannels.size() == 1) {
            TimerTask simpleCycle = new TimerTask() {
                @Override
                public void run() {
                    String currentTime = new SimpleDateFormat("HHmmss").format(new Date());
                    if (currentTime.endsWith("0") ||
                            currentTime.endsWith("5"))
                        mentionedChannels.get(0).sendMessage("Current time: " + currentTime).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
                }
            };
            Timer timer = new Timer();
            timer.schedule(simpleCycle, 0, 1000);
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timer.cancel();
        }
        else if (command.startsWith("setstatus")) {
            if (command.contains("dnd"))
                Main.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            else if (command.contains("invis"))
                Main.getJda().getPresence().setStatus(OnlineStatus.INVISIBLE);
            else if (command.contains("on"))
                Main.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
            else if (command.contains("off"))
                Main.getJda().getPresence().setStatus(OnlineStatus.OFFLINE);
        }
        else if (command.startsWith("list")) {
            for (Server server : Main.getServerList()) {
                textChannel.sendMessage(server.getServerName()  + " " + server.getGuildId() + ":\n" + server.getConfigs()).queue();
            }
        }
        else if (command.startsWith("getserver ")) {
            String serverID = command.substring(command.indexOf(" ") + 1);
            Server server = getServer(Main.getJda().getGuildById(serverID));
            textChannel.sendMessage(server.getConfigs()).queue();
        }
        else if (command.equals("delete")) {
            for (Message message : Main.lastSentMessages) {
                message.delete().queue();
            }
        }
    }
}

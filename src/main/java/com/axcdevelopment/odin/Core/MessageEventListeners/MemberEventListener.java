package com.axcdevelopment.odin.Core.MessageEventListeners;

import com.axcdevelopment.odin.Core.Main;
import com.axcdevelopment.odin.Server.Server;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MemberEventListener extends OdinMessageEventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        super.onEvent(event);
        if (member == null)
            return;
        if (user.isBot() || user.isFake())
            return;
        if (messageReceivedEvent == null)
            return;
        if (!textChannel.getType().equals(ChannelType.TEXT))
            return;
        if (mentionedUsers.size() == 1 && mentionedUsers.get(0).equals(Main.getJda().getSelfUser())) {
            if (toggleHashMap.get(Server.PING) && !isModerator(member)) {
                String[] possibleReplies = new String[]{"I am not even disappointed, I simply wonder how someone as lousy as you became a commander in the first place.",
                        "You lot already have no avenues to escape!",
                        "Flee while you still can.",
                        "You have missions. Leaving them unfinished would be a foolish choice.",
                        "As time passes, you learn how futile your concerns are.",
                        "Do not waste my time \"Commander\".",
                        "I do not talk to insignificant people.",
                        "I don't even know who you are.",
                        "Are you sure you're a fully licensed commander?",
                        "https://cdn.discordapp.com/emojis/730498676190871675.png?v=1"};
                String pingReplyMessage = user.getAsMention() + " " + possibleReplies[(int) (Math.random() * possibleReplies.length)];
                textChannel.sendMessage(pingReplyMessage).queue();
                return;
            }
        }
        if (command == null)
            return;
        if (server == null)
            return;
        if (command.equals("help")) {
            String helpMessage = "Basic Commands: \n" +
                    "> o.time *EN/JP/CN* **:** EN/JP/CN server time.\n" +
                    "> o.invite **:** invite code for this bot.";
            if (isModerator(member)) {
                helpMessage += "\n" +
                        "> o.configs **:** displays the current configurations\n" +
                        "> o.ping *#channel* **:** send a temporary message in *#channel*. Useful to check for proper permissions.\n" +
                        "Channel Commands:\n" +
                        "> o.channel *group* **:** lists the current channels in *group*.\n" +
                        "> o.channel *group* *#channel* **:** add new channels to the *group* channels, and remove old ones.\n" +
                        "> *group* can be \"general\", \"announce\", \"twitter\"\n" +
                        ">      Day and PvP resets occur in general, along with the majority of maintenance countdown\n" +
                        ">      Maint t-12 and t-3 occur in announcement channels\n" +
                        ">      Twitter Feed from @AzurLane_EN occur in twitter channels\n" +
                        "Reminder Toggles\n" +
                        "> o.pvp *on/off* **:** toggle the PvP reset reminder.\n" +
                        "> o.daily *on/off* **:** toggle daily reset reminder.\n" +
                        "> o.maint *on/off* **:** toggle maintenance reminder.\n" +
                        "> o.nightcom *on/off* **:** toggle night commission reminder.\n" +
                        "> o.twitter *on/off* **:** toggles twitter status updates\n" +
                        "> o.ping *on/off* **:** toggles ping capability of users (people with admin or manage message perms are immune to this)";
            }
            textChannel.sendMessage(helpMessage).queue();
        }
        else if (command.equals("invite")) {
            textChannel.sendMessage("https://discord.gg/SGtY8am").queue();
        }
        else if (command.startsWith("time")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
            Date now = new Date();
            String timeMessage = "Current REGION server time HHHH";
            if (command.toLowerCase().contains("jp")) {
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                if (simpleDateFormat.getTimeZone().inDaylightTime(now)) {
                    now =  new Date(now.getTime() - 60 * 60 * 1000);
                }
                String currentTime = simpleDateFormat.format(now);
                textChannel.sendMessage(timeMessage.replace("REGION", "JP")
                        .replace("HHHH", simpleDateFormat.format(currentTime))).queue();
            } else if (command.toLowerCase().contains("cn")) {
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Guangzhou"));
                if (simpleDateFormat.getTimeZone().inDaylightTime(now)) {
                    now =  new Date(now.getTime() - 60 * 60 * 1000);
                }
                String currentTime = simpleDateFormat.format(now);
                textChannel.sendMessage(timeMessage.replace("REGION", "CN")
                        .replace("HHHH", simpleDateFormat.format(currentTime))).queue();
            } else {
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                if (simpleDateFormat.getTimeZone().inDaylightTime(now)) {
                    now =  new Date(now.getTime() - 60 * 60 * 1000);
                }
                String currentTime = simpleDateFormat.format(now);
                textChannel.sendMessage(timeMessage.replace("REGION", "EN")
                        .replace("HHHH", simpleDateFormat.format(currentTime))).queue();
            }
        }
    }

}

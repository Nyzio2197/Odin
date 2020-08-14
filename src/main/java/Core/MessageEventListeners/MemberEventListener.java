package Core.MessageEventListeners;

import net.dv8tion.jda.api.MessageBuilder;
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
        if (command == null)
            return;
        if (messageReceivedEvent == null)
            return;
        if (!textChannel.getType().equals(ChannelType.TEXT))
            return;
        if (command.equals("help")) {
            String helpMessage = "Basic Commands: \n" +
                    "> o.time *EN/JP* **:** EN/JP server time.\n" +
                    "> o.invite **:** invite code for this bot.";
            if (isModerator(member)) {
                helpMessage += "\n" +
                        "> o.configs **:** displays the current configurations\n" +
                        "> o.ping *#channel* **:** send a temporary message in *#channel*. Useful to check for proper permissions.\n" +
                        "Channel Commands:\n" +
                        "> o.channel *group* **:** lists the current channels in *group*.\n" +
                        "> o.channel *group* *#channel* **:** add channels to the *group* channels.\n" +
                        "> o.delchannel *group* *#channel* **:** remove *#channel* from list of general channels.\n" +
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
            Message inviteMessage = new MessageBuilder().append("https://bit.ly/2AMrKEQ").build();
            inviteMessage.suppressEmbeds(true);
            textChannel.sendMessage(inviteMessage).queue();
        }
        else if (command.startsWith("time")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm aa");
            Date currentTime = new Date();
            String timeMessage = "Current REGION server time HHHH";
            if (command.toLowerCase().contains("jp")) {
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
                textChannel.sendMessage(timeMessage.replace("REGION", "JP")
                        .replace("HHHH", simpleDateFormat.format(currentTime))).queue();
            } else if (command.toLowerCase().contains("cn")) {
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));
                textChannel.sendMessage(timeMessage.replace("REGION", "CN")
                        .replace("HHHH", simpleDateFormat.format(currentTime))).queue();
            } else {
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                textChannel.sendMessage(timeMessage.replace("REGION", "EN")
                        .replace("HHHH", simpleDateFormat.format(currentTime))).queue();
            }
        }
    }

}

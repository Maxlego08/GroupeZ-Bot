package fr.maxlego08.zsupport.listener;

import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class SuggestListener extends ListenerAdapter implements Constant {


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        Member member = event.getMember();
        Guild guild = null;
        try {
            guild = event.getGuild();
        } catch (Exception e) {
        }

        if (guild == null)
            return;

        if (message.getChannel().getIdLong() != SUGGEST_CHANNEL) {
            return;
        }

        assert member != null;

        if (member.getUser().isBot()) {
            return;
        }

        final Message complete = message.getChannel().sendMessage(new EmbedBuilder()
                .setAuthor(message.getAuthor().getAsTag() + " suggest's", message.getAuthor().getDefaultAvatarUrl())
                .setColor(Color.getHSBColor(5, 255, 5))
                .setDescription(message.getContentDisplay())
                .setFooter("2021 - " + guild.getName(), guild.getIconUrl())
                .build()).complete();

        complete.addReaction("✅").complete();
        complete.addReaction("❌").complete();


    }
}

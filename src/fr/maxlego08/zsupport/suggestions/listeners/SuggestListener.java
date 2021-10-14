package fr.maxlego08.zsupport.suggestions.listeners;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.suggestions.entity.Suggestion;
import fr.maxlego08.zsupport.suggestions.SuggestionManager;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.EmbedBuilder;
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
        Guild guild = event.getGuild();

        if (message.getChannel().getIdLong() != SUGGEST_CHANNEL) {
            return;
        }

        assert member != null;

        if (member.getUser().isBot()) {
            return;
        }

        message.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor(message.getAuthor().getAsTag() + " suggest's", message.getAuthor().getDefaultAvatarUrl())
                        .setColor(Color.getHSBColor(5, 255, 5))
                        .setDescription(message.getContentDisplay())
                        .setFooter("2021 - " + guild.getName(), guild.getIconUrl()).build())

                .queue(discordMessage -> discordMessage.addReaction("U+2705")
                        .queue(e -> discordMessage.addReaction("U+274C")
                                .queue(e2 -> message.delete().queue())));

        SuggestionManager.getSuggestions().add(new Suggestion(member.getUser().getId(), message.getContentDisplay(), message.getId()));
        ZSupport.instance.save();
    }
}

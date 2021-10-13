package fr.maxlego08.zsupport.suggestions.entity;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.suggestions.SuggestionManager;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import java.awt.*;
import java.util.Optional;

public class ValidChoice implements Choice, Constant {
    @Override
    public String getId() {
        return "valide_suggest";
    }

    @Override
    public Emoji getEmoji() {
        return Emoji.fromUnicode("U+2705");
    }

    @Override
    public ButtonStyle getButtonStyle() {
        return ButtonStyle.SUCCESS;
    }

    @Override
    public void run(Interaction interaction, Suggestion suggestion) {
        final Member member = interaction.getMember();

        assert member != null;
        Optional<Role> optional = member.getRoles().stream().filter(role -> role.getIdLong() == ADMIN_ROLE).findFirst();

        if (!optional.isPresent()) {
            interaction.reply("Vous n'avez pas la permission pour faire ceci.").queue();
            return;
        }


        TextChannel channel = ZSupport.instance.getJda().getTextChannelById(SUGGEST_TREATED_CHANNEL);

        if (channel == null) {
            interaction.reply("je n'ai pas trouvé le channel pour les suggestions traitées").setEphemeral(true).queue();
            return;
        }

        User suggestUser = ZSupport.instance.getJda().getUserById(suggestion.getAuthorId());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor("Suggestion de " + (suggestUser != null ? suggestUser.getName() : suggestion.getAuthorId()))
                .setDescription(suggestion.getSuggestion())
                .setColor(Color.GREEN)
                .setFooter("GroupeZ");

        channel.sendMessageEmbeds(embedBuilder.build())
                .queue(message -> interaction.reply("La suggestion a été traitée").setEphemeral(true).queue());


        SuggestionManager.getSuggestions().remove(suggestion);
        ZSupport.instance.save();
    }
}

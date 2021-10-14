package fr.maxlego08.zsupport.suggestions.commands;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.suggestions.entity.Suggestion;
import fr.maxlego08.zsupport.suggestions.SuggestionManager;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Objects;

public class SuggestionTraitmentCommand extends VCommand implements Constant {

    private final SuggestionManager suggestionManager = SuggestionManager.getInstance();

    public SuggestionTraitmentCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    protected CommandType perform(ZSupport main) {

        Suggestion suggestion = this.suggestionManager.getByMessageId(this.argAsString(1));

        if (suggestion == null) {
            sender.sendMessage("Aucune suggestion avec cet id n'a été trouvé.");
            return CommandType.EXCEPTION_ERROR;
        }

        Message message = Objects.requireNonNull(ZSupport.instance.getJda().getTextChannelById(SUGGEST_CHANNEL)).getHistory().getMessageById(suggestion.getMessageId());


        if (message == null) {
            sender.sendMessage("Aucun message n'a été trouvé avec cet id !");
            return CommandType.EXCEPTION_ERROR;
        }


        message.delete().queue(unused -> {

            String arg = argAsString(2);

            if (arg != null && arg.equalsIgnoreCase("delete")) {
                SuggestionManager.getSuggestions().remove(suggestion);
                ZSupport.instance.save();
                return;
            }

            TextChannel textChannel = ZSupport.instance.getJda().getTextChannelById(SUGGEST_UNTREATED_CHANNEL);

            if (textChannel == null) {
                sender.sendMessage("Je n'arrive pas à trouver le channel prévu à cet effet.");
                return;
            }

            textChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor("Suggestion entrain d'être réalisée.")
                    .setDescription(suggestion.getSuggestion())
                    .setFooter(suggestion.getUuid().toString())
                    .build()
            ).queue(suggestionMessage -> {
                ActionRow of = ActionRow.of();

                this.suggestionManager.getChoices().forEach(choice -> of.getButtons().add(Button.of(choice.getButtonStyle(), choice.getId(), choice.getEmoji())));
                suggestionMessage.getActionRows().add(of);
            });
        });

        return CommandType.SUCCESS;
    }
}

package fr.maxlego08.zsupport.suggestions.listeners;

import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import fr.maxlego08.zsupport.suggestions.SuggestionManager;
import fr.maxlego08.zsupport.suggestions.entity.Choice;
import fr.maxlego08.zsupport.suggestions.entity.Suggestion;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.entities.AbstractChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.components.Component;

public class SuggestInteraction extends ListenerAdapter implements Constant {

    private final SuggestionManager suggestionManager = SuggestionManager.getInstance();

    @Override
    public void onGenericComponentInteractionCreate(@NotNull GenericComponentInteractionCreateEvent event) {
        final InteractionType interactionType = event.getType();
        final Interaction interaction = event.getInteraction();
        final AbstractChannel channel = interaction.getChannel();

        if (interactionType != InteractionType.COMPONENT) {
            return;
        }

        final Component.Type componentType = event.getComponentType();

        if (componentType != Component.Type.BUTTON) {
            return;
        }

        assert channel != null;
        if (channel.getIdLong() != SUGGEST_UNTREATED_CHANNEL) {
            return;
        }

        final Message message = event.getMessage();

        Optional<MessageEmbed> optionalMessageEmbed = message.getEmbeds().stream().findFirst();

        if (!optionalMessageEmbed.isPresent()) {
            return;
        }

        Suggestion suggestion = this.suggestionManager.getByUUID(Objects.requireNonNull(optionalMessageEmbed.get().getFooter()).getText());

        if (suggestion == null) {
            message.delete().queue();
            return;
        }

        Optional<Choice> optional = this.suggestionManager.getChoices().stream().filter(choice -> choice.getId().equals(event.getComponentId())).findFirst();

        optional.ifPresent(choice -> choice.run(interaction, suggestion));
    }


}

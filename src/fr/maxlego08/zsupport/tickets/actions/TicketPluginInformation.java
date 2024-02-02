package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.tickets.TicketStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.util.Objects;

public class TicketPluginInformation extends TicketAction {
    @Override
    public void process(Interaction interaction) {

        EmbedBuilder builder = createEmbed();
        setDescription(builder, "Please fill in the form to continue with the ticket.");

        Button openForm = new ButtonImpl(BUTTON_OPEN_MODAL_PLUGIN, "Open the form", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("U+1F44B"));

        this.textChannel.sendMessageEmbeds(builder.build()).setActionRow(openForm, createCloseButton()).queue();

        if (interaction != null && !interaction.isAcknowledged() && interaction instanceof ComponentInteraction componentInteraction) {
            componentInteraction.getMessage().delete().queue();
        }
    }

    private void openModal() {
        TextInput pluginVersion = TextInput.create("pluginVersion", "Version du Plugin", TextInputStyle.SHORT)
                .setPlaceholder("Entrez la version de votre plugin ici")
                .setMinLength(1)
                .setMaxLength(50)
                .build();

        TextInput serverVersion = TextInput.create("serverVersion", "Version du Serveur", TextInputStyle.SHORT)
                .setPlaceholder("Entrez la version de votre serveur ici")
                .setMinLength(1)
                .setMaxLength(50)
                .build();

        TextInput problemDescription = TextInput.create("description", "Description du Problème", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Décrivez le problème que vous rencontrez")
                .setMinLength(10)
                .setMaxLength(1000)
                .build();

        Modal modal = Modal.create(MODAL_PLUGIN_INFORMATIONS, "Informations")
                .addComponents(ActionRow.of(pluginVersion), ActionRow.of(serverVersion), ActionRow.of(problemDescription))
                .build();

        replyModal(modal);
    }

    @Override
    public void onButton(ButtonInteractionEvent event) {
        if (Objects.equals(event.getButton().getId(), BUTTON_OPEN_MODAL_PLUGIN)) {
            if (isAuthor()) openModal();
            else event.reply(":x: Only the ticket author to access the modal.").setEphemeral(true).queue();
        }
    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {

    }

    @Override
    public void onModal(ModalInteractionEvent event) {

        if (event.getModalId().equals(MODAL_PLUGIN_INFORMATIONS)) {

            String pluginVersion = event.getValue("pluginVersion").getAsString();
            String serverVersion = event.getValue("serverVersion").getAsString();
            String description = event.getValue("description").getAsString();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("GroupeZ - Support");
            setEmbedFooter(guild, builder, new Color(26, 237, 148));
            setDescription(builder,
                    "**Plugin Version**:",
                    "```" + pluginVersion + "```",
                    "**Server Version**:",
                    "```" + serverVersion + "```",
                    "**Description**:",
                    "```" + description + "```",
                    "",
                    "You can send additional information, logs, configurations, or other information to help your problem.",
                    "```ansi\n" +
                            "\u001B[2;31m\u001B[1;31mIn order for the support response to be effective, please provide all the requested information.\u001B[0m\u001B[2;31m\u001B[0m\n" +
                            "```"
            );

            this.textChannel.sendMessageEmbeds(builder.build()).queue(message -> processNextAction(TicketStatus.OPEN));

            Message message = event.getMessage();
            if (message != null) message.delete().queue();
        }
    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.PLUGIN_INFORMATION;
    }

    @Override
    public void onMessage(MessageReceivedEvent event) {

    }
}

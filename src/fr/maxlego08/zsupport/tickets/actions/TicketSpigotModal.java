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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TicketSpigotModal extends TicketAction {

    @Override
    public void process(Interaction interaction) {

        EmbedBuilder builder = createEmbed();
        setDescription(builder, "Please provide your pseudo spigot in the form.");

        Button openForm = new ButtonImpl(BUTTON_OPEN_MODAL_SPIGOT, "Open the form", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("U+1F44B"));

        if (interaction != null && !interaction.isAcknowledged() && interaction instanceof ComponentInteraction componentInteraction) {
            componentInteraction.editMessageEmbeds(builder.build()).setActionRow(openForm, createCloseButton()).queue();
        }
    }

    private void openModal() {
        TextInput pluginVersion = TextInput.create("spigotName", "Spigot Name", TextInputStyle.SHORT)
                .setPlaceholder("Enter your spigot username")
                .setMinLength(3)
                .setMaxLength(32)
                .build();

        Modal modal = Modal.create(MODAL_SPIGOT_INFORMATIONS, "Informations")
                .addComponents(ActionRow.of(pluginVersion))
                .build();

        replyModal(modal);
    }

    @Override
    public void onButton(ButtonInteractionEvent event) {
        if (Objects.equals(event.getButton().getId(), BUTTON_OPEN_MODAL_SPIGOT)) {
            if (isAuthor()) openModal();
            else event.reply(":x: Only the ticket author to access the modal.").setEphemeral(true).queue();
        }
    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {

    }

    @Override
    public void onModal(ModalInteractionEvent event) {

        if (!event.getModalId().equals(MODAL_SPIGOT_INFORMATIONS)) return;

        String spigotName = event.getValue("spigotName").getAsString();
        EmbedBuilder builder = createEmbed();
        setDescription(builder,
                "**Spigot Name**:",
                "```" + spigotName + "```",
                "",
                "```ansi\n" +
                        "\u001B[2;31m\u001B[1;31mNow please indicate the plugins you want to get on spigot.\u001B[0m\u001B[2;31m\u001B[0m\n" +
                        "```"
        );

        this.textChannel.sendMessageEmbeds(builder.build()).setActionRow(createCloseButton()).queue(message -> processNextAction(TicketStatus.SPIGOT_ACCESS));

        event.reply("Now please indicate the plugins you want to get on spigot.").setEphemeral(true).queue(response -> {
            Message message = event.getMessage();
            if (message != null) message.delete().queueAfter(3, TimeUnit.SECONDS);
        });

    }

    @Override
    public void onMessage(MessageReceivedEvent event) {

    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.SPIGOT_ACCESS_MODAL;
    }
}

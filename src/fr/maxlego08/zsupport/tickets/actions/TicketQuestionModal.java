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

public class TicketQuestionModal extends TicketAction {

    @Override
    public void process(Interaction interaction) {

        EmbedBuilder builder = createEmbed();
        setDescription(builder, "Please fill out the form to ask your question.");

        Button openForm = new ButtonImpl(BUTTON_OPEN_MODAL_QUESTION, "Open the form", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("U+1F44B"));

        if (interaction != null && !interaction.isAcknowledged() && interaction instanceof ComponentInteraction componentInteraction) {
            componentInteraction.editMessageEmbeds(builder.build()).setActionRow(openForm, createCloseButton()).queue();
        }
    }

    private void openModal() {

        TextInput questionTitle = TextInput.create("questionTitle", "Question title", TextInputStyle.SHORT)
                .setPlaceholder("Enter a title to your question")
                .setMinLength(10)
                .setMaxLength(50)
                .build();

        TextInput question = TextInput.create("question", "Question", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Your question")
                .setMinLength(10)
                .setMaxLength(4000)
                .build();

        Modal modal = Modal.create(MODAL_QUESTION_INFORMATIONS, "Informations")
                .addComponents(ActionRow.of(questionTitle), ActionRow.of(question))
                .build();

        replyModal(modal);
    }

    @Override
    public void onButton(ButtonInteractionEvent event) {
        if (Objects.equals(event.getButton().getId(), BUTTON_OPEN_MODAL_QUESTION)) {
            if (isAuthor()) openModal();
            else event.reply(":x: Only the ticket author to access the modal.").setEphemeral(true).queue();
        }
    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {

    }

    @Override
    public void onModal(ModalInteractionEvent event) {

        if (!event.getModalId().equals(MODAL_QUESTION_INFORMATIONS)) return;

        String questionTitle = event.getValue("questionTitle").getAsString();
        String question = event.getValue("question").getAsString();

        EmbedBuilder builder = createEmbed();
        setDescription(builder,
                "**Question title**:",
                "```" + questionTitle + "```",
                "**Question**:",
                "```" + question + "```",
                "",
                "```ansi\n" +
                        "\u001B[2;31m\u001B[1;31mIf you have any further questions, you can add them now.\u001B[0m\u001B[2;31m\u001B[0m\n" +
                        "```"
        );

        this.textChannel.sendMessageEmbeds(builder.build()).setActionRow(createCloseButton()).queue(message -> processNextAction(TicketStatus.QUESTION));

        event.reply("If you have any further questions, you can add them now.").setEphemeral(true).queue(response -> {
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

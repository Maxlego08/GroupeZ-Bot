package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.TicketStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;

public class TicketChooseType extends TicketAction {

    @Override
    public void process(Interaction interaction) {

        PermissionOverrideAction permissionOverrideAction = textChannel.upsertPermissionOverride(this.member);
        permissionOverrideAction.setAllowed(Permission.VIEW_CHANNEL).queue(e -> {

            String welcomeMessage = getMessage(ticket.getLangType(), Message.TICKET_WELCOME, user.getAsMention());
            textChannel.sendMessage(welcomeMessage).queue();

            Emoji emoji = Config.getSpigotEmoji(guild);
            EmbedBuilder builder = this.createEmbed();

            builder.setDescription(getMessage(this.ticket.getLangType(), Message.TICKET_CHOOSE, emoji.getFormatted()));

            MessageCreateAction action = textChannel.sendMessageEmbeds(builder.build());

            Button buttonOrder = new ButtonImpl(BUTTON_CHOOSE_VERIFY,
                    "Verify Purchase", ButtonStyle.SUCCESS, false,
                    Emoji.fromUnicode("U+1F4B5"));

            Button buttonHelp = new ButtonImpl(BUTTON_CHOOSE_SUPPORT,
                    getMessage(this.ticket.getLangType(), Message.TICKET_CHOOSE_PLUGIN), ButtonStyle.PRIMARY, false,
                    Emoji.fromUnicode("U+2753"));

            Button buttonSpigot = new ButtonImpl(BUTTON_CHOOSE_SPIGOT,
                    getMessage(this.ticket.getLangType(), Message.TICKET_CHOOSE_SPIGOT), ButtonStyle.SECONDARY, false,
                    emoji);

            Button buttonBeforePurchase = new ButtonImpl(BUTTON_CHOOSE_BEFORE_PURCHASE,
                    getMessage(this.ticket.getLangType(), Message.TICKET_CHOOSE_BEFORE_PURCHASE), ButtonStyle.SECONDARY, false,
                    Emoji.fromUnicode("U+1F44B"));

            action.setActionRow(buttonOrder, buttonHelp, buttonSpigot, buttonBeforePurchase, createCloseButton());
            action.queue(result -> {
                permissionOverrideAction.setAllowed(Permission.VIEW_CHANNEL).queue();
            });

        });

    }

    @Override
    public void onButton(ButtonInteractionEvent event) {

        String buttonId = event.getButton().getId();
        if (buttonId == null) {
            event.reply("Impossible de trouver l'intéraction.").setEphemeral(true).queue();
            return;
        }

        switch (buttonId) {

            case BUTTON_CHOOSE_SUPPORT -> processNextAction(TicketStatus.CHOOSE_PLUGIN);
            case BUTTON_CHOOSE_VERIFY -> processNextAction(TicketStatus.VERIFY_PURCHASE);

            default -> event.reply("Impossible de trouver l'intéraction.").setEphemeral(true).queue();
        }

    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {

    }

    @Override
    public void onModal(ModalInteractionEvent event) {

    }

    @Override
    public void onMessage(MessageReceivedEvent event) {

    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.CHOOSE_TYPE;
    }
}

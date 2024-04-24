package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.tickets.TicketStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TicketVerification extends TicketAction {
    @Override
    public void process(Interaction interaction) {

        EmbedBuilder builder = this.createEmbed();

        setDescription(builder, ":warning: Please send **proof of purchase** in this channel. A screenshot from the page where you purchased the plugin is enough. We need to see the entire page with your username and date.", "Please wait for someone to verify your purchase.\n\nIf you need help after your verification, please create a new ticket.\n\nCheck for the plugin: " + ticket.getPlugin().getName());

        ((IMessageEditCallback) this.event).editMessageEmbeds(builder.build()).setActionRow(createCloseButton()).queue(r -> updatePermission(null, Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL));
    }

    @Override
    public void onButton(ButtonInteractionEvent event) {

    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {

    }

    @Override
    public void onModal(ModalInteractionEvent event) {

    }

    @Override
    public void onMessage(MessageReceivedEvent event) {

        Message message = event.getMessage();
        boolean containsImage = message.getAttachments().stream().anyMatch(Message.Attachment::isImage);

        // If the message contains an image, then it is a moderator who must check if the proof of purchase is good.
        if (containsImage) {

            message.reply(":white_check_mark: You just sent a proof of purchase. A team member will soon verify your proof. You can now fill out the form for your application.\n\nAfter a moderator checks your purchase, you need to create a new ticket.").queue(success -> {
                success.delete().queueAfter(10, TimeUnit.SECONDS);
                this.sendModeratorAccept();
                PermissionOverrideAction permissionOverrideAction = textChannel.upsertPermissionOverride(this.member);
                permissionOverrideAction.setAllowed(Permission.VIEW_CHANNEL).queue();
            });
        } else {

            if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MESSAGE_MANAGE)) return;

            message.reply(":x: You must provide proof of purchase with an image. Please try again.").queue(success -> {
                message.delete().queue();
                success.delete().queueAfter(10, TimeUnit.SECONDS);
            });
        }

    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.VERIFY_PURCHASE;
    }
}

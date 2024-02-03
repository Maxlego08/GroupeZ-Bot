package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.tickets.TicketStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;

public class TicketVerification extends TicketAction {
    @Override
    public void process(Interaction interaction) {

        EmbedBuilder builder = this.createEmbed();

        setDescription(builder,
                ":warning: Please send **proof of purchase** in this channel. A screenshot from the page where you purchased the plugin is enough. We need to see the entire page with your username and date.",
                "Please wait for someone to verify your purchase."
        );

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

    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.VERIFY_PURCHASE;
    }
}

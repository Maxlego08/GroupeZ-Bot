package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.tickets.TicketStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;

public class TicketOpen extends TicketAction {

    @Override
    public void process(Interaction interaction) {
        updatePermission(null, Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL);
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
        return TicketStatus.OPEN;
    }
}

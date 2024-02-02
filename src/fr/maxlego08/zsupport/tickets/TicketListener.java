package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Objects;

public class TicketListener extends ListenerAdapter implements Constant {

    private final TicketManager ticketManager;

    public TicketListener(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        Button button = event.getButton();

        if (event.getChannel().getIdLong() == Config.ticketChannel && !event.getUser().isBot()) {

            LangType langType = Objects.equals(button.getId(), BUTTON_FR) ? LangType.FR : LangType.US;
            this.ticketManager.createTicket(event.getUser(), event.getGuild(), langType, event);
        } else if (event.getChannel() instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategoryIdLong() == Config.ticketCategoryId && !event.getUser().isBot()) {

            this.ticketManager.buttonAction(event, event.getGuild());
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getChannel() instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategoryIdLong() == Config.ticketCategoryId && !event.getUser().isBot()) {

            this.ticketManager.selectionAction(event, event.getGuild());
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getChannel() instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategoryIdLong() == Config.ticketCategoryId && !event.getUser().isBot()) {

            this.ticketManager.modalAction(event, event.getGuild());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getChannel() instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategoryIdLong() == Config.ticketCategoryId && !event.getAuthor().isBot()) {

            this.ticketManager.onMessage(event, event.getGuild());
        }
    }
}

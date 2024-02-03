package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.tickets.TicketManager;
import fr.maxlego08.zsupport.tickets.TicketStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TicketZMenuClose extends TicketAction {

    @Override
    public void process(Interaction interaction) {

        String forum = guild.getForumChannelById(Config.zMenuForum).getAsMention();

        EmbedBuilder builder = new EmbedBuilder();
        setEmbedFooter(this.guild, builder, new Color(218, 8, 8));
        setDescription(builder, ":x: You cannot create a ticket for zMenu.",
                "",
                "**You can open a ticket by upgrading to Premium here:**",
                "https://minecraft-inventory-builder.com/account-upgrade",
                "",
                "You can get help with zMenu through the discord forum: " + forum);

        this.textChannel.sendMessageEmbeds(builder.build()).queue();

        TicketManager.scheduler.schedule(() -> {

            this.ticket.setTicketStatus(TicketStatus.CLOSE);
            this.ticketManager.getSqlManager().updateTicket(ticket, true);

            this.textChannel.delete().queue();


        }, 1, TimeUnit.MINUTES);
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
        return TicketStatus.VERIFY_ZMENU_CLOSE;
    }
}

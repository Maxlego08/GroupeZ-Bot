package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketManager;
import fr.maxlego08.zsupport.tickets.TicketStatus;
import fr.maxlego08.zsupport.utils.ZUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;

public abstract class TicketAction extends ZUtils {

    protected TicketManager ticketManager;
    protected Ticket ticket;
    protected Guild guild;
    protected TextChannel textChannel;
    protected User user;
    protected Member member;
    protected Interaction event;

    public abstract void process(Interaction interaction);

    public abstract void onButton(ButtonInteractionEvent event);

    public abstract void onSelect(StringSelectInteractionEvent event);

    public abstract TicketStatus getTicketStatus();

    public void preProcess(TicketManager ticketManager, Ticket ticket, Guild guild, TextChannel textChannel, Member member, Interaction interaction) {
        this.ticketManager = ticketManager;
        this.ticket = ticket;
        this.guild = guild;
        this.textChannel = textChannel;
        this.member = member;
        this.user = member.getUser();
        this.event = interaction;
        this.process(interaction);
    }

    protected EmbedBuilder createEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("GroupeZ - Support");
        setEmbedFooter(this.guild, builder, new Color(45, 150, 45));
        return builder;
    }

    public void preButtonAction(ButtonInteractionEvent event, Member member, Guild guild, TicketManager ticketManager, Ticket ticket) {
        this.ticketManager = ticketManager;
        this.ticket = ticket;
        this.guild = guild;
        this.textChannel = ticket.getTextChannel(guild);
        this.member = member;
        this.user = member.getUser();
        this.event = event;
        onButton(event);
    }

    protected void processNextAction(TicketStatus ticketStatus) {

        TicketAction ticketAction = ticketStatus.getAction();
        if (ticketAction == null) return;

        this.ticket.setTicketStatus(ticketStatus);
        this.ticket.setTicketAction(ticketAction);

        this.ticketManager.updateTicket(ticket);

        ticketAction.preProcess(this.ticketManager, this.ticket, this.guild, this.textChannel, this.member, this.event);
    }

    protected void replyModal(Modal modal) {
        if (this.event instanceof IModalCallback) {
            ((IModalCallback) this.event).replyModal(modal).queue();
        } else if (this.event instanceof IReplyCallback) {
            ((IReplyCallback) this.event).reply("An error occurred during the interaction.").queue();
        }
    }

    protected StringBuilder createDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Bienvenue sur le support de **GroupeZ**");
        stringBuilder.append("\n");
        stringBuilder.append("Pour une meilleur prise en charge de votre demande, merci de répondre aux questions:");
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        return stringBuilder;
    }

    public void preselectionAction(StringSelectInteractionEvent event, Member member, Guild guild, TicketManager ticketManager, Ticket ticket) {
        this.ticketManager = ticketManager;
        this.ticket = ticket;
        this.guild = guild;
        this.textChannel = ticket.getTextChannel(guild);
        this.member = member;
        this.user = member.getUser();
        this.event = event;
        this.onSelect(event);
    }
}

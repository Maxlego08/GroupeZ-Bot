package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.actions.TicketAction;
import fr.maxlego08.zsupport.tickets.storage.SqlManager;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketManager extends ZUtils {

    private final ZSupport instance;
    private final SqlManager sqlManager = new SqlManager();
    private final List<Ticket> tickets = new ArrayList<>();

    public TicketManager(ZSupport instance) {
        this.instance = instance;
    }

    public void save() {
        this.sqlManager.getSqlConnection().disconnect();
    }

    public void load() {
        this.sqlManager.createTable();
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public Optional<Ticket> getByUser(User user) {
        return tickets.stream().filter(ticket -> ticket.getUserId() == user.getIdLong()).findAny();
    }

    public Optional<Ticket> getByChannel(ISnowflake channel) {
        return tickets.stream().filter(ticket -> ticket.getChannelId() == channel.getIdLong()).findAny();
    }

    public void createTicket(User user, Guild guild, LangType langType, ButtonInteractionEvent event) {

        Optional<Ticket> optional = getByUser(user);

        // The user already has an open ticket
        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            ticket.sendMessage(guild, Message.TICKET_ALREADY_CREATE);

            String replyContent = this.getMessage(langType, Message.TICKET_ALREADY_CREATE_REPLY, ticket.getTextChannel(guild).getAsMention());
            event.deferReply(true).setContent(replyContent).queue();

            return;
        }

        VerifyManager manager = VerifyManager.getInstance();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("GroupeZ - Support");
        builder.setDescription(this.getMessage(langType, Message.TICKET_CREATE_WAIT));
        setEmbedFooter(guild, builder);

        event.deferReply(true).addEmbeds(builder.build()).queue(message -> {

            Runnable errorRunnable = () -> {
                builder.setDescription(this.getMessage(langType, Message.TICKET_CREATE_ERROR));
                builder.setColor(Color.RED);
                message.editOriginalEmbeds(builder.build()).queue();
            };

            manager.userIsLink(user, () -> {
                Category category = getTicketCategory(guild);
                category.createTextChannel("ticket-waiting").queue(ticketChannel -> createTicket(user, guild, langType, event, ticketChannel, errorRunnable));
            }, errorRunnable);
        });
    }

    private void createTicket(User user, Guild guild, LangType langType, ButtonInteractionEvent event, TextChannel ticketChannel, Runnable errorRunnable) {

        // Création du nouveau ticket après la vérification de l'utilisateur
        Ticket ticket = new Ticket(langType, ticketChannel.getIdLong(), user.getIdLong(), TicketStatus.CHOOSE_TYPE, TicketType.WAITING);
        ticket.setTextChannel(ticketChannel);

        this.sqlManager.createTicket(ticket, () -> {

            TicketAction action = TicketStatus.CHOOSE_TYPE.getAction();
            ticket.setTicketAction(action);
            action.preProcess(this, ticket, guild, ticketChannel, event.getMember(), event);
        }, () -> {

            ticket.sendMessage(guild, "Unable to add ticket to database.");
            errorRunnable.run();
        });
        this.tickets.add(ticket);
    }

    public Category getTicketCategory(Guild guild) {
        return guild.getCategoryById(Config.ticketCategoryId);
    }

    public void buttonAction(ButtonInteractionEvent event, User user, Guild guild) {

        Optional<Ticket> optional = getByUser(user);

        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            TicketAction ticketAction = ticket.getTicketAction();
            if (ticketAction == null) return;

            ticketAction.preButtonAction(event, event.getMember(), guild, this, ticket);
        }
    }

    public void selectionAction(StringSelectInteractionEvent event, User user, Guild guild) {
        Optional<Ticket> optional = getByUser(user);

        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            TicketAction ticketAction = ticket.getTicketAction();
            if (ticketAction == null) return;

            ticketAction.preselectionAction(event, event.getMember(), guild, this, ticket);
        }
    }

    public void updateTicket(Ticket ticket) {
        this.sqlManager.updateTicket(ticket);
    }
}

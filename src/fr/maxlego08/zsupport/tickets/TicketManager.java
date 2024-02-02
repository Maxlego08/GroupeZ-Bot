package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.actions.TicketAction;
import fr.maxlego08.zsupport.tickets.storage.SqlManager;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.verify.VerifyManager;
import gs.mclo.api.MclogsClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TicketManager extends ZUtils {

    private final ZSupport instance;
    private final SqlManager sqlManager = new SqlManager();
    private List<Ticket> tickets = new ArrayList<>();

    public TicketManager(ZSupport instance) {
        this.instance = instance;
    }

    public void save() {
        this.sqlManager.getSqlConnection().disconnect();
    }

    public void load() {
        this.sqlManager.createTable(tickets -> this.tickets = tickets);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public Optional<Ticket> getByUser(User user, Guild guild) {
        verifyTickets(guild);
        return tickets.stream().filter(ticket -> ticket.getUserId() == user.getIdLong()).findAny();
    }

    public Optional<Ticket> getByChannel(ISnowflake channel, Guild guild) {
        verifyTickets(guild);
        return tickets.stream().filter(ticket -> ticket.getChannelId() == channel.getIdLong()).findAny();
    }

    public void createTicket(User user, Guild guild, LangType langType, ButtonInteractionEvent event) {

        Optional<Ticket> optional = getByUser(user, guild);

        // The user already has an open ticket
        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            if (ticket.getTicketStatus() != TicketStatus.CLOSE) {
                ticket.sendMessage(guild, Message.TICKET_ALREADY_CREATE);

                String replyContent = this.getMessage(langType, Message.TICKET_ALREADY_CREATE_REPLY, ticket.getTextChannel(guild).getAsMention());
                event.deferReply(true).setContent(replyContent).queue();

                return;
            }
        }

        VerifyManager manager = VerifyManager.getInstance();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("GroupeZ - Support");
        setEmbedFooter(guild, builder, new Color(209, 130, 27));
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
                category.createTextChannel("ticket-waiting").queue(ticketChannel -> createTicket(user, guild, langType, event, ticketChannel, errorRunnable, message));
            }, errorRunnable);
        });
    }

    private void createTicket(User user, Guild guild, LangType langType, ButtonInteractionEvent event, TextChannel ticketChannel, Runnable errorRunnable, InteractionHook message) {

        // Création du nouveau ticket après la vérification de l'utilisateur
        Ticket ticket = new Ticket(langType, ticketChannel.getIdLong(), user.getIdLong(), TicketStatus.CHOOSE_TYPE, TicketType.WAITING);
        ticket.setTextChannel(ticketChannel);

        this.sqlManager.createTicket(ticket, () -> {

            TicketAction action = TicketStatus.CHOOSE_TYPE.getAction();
            ticket.setTicketAction(action);
            action.preProcess(this, ticket, guild, ticketChannel, event.getMember(), event);

            EmbedBuilder builderCreateChannel = new EmbedBuilder();
            builderCreateChannel.setTitle("GroupeZ - Support");
            builderCreateChannel.setDescription(this.getMessage(langType, Message.TICKET_CREATE_SUCCESS) + ticketChannel.getAsMention());
            setEmbedFooter(guild, builderCreateChannel, new Color(45, 250, 45));
            message.editOriginalEmbeds(builderCreateChannel.build()).queue();

        }, () -> {

            ticket.sendMessage(guild, "Unable to add ticket to database.");
            errorRunnable.run();
        });
        this.tickets.add(ticket);
    }

    public Category getTicketCategory(Guild guild) {
        return guild.getCategoryById(Config.ticketCategoryId);
    }

    public void buttonAction(ButtonInteractionEvent event, Guild guild) {

        Optional<Ticket> optional = getByChannel(event.getChannel(), guild);

        if (optional.isPresent()) {

            Ticket ticket = optional.get();

            if (Objects.equals(event.getButton().getId(), BUTTON_CLOSE)) {
                TicketAction action = ticket.getTicketAction();
                if (action != null) action.startConfirmClose();
                event.reply(":warning: Please confirm that the ticket is closed.").setEphemeral(true).queue();
                return;
            }

            if (Objects.equals(event.getButton().getId(), BUTTON_CLOSE_CONFIRM)) {
                ticket.close(guild);
                this.sqlManager.updateTicket(ticket);
                event.reply(event.getUser().getAsMention() + " just closed the ticket.").queue();
                return;
            }

            TicketAction ticketAction = ticket.getTicketAction();
            if (ticketAction == null) return;

            ticketAction.preButtonAction(event, event.getMember(), guild, this, ticket);
        }
    }

    public void selectionAction(StringSelectInteractionEvent event, Guild guild) {
        Optional<Ticket> optional = getByChannel(event.getChannel(), guild);

        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            TicketAction ticketAction = ticket.getTicketAction();
            if (ticketAction == null) return;

            ticketAction.preSelectionAction(event, event.getMember(), guild, this, ticket);
        }
    }

    public void modalAction(ModalInteractionEvent event, Guild guild) {
        Optional<Ticket> optional = getByChannel(event.getChannel(), guild);

        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            TicketAction ticketAction = ticket.getTicketAction();
            if (ticketAction == null) return;

            ticketAction.preModalAction(event, event.getMember(), guild, this, ticket);
        }
    }

    public void updateTicket(Ticket ticket) {
        this.sqlManager.updateTicket(ticket);
    }

    public void onMessage(MessageReceivedEvent event, Guild guild) {

        Optional<Ticket> optional = getByChannel(event.getChannel(), guild);

        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            this.sqlManager.addMessageToTicket(ticket, event.getMessage());

            TicketAction ticketAction = ticket.getTicketAction();
            if (ticketAction == null) return;

            ticketAction.preMessageAction(event, event.getMember(), guild, this, ticket);
        }
    }

    public void closeTicket(SlashCommandInteractionEvent event, Guild guild, Member member) {

        Optional<Ticket> optional = getByChannel(event.getChannel(), guild);

        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            TicketAction action = ticket.getTicketAction();
            if (action != null) action.startConfirmClose();
            event.reply(":warning: Please confirm that the ticket is closed.").setEphemeral(true).queue();
        } else {

            event.reply(":x: Unable to find the ticket, you cannot use this command.").setEphemeral(true).queue();
        }
    }

    public void processMessageUpload(MessageReceivedEvent event) {
        net.dv8tion.jda.api.entities.Message message = event.getMessage();
        message.getAttachments().forEach(attachment -> {

            if (attachment.isImage() || attachment.isVideo()) return;

            String fileExtension = attachment.getFileExtension();
            if (fileExtension != null && (fileExtension.equals("yml") || fileExtension.equals("log") || fileExtension.equals("txt"))) {
                SqlManager.service.execute(() -> {
                    String content = readContentFromURL(attachment.getProxy().getUrl());
                    try {
                        MclogsClient mclogsClient = ZSupport.instance.getMclogsClient();
                        mclogsClient.uploadLog(content).thenAccept(uploadLogResponse -> {
                            if (uploadLogResponse.isSuccess()) {
                                MessageCreateAction action = event.getMessage().reply(":open_file_folder: " + attachment.getFileName() + ": https://mclo.gs/" + uploadLogResponse.getId());
                                action.setSuppressEmbeds(true);
                                action.queue();
                            }
                        });
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
            }
        });
    }

    private void verifyTickets(Guild guild) {
        Iterator<Ticket> ticketIterator = this.tickets.iterator();
        while (ticketIterator.hasNext()) {
            Ticket ticket = ticketIterator.next();
            if (ticket.getTextChannel(guild) == null) {
                ticket.setTicketStatus(TicketStatus.CLOSE);
                this.sqlManager.updateTicket(ticket);
                ticketIterator.remove();
            }
        }
    }

    public void userLeave(Guild guild, User user) {
        Optional<Ticket> optional = this.getByUser(user, guild);
        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            ticket.setTicketStatus(TicketStatus.CLOSE);
            this.sqlManager.updateTicket(ticket);

            TextChannel channel = ticket.getTextChannel(guild);
            TextChannelManager channelManager = channel.getManager();
            channelManager.setName("user-leave").queue();
        }
    }
}

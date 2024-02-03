package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.plugins.PluginManager;
import fr.maxlego08.zsupport.tickets.actions.TicketAction;
import fr.maxlego08.zsupport.tickets.storage.SqlManager;
import fr.maxlego08.zsupport.utils.ChannelType;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TicketManager extends ZUtils {

    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ZSupport instance;
    private final SqlManager sqlManager = new SqlManager();
    private final ScheduledFuture<?> scheduledFuture;
    private final Map<Long, ChannelInfo> channelInfoMap = new HashMap<>();
    private List<Ticket> tickets = new ArrayList<>();

    public TicketManager(ZSupport instance) {
        this.instance = instance;
        this.scheduledFuture = scheduler.scheduleAtFixedRate(this::checkTickets, 1, 1, TimeUnit.MINUTES);
    }

    public void save() {
        this.scheduledFuture.cancel(true);
        this.sqlManager.getSqlConnection().disconnect();
    }

    public void load(Runnable runnable) {
        this.sqlManager.createTable(tickets -> {
            this.tickets = tickets;
            runnable.run();
        });
    }

    private void checkTickets() {
        List<Ticket> tickets = getTickets();
        long now = Instant.now().toEpochMilli();
        Iterator<Ticket> iterator = tickets.iterator();
        Guild guild = instance.getJda().getGuildById(Config.guildId);

        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();

            if (ticket.getTicketStatus() == TicketStatus.CLOSE) continue;

            long hoursSinceUpdate = (now - ticket.getUpdatedAt()) / (1000 * 60 * 60);

            TextChannel channel = ticket.getTextChannel(guild);

            if (hoursSinceUpdate > 72) {

                iterator.remove();

                ticket.setTicketStatus(TicketStatus.CLOSE);
                this.sqlManager.updateTicket(ticket, true);

                TextChannelManager channelManager = channel.getManager();
                channelManager.setName("ticket-close").queue();

                EmbedBuilder builder = new EmbedBuilder();
                setEmbedFooter(guild, builder, new Color(204, 14, 14));
                builder.setTitle("GroupeZ - Support");
                setDescription(builder, "Ticket closed after 72 hours without response.");
                channel.sendMessageEmbeds(builder.build()).queue();

                System.out.println("Deleted channel for ticket ID: " + ticket.getId());

            } else if (hoursSinceUpdate > 48 && !ticket.isNotificationSent()) {

                ticket.setNotificationSent(true);
                ticket.sendMessage(guild, "%user%, close your ticket in 24 hours if there is no answer.");
                this.sqlManager.updateTicket(ticket, false);

                System.out.println("Sent reminder for ticket ID: " + ticket.getId());
            }

        }
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

    public void createTicket(User user, Guild guild, LangType langType, ButtonInteractionEvent event, TicketStatus ticketStatus) {

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
                category.createTextChannel("ticket-waiting").queue(ticketChannel -> createTicket(user, guild, langType, event, ticketChannel, errorRunnable, message, ticketStatus));
            }, errorRunnable);
        });
    }

    private void createTicket(User user, Guild guild, LangType langType, ButtonInteractionEvent event, TextChannel ticketChannel, Runnable errorRunnable, InteractionHook message, TicketStatus ticketStatus) {

        // Création du nouveau ticket après la vérification de l'utilisateur
        Ticket ticket = new Ticket(langType, ticketChannel.getIdLong(), user.getIdLong(), ticketStatus, TicketType.WAITING);
        ticket.setTextChannel(ticketChannel);
        if (ticketStatus == TicketStatus.VERIFY_ZMENU_PURCHASE) ticket.setPluginId(Config.zMenu.getPluginId());

        this.sqlManager.createTicket(ticket, user.getName(), () -> {

            TicketAction action = ticketStatus.getAction();
            ticket.setTicketAction(action);

            action.preProcess(this, ticket, guild, ticketChannel, event.getMember(), event);
            action.processChannelName(ticketStatus);

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
                if (action != null)
                    action.startConfirmClose(event, guild, event.getMember(), ticket.getTextChannel(guild), ticket);
                event.reply(":warning: Please confirm that the ticket is closed.").setEphemeral(true).queue();
                return;
            }

            if (Objects.equals(event.getButton().getId(), BUTTON_CLOSE_CONFIRM)) {
                ticket.close(guild);
                this.sqlManager.updateTicket(ticket, true);
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
        this.sqlManager.updateTicket(ticket, true);
    }

    public void onMessage(MessageReceivedEvent event, Guild guild) {

        Optional<Ticket> optional = getByChannel(event.getChannel(), guild);

        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            ticket.setNotificationSent(false);

            this.sqlManager.addMessageToTicket(ticket, event.getMessage());
            this.sqlManager.updateTicket(ticket, true);

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
            if (action != null) action.startConfirmClose(event, guild, member, ticket.getTextChannel(guild), ticket);
            event.reply(":warning: Please confirm that the ticket is closed.").setEphemeral(true).queue();
        } else {

            event.reply(":x: Unable to find the ticket, you cannot use this command.").setEphemeral(true).queue();
        }
    }

    public void processMessageUpload(MessageReceivedEvent event) {
        net.dv8tion.jda.api.entities.Message message = event.getMessage();
        message.getAttachments().forEach(attachment -> {

            SqlManager.service.execute(() -> {
                if (attachment.getSize() < 16000000) {
                    Optional<Ticket> optional = getByChannel(event.getChannel(), event.getGuild());
                    optional.ifPresent(ticket -> attachment.getProxy().download().thenAccept(inputStream -> this.sqlManager.addAttachment(ticket.getId(), message.getIdLong(), inputStream)));
                }
            });

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
                this.sqlManager.updateTicket(ticket, true);
                ticketIterator.remove();
            }
        }
    }

    public void userLeave(Guild guild, User user) {
        Optional<Ticket> optional = this.getByUser(user, guild);
        if (optional.isPresent()) {

            Ticket ticket = optional.get();
            ticket.setTicketStatus(TicketStatus.CLOSE);
            this.sqlManager.updateTicket(ticket, true);

            TextChannel channel = ticket.getTextChannel(guild);
            TextChannelManager channelManager = channel.getManager();
            channelManager.setName("user-leave").queue();
        }
    }

    public SqlManager getSqlManager() {
        return sqlManager;
    }

    public void verifyVersion(Ticket ticket, TextChannel textChannel, Guild guild, String version) {
        PluginManager.fetchResource(ticket.getPlugin(), resource -> {
            boolean isLastVersion = version.equals(resource.getVersion().getVersion());

            if (!isLastVersion) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("GroupeZ - Support");
                setEmbedFooter(guild, builder, new Color(218, 8, 8));
                setDescription(builder, "```ansi\n" + "\u001B[2;31mYou are not using the latest version of the plugin! Your problem will probably come from this. Please check if the latest version fixes your problem. Don’t forget to also read the changelogs. If your problem is known it will have been notified in a changelogs.\u001B[0m\n" + "```");

                textChannel.sendMessageEmbeds(builder.build()).queue();
            }

            this.sqlManager.insertPluginForTicket(ticket.getId(), version, isLastVersion);
        });
    }

    private ChannelInfo getInfo(ISnowflake channel) {
        return channelInfoMap.computeIfAbsent(channel.getIdLong(), id -> new ChannelInfo());
    }

    public void sendChannelInformations(MessageReceivedEvent event, TextChannel textChannel, ChannelType channelType, ChannelInfo channelInfo) {

        if (channelType == ChannelType.GENERAL) return;

        if (channelInfo.getMessageAt() > System.currentTimeMillis()) return;

        channelInfo.setMessageAt(System.currentTimeMillis() + (1000 * 60 * 15));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(channelType.getTitle());
        setEmbedFooter(event.getGuild(), builder, new Color(186, 8, 8));

        TextChannel ticketChannel = textChannel.getGuild().getTextChannelById(Config.ticketChannel);
        builder.setDescription(String.format(channelType.getDescription(), ticketChannel.getAsMention()));

        textChannel.sendMessageEmbeds(builder.build()).queue(message -> channelInfo.setMessageId(message.getIdLong()));
    }

    public void sendChannelInformations(MessageReceivedEvent event, TextChannel textChannel, ChannelType channelType) {

        ChannelInfo channelInfo = getInfo(textChannel);

        if (channelInfo.getMessageId() == 0) {

            sendChannelInformations(event, textChannel, channelType, channelInfo);

        } else {

            textChannel.getHistoryAround(channelInfo.getMessageId(), 99).queue(history -> {
                net.dv8tion.jda.api.entities.Message message = history.getMessageById(channelInfo.getMessageId());
                if (message != null) {
                    if (System.currentTimeMillis() > channelInfo.getMessageAt()) {
                        channelInfo.setMessageId(0);
                        message.delete().queue(s -> sendChannelInformations(event, textChannel, channelType, channelInfo));
                        return;
                    }
                }
                sendChannelInformations(event, textChannel, channelType, channelInfo);
            });
        }

    }
}

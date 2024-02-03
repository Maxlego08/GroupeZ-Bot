package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ErrorManager {

    private final Set<String> helpKeywords = new HashSet<>(Arrays.asList("help", "aide", "support", "assistance", "problem", "problème", "how to", "comment faire", "error", "erreur", "issue", "problème", "bug", "fix", "resolve", "résoudre", "broken", "cassé", "fail", "échouer", "doesn't work", "ne fonctionne pas", "can't", "cannot", "ne peux pas", "ne peut pas", "help me", "aidez-moi", "question", "questions", "how do I", "comment je peux", "guide", "guidance", "orientation", "stuck", "bloqué", "trouble", "difficulté"));
    private final Pattern ERROR_PATTERN = Pattern.compile("\\[(\\d{2}:\\d{2}:\\d{2})\\] ERROR\\]: .+\\n" + // Ligne d'introduction de l'erreur avec l'heure
            "(?:.*Exception: .+\\n)?" + // Ligne optionnelle de l'exception (rendue plus générique)
            "(?:\\s*at .+\\(.*\\.java:\\d+\\)\\n)+" + // Lignes de stack trace
            "|java\\.lang\\.[\\w\\.]+Exception: .+" // Ou une exception sur une seule ligne
    );
    private final ConcurrentHashMap<Long, Long> userCooldownMap = new ConcurrentHashMap<>();

    public boolean isErrorMessage(String message) {
        return ERROR_PATTERN.matcher(message).find();
    }

    public boolean containsHelpRequest(String message) {
        String normalizedMessage = message.toLowerCase();
        return helpKeywords.stream().anyMatch(normalizedMessage::contains);
    }

    public void processMessage(MessageReceivedEvent event, Message message, User author) {

        if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MESSAGE_MANAGE)) return;

        String messageContent = message.getContentRaw().replace("`", "");

        if (isErrorMessage(messageContent)) {
            processErrorMessage(event, message, author);
        } else if (containsHelpRequest(messageContent)) {
            processHelpMessage(event, message, author);
        }

        this.cleanUpOldEntries();
    }

    private void processErrorMessage(MessageReceivedEvent event, Message message, User author) {
        String replyMessage = author.getAsMention() + ", you seem to need help. But you’re in the general here. To ask for help please respect the rules of discord and use " + getTicketMention(event.getGuild());
        message.reply(replyMessage).queue(s -> {
            message.delete().queueAfter(1, TimeUnit.SECONDS);
            s.delete().queueAfter(1, TimeUnit.MINUTES);
        });
    }

    private void processHelpMessage(MessageReceivedEvent event, Message message, User author) {
        if (!canSendHelpRequest(author.getIdLong())) return;
        String replyMessage = author.getAsMention() + ", if you need help please go to " + getTicketMention(event.getGuild());
        message.reply(replyMessage).queue();
    }

    public boolean canSendHelpRequest(long userId) {
        long lastRequestTime = userCooldownMap.getOrDefault(userId, 0L);
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastRequestTime) > 60000) {
            userCooldownMap.put(userId, currentTime);
            cleanUpOldEntries();
            return true;
        }
        return false;
    }

    private String getTicketMention(Guild guild) {
        return Objects.requireNonNull(guild.getTextChannelById(Config.ticketChannel)).getAsMention();
    }

    private void cleanUpOldEntries() {
        long oneMinuteAgo = System.currentTimeMillis() - 60000;
        userCooldownMap.entrySet().removeIf(entry -> entry.getValue() < oneMinuteAgo);
    }
}

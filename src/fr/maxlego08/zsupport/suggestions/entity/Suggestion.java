package fr.maxlego08.zsupport.suggestions.entity;

import java.util.UUID;

public class Suggestion {

    private final UUID uuid;
    private final String authorId;
    private final String suggestion;
    private final String messageId;

    public Suggestion(String authorId, String suggestion, String messageId) {
        this.uuid = UUID.randomUUID();
        this.authorId = authorId;
        this.suggestion = suggestion;
        this.messageId = messageId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public String getMessageId() {
        return messageId;
    }
}

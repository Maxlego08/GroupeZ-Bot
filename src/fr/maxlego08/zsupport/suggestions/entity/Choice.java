package fr.maxlego08.zsupport.suggestions.entity;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

public interface Choice {

    String getId();
    Emoji getEmoji();
    ButtonStyle getButtonStyle();
    void run(Interaction interaction, Suggestion suggestion);
}

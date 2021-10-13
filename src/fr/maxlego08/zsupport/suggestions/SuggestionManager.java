package fr.maxlego08.zsupport.suggestions;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.suggestions.entity.Choice;
import fr.maxlego08.zsupport.suggestions.entity.RefuseChoice;
import fr.maxlego08.zsupport.suggestions.entity.Suggestion;
import fr.maxlego08.zsupport.suggestions.entity.ValidChoice;
import fr.maxlego08.zsupport.suggestions.listeners.SuggestListener;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Saveable;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestionManager implements Saveable {

    private final static List<Suggestion> suggestions = new ArrayList<>();
    private static volatile SuggestionManager instance;
    private final List<Choice> choices;

    public SuggestionManager() {

        this.choices = new ArrayList<>();

        this.choices.addAll(Arrays.asList(new ValidChoice(), new RefuseChoice()));

        this.registerListener(new SuggestListener());
    }

    public static List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public Suggestion getByMessageId(String messageId) {
        return suggestions.stream().filter(suggestion -> suggestion.getMessageId().equals(messageId)).findFirst().orElse(null);
    }

    public Suggestion getByUUID(String uuid) {
        return suggestions.stream().filter(suggestion -> suggestion.getUuid().toString().equals(uuid)).findFirst().orElse(null);
    }

    public List<Suggestion> getSuggestionByUser(String id) {
        return suggestions.stream().filter(suggestion -> suggestion.getAuthorId().equals(id)).collect(Collectors.toList());
    }

    @Override
    public void save(Persist persist) {
        persist.save(this);
    }

    @Override
    public void load(Persist persist) {
        persist.loadOrSaveDefault(this, SuggestionManager.class);
    }

    public static SuggestionManager getInstance() {
        if (instance == null) {
            synchronized (VerifyManager.class) {
                if (instance == null) {
                    instance = new SuggestionManager();
                }
            }
        }
        return instance;
    }

    public void registerListener(ListenerAdapter listenerAdapter) {
        ZSupport.instance.getJda().addEventListener(listenerAdapter);
    }

    public List<Choice> getChoices() {
        return choices;
    }
}

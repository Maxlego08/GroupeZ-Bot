package fr.maxlego08.zsupport.suggestions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.maxlego08.zsupport.suggestions.entity.Choice;
import fr.maxlego08.zsupport.suggestions.entity.RefuseChoice;
import fr.maxlego08.zsupport.suggestions.entity.Suggestion;
import fr.maxlego08.zsupport.suggestions.entity.ValidChoice;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Saveable;
import fr.maxlego08.zsupport.verify.VerifyManager;

public class SuggestionManager implements Saveable {

	private static List<Suggestion> suggestions = new ArrayList<>();
	private transient static volatile SuggestionManager instance;
	private transient final List<Choice> choices;

	public SuggestionManager() {

		this.choices = new ArrayList<>();

		this.choices.addAll(Arrays.asList(new ValidChoice(), new RefuseChoice()));
	}

	public static List<Suggestion> getSuggestions() {
		return suggestions;
	}

	public Optional<Suggestion> getByMessageId(String messageId) {
		System.out.println(messageId);
		return suggestions.stream().filter(suggestion -> {
			System.out.println(suggestion.getMessageId() + " - " + suggestion.getMessageId().equals(messageId));
			return suggestion.getMessageId().equals(messageId);
		}).findFirst();
	}

	public Suggestion getByUUID(String uuid) {
		return suggestions.stream().filter(suggestion -> suggestion.getUuid().toString().equals(uuid)).findFirst()
				.orElse(null);
	}

	public List<Suggestion> getSuggestionByUser(String id) {
		return suggestions.stream().filter(suggestion -> suggestion.getAuthorId().equals(id))
				.collect(Collectors.toList());
	}

	@Override
	public void save(Persist persist) {
		persist.save(this, "suggestions");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, SuggestionManager.class, "suggestions");
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

	public List<Choice> getChoices() {
		return choices;
	}
}

package fr.maxlego08.zsupport.lang;

import java.util.HashMap;
import java.util.Map;

import fr.maxlego08.zsupport.lang.langs.MessageEN;
import fr.maxlego08.zsupport.lang.langs.MessageFR;

public class Lang {

	private final Map<LangType, Map<Message, String>> messages = new HashMap<LangType, Map<Message, String>>();

	/**
	 * static Singleton instance.
	 */
	private static volatile Lang instance;

	/**
	 * Private constructor for singleton.
	 */
	private Lang() {
		messages.put(LangType.FR, new MessageFR().getMessages());
		messages.put(LangType.US, new MessageEN().getMessages());
	}

	/**
	 * Return a singleton instance of Lang.
	 */
	public static Lang getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (Lang.class) {
				if (instance == null) {
					instance = new Lang();
				}
			}
		}
		return instance;
	}

	/**
	 * 
	 * @param type
	 * @param message
	 * @return
	 */
	public String getMessage(LangType type, Message message, Object... objects) {
		String finalMessage = this.messages.get(type).getOrDefault(message, "Message not found");
		return objects.length == 0 ? finalMessage : String.format(finalMessage, objects);
	}

}

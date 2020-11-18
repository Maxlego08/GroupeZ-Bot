package fr.maxlego08.zsupport.lang;

import java.util.HashMap;
import java.util.Map;

import fr.maxlego08.zsupport.tickets.LangType;

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
		for (LangType type : LangType.values())
			messages.put(type, new HashMap<>());

		// Init messages

		// FR
		Map<Message, String> frMessage = new HashMap<>();
		frMessage.put(Message.TICKET_ALREADY_CREATE,
				"**Attention!** %user% tu as déjà créé un ticket ici, inutile d'enfaire un autre.");
		frMessage.put(Message.TICKET_DESC,
				"Bienvenue dans votre ticket, un membre de l'équipe  viendra répondre à vos besoins sous peu.");
		frMessage.put(Message.TICKET_PLUGIN_CHOOSE, "Clique sur le plugin pour lequel tu souhaites obtenir de l'aide:");
		frMessage.put(Message.TICKET_PLUGIN, "Vous venez de choisir le plugin **%plugin%** %pluginEmote%.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE,
				"Vous avez déjà confirmé l'achat du plugin, vous pouvez directement poser votre question.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR,
				"Vous n'avez pas confirmé l'achat du plugin, pour confirmer l'achat vous devez donner l'identifiant de la transaction Paypal.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_ID,
				"Vous devez envoyer **uniquement** l'id de la transaction pour qu'une vérification soit faite.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ID_SUCCESS,
				"Votre demande a bien été prise en compte, un administrateur va rapidement vérifier le paiement.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_SUCCESS, "Vous venez de reçevoir le rôle pour le plugin %plugin%");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_GIVE, "Impossible de prouver que vous avez acheter le plugin %plugin%.");

		// US
		Map<Message, String> usMessage = new HashMap<>();
		usMessage.put(Message.TICKET_ALREADY_CREATE,
				"**Warning!** %user% you've already opened a ticket here! No need to try to create another one.");
		usMessage.put(Message.TICKET_DESC,
				"Welcome to your ticket, a member of the team will come to meet your needs shortly.");
		usMessage.put(Message.TICKET_PLUGIN_CHOOSE, "Click on the plugin for which you want help:");
		usMessage.put(Message.TICKET_PLUGIN, "You just choose the plugin **%plugin%** %pluginEmote%.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE,
				"You have already confirmed the purchase of the plugin, you can directly ask your question.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR,
				"You have not confirmed the purchase of the plugin, to confirm the purchase you must give the identifier of the Paypal transaction.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_ID,
				"You must send **only** the transaction id for a verification to be made.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_ID_SUCCESS,
				"Your request has been taken into account, an administrator will quickly verify the payment.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_SUCCESS, "You have just received the role for the plugin %plugin%");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_GIVE, "Unable to prove that you purchased the %plugin% plugin.");

		messages.put(LangType.FR, frMessage);
		messages.put(LangType.US, usMessage);
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
	public String getMessage(LangType type, Message message) {
		return messages.get(type).getOrDefault(message, "Message not found");
	}

}

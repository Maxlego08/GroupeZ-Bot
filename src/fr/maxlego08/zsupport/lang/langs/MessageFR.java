package fr.maxlego08.zsupport.lang.langs;

import java.util.HashMap;
import java.util.Map;

import fr.maxlego08.zsupport.lang.Message;

public class MessageFR {

	public Map<Message, String> getMessages() {
		// FR
		Map<Message, String> frMessage = new HashMap<>();
		frMessage.put(Message.TICKET_ALREADY_CREATE,
				"**Attention!** %user% tu as d�j� cr�� un ticket ici, inutile d'enfaire un autre.");
		frMessage.put(Message.TICKET_DESC,
				"Bienvenue dans votre ticket, un membre de l'�quipe  viendra r�pondre � vos besoins sous peu.");
		frMessage.put(Message.TICKET_PLUGIN_CHOOSE, "Clique sur le plugin pour lequel tu souhaites obtenir de l'aide:");
		frMessage.put(Message.TICKET_PLUGIN, "Vous venez de choisir le plugin **%plugin%** %pluginEmote%.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE,
				"Vous avez d�j� confirm� l'achat du plugin, vous pouvez directement poser votre question.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR,
				"Vous n'avez pas confirm� l'achat du plugin. Une verification est en cours.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_ID,
				"Vous devez envoyer **uniquement** l'id de la transaction pour qu'une v�rification soit faite.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ID_SUCCESS,
				"Votre demande a bien �t� prise en compte, un administrateur va rapidement v�rifier le paiement.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_SUCCESS, "Vous venez de re�evoir le r�le pour le plugin %plugin%");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_GIVE,
				"Impossible de prouver que vous avez acheter le plugin %plugin%.");

		frMessage.put(Message.TICKET_CREATE_ERROR,
				":x: Impossible de cr�er votre ticket, veuillez v�rifier que votre compte discord est correctement reli� au site.");
		frMessage.put(Message.TICKET_CREATE_SUCCESS, "Votre ticket est disponible ici : ");
		frMessage.put(Message.TICKET_CREATE_WAIT,
				"V�rification de la liaison de votre compte discord en cours, veuillez patienter.");
		return frMessage;
	}

}

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

		frMessage.put(Message.TICKET_ALREADY_CREATE_REPLY, "Vous avez d�j� un ticket ouvert: %s");

		frMessage.put(Message.TICKET_DESC,
				"Bienvenue dans votre ticket, un membre de l'�quipe  viendra r�pondre � vos besoins sous peu.");
		frMessage.put(Message.TICKET_PLUGIN_CHOOSE, "Clique sur le plugin pour lequel tu souhaites obtenir de l'aide:");
		frMessage.put(Message.TICKET_PLUGIN, "Vous venez de choisir le plugin **%s** %s.");
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
				":x: Impossible de cr�er votre ticket, veuillez v�rifier que votre compte discord est correctement reli� au site.\nhttps://groupez.dev/dashboard/account");
		frMessage.put(Message.TICKET_CREATE_SUCCESS, "Votre ticket est disponible ici : ");
		frMessage.put(Message.TICKET_CREATE_WAIT,
				"V�rification de la liaison de votre compte discord en cours, veuillez patienter.");

		frMessage.put(Message.TICKET_WELCOME, "Bienvenue %s");
		frMessage.put(Message.TICKET_PLUGIN_ERROR, "Une erreur est survenue, merci de contacter l'�quipe.");

		frMessage.put(Message.TICKET_CLOSE_BUTTON, "Fermer le ticket");

		frMessage.put(Message.TICKET_OTHER, "Veuillez d�tailler la demande de votre ticket.");
		frMessage.put(Message.TICKET_CLOSE, "Fermure du ticket dans %s seconde%s");

		frMessage.put(Message.OTHER, "Autre");
		frMessage.put(Message.TICKET_OTHER_INFO, "Veuillez choisir le plugin pour lequelle vous avez besoin d'aide.");

		frMessage.put(Message.TICKET_ORDER,
				"Pour pouvoir passer une commande vous devez donner toutes les informations sur le projet." + "\n"
						+ "Comme par exemple:" + "\n" + "- Le nom de votre serveur" + "\n"
						+ "- La version de votre serveur" + "\n" + "- Le d�lais de livraison" + "\n"
						+ "- La description compl�te de votre plugin" + "\n" + "\n" + "Informations:" + "\n"
						+ "Tarif: **20�/h**" + "\n"
						+ "Achat du code source: **50%** du prix (par exemple, pour un plugin � 100� les sources seront vendu � 50�)"
						+ "\n" + "Siret: 88761749600013");

		frMessage.put(Message.TICKET_CHOOSE,
				"Veuillez choisir le type de votre ticket:" + "\n" + "\n" + ":dollar: Pour passer une commande" + "\n"
						+ ":question: Pour demander de l'aide sur un plugin" + "\n" + "\n");

		frMessage.put(Message.TICKET_CHOOSE_ORDER, "Passer une commande");
		frMessage.put(Message.TICKET_CHOOSE_PLUGIN, "Support plugins");

		return frMessage;
	}

}

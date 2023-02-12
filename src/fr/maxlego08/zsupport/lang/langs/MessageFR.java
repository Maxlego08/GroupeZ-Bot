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
						+ ":question: Pour demander de l'aide sur un plugin" + "\n"
						+ "%s Pour demander l'acc�s sur spigot" + "\n");

		frMessage.put(Message.TICKET_CHOOSE_ORDER, "Passer une commande");
		frMessage.put(Message.TICKET_CHOOSE_PLUGIN, "Support plugins");
		frMessage.put(Message.TICKET_CHOOSE_SPIGOT, "Demander l'acc�s sur spigot");

		frMessage.put(Message.TICKET_SPIGOT,
				"Pour demander l'acc�s vous devez fournir le nom de votre compte spigot ainsi que la liste des plugins o� vous voulez l'acc�s.");

		frMessage.put(Message.TICKET_HOUR,
				":exclamation: Attention, il est actuellement **%s:%s** en france."
						+ " Le support est disponible uniquement entre **9 heures** et **20 heures** tout les jours."
						+ " Cependant le support se livre le droit d'�tre moins actif durant les weekends.");

		frMessage.put(Message.TICKET_PLUGIN_VERSION_COMMAND,
				"Pour acc�l�rer le support, veuillez �crire la version du plugin." + "\n"
						+ "Pour obtenir la version du plugin faite la commande suivante: **/%s version**" + "\n"
						+ "Example:");

		frMessage.put(Message.TICKET_PLUGIN_VERSION_CONSOLE,
				"Pour acc�l�rer le support, veuillez �crire la version du plugin." + "\n"
						+ "Pour obtenir la version du plugin regardez dans la console." + "\n" + "Example:");

		frMessage.put(Message.TICKET_PLUGIN_VERSION_ERROR,
				"Vous n'utilisez pas la derni�re version du plugin." + "\n"
						+ "Mettez � jour le plugin, v�rifier si votre probl�me est toujours pr�sent." + "\n"
						+ "Si oui, vous pourrez alors cr�er un nouveau ticket." + "\n\n"
						+ "Fermeture de votre ticket dans **30** secondes.");

		frMessage.put(Message.TICKET_PLUGIN_RULES,
				"\n" + "Merci de ne pas **mentionner** l'�quipe de GroupeZ sous peine de sanction." + "\n"
						+ "N'oubliez pas de d�sactiver les mentions lorsque vous faites une r�ponse !" + "\n" + "\n"
						+ "Si votre ticket ne re�oit aucun message au bout de **48 heures**, il sera automatiquement ferm� !");

		return frMessage;
	}

}

package fr.maxlego08.zsupport.lang.langs;

import java.util.HashMap;
import java.util.Map;

import fr.maxlego08.zsupport.lang.Message;

public class MessageFR {

	public Map<Message, String> getMessages() {
		// FR
		Map<Message, String> frMessage = new HashMap<>();
		frMessage.put(Message.TICKET_ALREADY_CREATE,
				"**Attention!** %user% tu as déjà créé un ticket ici, inutile d'enfaire un autre.");

		frMessage.put(Message.TICKET_ALREADY_CREATE_REPLY, "Vous avez déjà un ticket ouvert: %s");

		frMessage.put(Message.TICKET_DESC,
				"Bienvenue dans votre ticket, un membre de l'équipe  viendra répondre à vos besoins sous peu.");
		frMessage.put(Message.TICKET_PLUGIN_CHOOSE, "Clique sur le plugin pour lequel tu souhaites obtenir de l'aide:");
		frMessage.put(Message.TICKET_PLUGIN, "Vous venez de choisir le plugin **%s** %s.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE,
				"Vous avez déjà confirmé l'achat du plugin, vous pouvez directement poser votre question.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR,
				"Vous n'avez pas confirmé l'achat du plugin. Une verification est en cours.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_ID,
				"Vous devez envoyer **uniquement** l'id de la transaction pour qu'une vérification soit faite.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ID_SUCCESS,
				"Votre demande a bien été prise en compte, un administrateur va rapidement vérifier le paiement.");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_SUCCESS, "Vous venez de reçevoir le rôle pour le plugin %plugin%");
		frMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_GIVE,
				"Impossible de prouver que vous avez acheter le plugin %plugin%.");

		frMessage.put(Message.TICKET_CREATE_ERROR,
				":x: Impossible de créer votre ticket, veuillez vérifier que votre compte discord est correctement relié au site.\nhttps://groupez.dev/dashboard/account");
		frMessage.put(Message.TICKET_CREATE_SUCCESS, "Votre ticket est disponible ici : ");
		frMessage.put(Message.TICKET_CREATE_WAIT,
				"Vérification de la liaison de votre compte discord en cours, veuillez patienter.");

		frMessage.put(Message.TICKET_WELCOME, "Bienvenue %s");
		frMessage.put(Message.TICKET_PLUGIN_ERROR, "Une erreur est survenue, merci de contacter l'équipe.");

		frMessage.put(Message.TICKET_CLOSE_BUTTON, "Fermer le ticket");

		frMessage.put(Message.TICKET_OTHER, "Veuillez détailler la demande de votre ticket.");
		frMessage.put(Message.TICKET_CLOSE, "Fermure du ticket dans %s seconde%s");

		frMessage.put(Message.OTHER, "Autre");
		frMessage.put(Message.TICKET_OTHER_INFO, "Veuillez choisir le plugin pour lequelle vous avez besoin d'aide.");

		frMessage.put(Message.TICKET_ORDER,
				"Pour pouvoir passer une commande vous devez donner toutes les informations sur le projet." + "\n"
						+ "Comme par exemple:" + "\n" + "- Le nom de votre serveur" + "\n"
						+ "- La version de votre serveur" + "\n" + "- Le délais de livraison" + "\n"
						+ "- La description complète de votre plugin" + "\n" + "\n" + "Informations:" + "\n"
						+ "Tarif: **20€/h**" + "\n"
						+ "Achat du code source: **50%** du prix (par exemple, pour un plugin à 100€ les sources seront vendu à 50€)"
						+ "\n" + "Siret: 88761749600013");

		frMessage.put(Message.TICKET_CHOOSE,
				"Veuillez choisir le type de votre ticket:" + "\n" + "\n" + ":dollar: Pour passer une commande" + "\n"
						+ ":question: Pour demander de l'aide sur un plugin" + "\n"
						+ "%s Pour demander l'accès sur spigot" + "\n");

		frMessage.put(Message.TICKET_CHOOSE_ORDER, "Passer une commande");
		frMessage.put(Message.TICKET_CHOOSE_PLUGIN, "Support plugins");
		frMessage.put(Message.TICKET_CHOOSE_SPIGOT, "Demander l'accès sur spigot");

		frMessage.put(Message.TICKET_SPIGOT,
				"Pour demander l'accès vous devez fournir le nom de votre compte spigot ainsi que la liste des plugins où vous voulez l'accès.");

		frMessage.put(Message.TICKET_HOUR,
				":exclamation: Attention, il est actuellement **%s:%s** en france."
						+ " Le support est disponible uniquement entre **9 heures** et **20 heures** tout les jours."
						+ " Cependant le support se livre le droit d'être moins actif durant les weekends.");

		frMessage.put(Message.TICKET_PLUGIN_VERSION_COMMAND,
				"Pour accélérer le support, veuillez écrire la version du plugin." + "\n"
						+ "Pour obtenir la version du plugin faite la commande suivante: **/%s version**" + "\n"
						+ "Example:");

		frMessage.put(Message.TICKET_PLUGIN_VERSION_CONSOLE,
				"Pour accélérer le support, veuillez écrire la version du plugin." + "\n"
						+ "Pour obtenir la version du plugin regardez dans la console." + "\n" + "Example:");

		frMessage.put(Message.TICKET_PLUGIN_VERSION_ERROR,
				"Vous n'utilisez pas la dernière version du plugin." + "\n"
						+ "Mettez à jour le plugin, vérifier si votre problème est toujours présent." + "\n"
						+ "Si oui, vous pourrez alors créer un nouveau ticket." + "\n\n"
						+ "Fermeture de votre ticket dans **30** secondes.");

		frMessage.put(Message.TICKET_PLUGIN_RULES,
				"\n" + "Merci de ne pas **mentionner** l'équipe de GroupeZ sous peine de sanction." + "\n"
						+ "N'oubliez pas de désactiver les mentions lorsque vous faites une réponse !" + "\n" + "\n"
						+ "Si votre ticket ne reçoit aucun message au bout de **48 heures**, il sera automatiquement fermé !");

		return frMessage;
	}

}

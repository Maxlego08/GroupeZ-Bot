package fr.maxlego08.zsupport.lang.langs;

import java.util.HashMap;
import java.util.Map;

import fr.maxlego08.zsupport.lang.Message;

public class MessageEN {

	public Map<Message, String> getMessages() {
		// US
		Map<Message, String> usMessage = new HashMap<>();
		usMessage.put(Message.TICKET_ALREADY_CREATE,
				"**Warning!** %user% you've already opened a ticket here! No need to try to create another one.");
		usMessage.put(Message.TICKET_ALREADY_CREATE_REPLY, "You already have an open ticket: %s.");

		usMessage.put(Message.TICKET_DESC,
				"Welcome to your ticket, a member of the team will come to meet your needs shortly.");
		usMessage.put(Message.TICKET_PLUGIN_CHOOSE, "Click on the plugin for which you want help:");
		usMessage.put(Message.TICKET_PLUGIN, "You just choose the plugin **%s** %s.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE,
				"You have already confirmed the purchase of the plugin, you can directly ask your question.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR,
				"You have not confirmed the purchase of the plugin, A check is in progress.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_ID,
				"You must send **only** the transaction id for a verification to be made.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_ID_SUCCESS,
				"Your request has been taken into account, an administrator will quickly verify the payment.");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_SUCCESS, "You have just received the role for the plugin %plugin%");
		usMessage.put(Message.TICKET_PLUGIN_ROLE_ERROR_GIVE, "Unable to prove that you purchased the %plugin% plugin.");

		usMessage.put(Message.TICKET_CREATE_ERROR,
				":x: Unable to create your ticket, please check that your discord account is correctly linked to the site.\nhttps://groupez.dev/dashboard/account");
		usMessage.put(Message.TICKET_CREATE_SUCCESS, "Your ticket is available here: ");
		usMessage.put(Message.TICKET_CREATE_WAIT,
				"Verification of the connection of your discord account in progress, please wait.");

		usMessage.put(Message.TICKET_WELCOME, "Welcome %s");
		usMessage.put(Message.TICKET_PLUGIN_ERROR, "An error has occurred, please contact the team.");

		usMessage.put(Message.TICKET_CLOSE_BUTTON, "Close ticket");

		usMessage.put(Message.TICKET_OTHER, "Please detail your ticket request.");
		usMessage.put(Message.TICKET_CLOSE, "Ticket closes in %s seconds%s");

		usMessage.put(Message.OTHER, "Other");
		usMessage.put(Message.TICKET_OTHER_INFO, "Please choose the plugin for which you need help.");

		usMessage.put(Message.TICKET_ORDER,
				"In order to place an order you must provide all the information about the project." + "\n"
						+ "For example:" + "\n" + "- The name of your server" + "\n" + "- The version of your server"
						+ "\n" + "- Delivery time" + "\n" + "- The complete description of your plugin" + "\n" + "\n"
						+ "Informations:" + "\n" + "Rate: **20€/h**" + "\n"
						+ "Purchase of the source code: **50%** of the price (for example, for a 100€ plugin the sources will be sold at 50€)"
						+ "\n" + "Siret: 88761749600013");

		usMessage.put(Message.TICKET_CHOOSE,
				"Please choose the type of your ticket:" + "\n" + "\n" + ":dollar: To take an order" + "\n"
						+ ":question: To request help on a plugin" + "\n" + "%s To request access on spigot" + "\n");

		usMessage.put(Message.TICKET_CHOOSE_ORDER, "Take an order");
		usMessage.put(Message.TICKET_CHOOSE_PLUGIN, "Plugin support");
		usMessage.put(Message.TICKET_CHOOSE_SPIGOT, "Request access on spigot");

		usMessage.put(Message.TICKET_SPIGOT,
				"To request access you need to provide the name of your spigot account as well as the list of plugins where you want access.");

		usMessage.put(Message.TICKET_HOUR,
				":exclamation: Attention, it is currently **%s:%s** in France. The support is available only between **9 hours** and **20 hours** every day. However the support reserves the right to be less active during the weekends.");

		usMessage.put(Message.TICKET_PLUGIN_VERSION_COMMAND,
				"To speed up support, please write the version of the plugin. To get the version of the plugin do the following command: /%s version");

		usMessage.put(Message.TICKET_PLUGIN_VERSION_CONSOLE,
				"To speed up support, please write the plugin version. To get the plugin version look in the console.");

		usMessage.put(Message.TICKET_PLUGIN_VERSION_ERROR,
				"You are not using the latest version of the plugin." + "\n"
						+ "Update the plugin, check if your problem is still present." + "\n"
						+ "If so, you can create a new ticket." + "\n\n" + "Closing of your ticket in **30** seconds.");

		usMessage.put(Message.TICKET_PLUGIN_RULES, "\n"
				+ "Please do not **mention** the GroupeZ team under penalty of punishment." + "\n"
				+ "Don't forget to disable mentions when you make a reply!" + "\n" + "\n"
				+ "If your ticket does not receive any messages after **48 hours**, it will be automatically closed !");

		return usMessage;

	}
}

package fr.maxlego08.zsupport.utils;

public enum Message {

	COMMAND_NOT_FOUND(":x: Impossible de trouver la commande demand�."),
	COMMAND_NO_CONSOLE(":x: Vous devez �tre un joueur pour pouvoir �xecuter cette commande."),
	COMMAND_NO_PLAYER(":x: Seul la console peut �xecuter cette commande."),
	COMMAND_NO_PERMISSION(":x: Vous n'avez pas la permission d'�xecuter cette commande."),
	COMMAND_SYNTAXE_ERROR(":x: Vous devez ex�cuter la commande comme ceci: %s"),
	
	;

	private String message;

	private Message(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String toMsg() {
		return message;
	}

	public String msg() {
		return message;
	}
	
}

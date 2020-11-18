package fr.maxlego08.zsupport.utils;

public enum Message {

	COMMAND_NOT_FOUND(":x: Impossible de trouver la commande demandé."),
	COMMAND_NO_CONSOLE(":x: Vous devez être un joueur pour pouvoir éxecuter cette commande."),
	COMMAND_NO_PLAYER(":x: Seul la console peut éxecuter cette commande."),
	COMMAND_NO_PERMISSION(":x: Vous n'avez pas la permission d'éxecuter cette commande."),
	COMMAND_SYNTAXE_ERROR(":x: Vous devez exécuter la commande comme ceci: %s"),
	
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

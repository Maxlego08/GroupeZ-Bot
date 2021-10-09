package fr.maxlego08.zsupport.utils;

public enum Message {

	COMMAND_NOT_FOUND(":x: Unable to find the requested order."),
	COMMAND_NO_CONSOLE(":x: You must be a player to be able to execute this command.."),
	COMMAND_NO_PLAYER(":x: Only the console can execute this command.."),
	COMMAND_NO_PERMISSION(":x: You do not have permission to execute this command.."),
	COMMAND_SYNTAXE_ERROR(":x: You have to execute the command like this: %s"),
	
	VERIFY_ERROR(":x: Unable to find you on https://groupez.dev. Please check if your discord is correct."),
	VERIFY_ERROR_EMPTY(":x: You have not purchased any plugin."),
	VERIFY_ERROR_ALREADY(":x: You already have roles from your purchased plugins."),
	
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

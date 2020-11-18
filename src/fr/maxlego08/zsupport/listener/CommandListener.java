package fr.maxlego08.zsupport.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.api.DiscordConsoleSender;
import fr.maxlego08.zsupport.api.DiscordPlayer;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.commands.ConsoleSender;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter implements Constant, Runnable {

	private final ZSupport instance;
	private boolean isRunning;
	private final Scanner scanner = new Scanner(System.in);

	public CommandListener(ZSupport instance) {
		super();
		this.instance = instance;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		User user = event.getAuthor();
		Member member = event.getMember();
		Message message = event.getMessage();
		String command = message.getContentDisplay();

		if (command.startsWith(COMMAND_PREFIX)) {

			command = command.replaceFirst(COMMAND_PREFIX, "");

			String commands = command.split(" ")[0];
			command = command.replaceFirst(commands, "");
			String[] args = command.length() != 0 ? get(command.split(" ")) : new String[0];
			PlayerSender sender = new DiscordPlayer(user, member, event.getTextChannel());
			instance.getCommandManager().onCommand(sender, commands, args, event);
			event.getMessage().delete().queue();
		}

	}

	private String[] get(String[] tableau) {
		List<String> test = new ArrayList<>();
		for (int a = 1; a != tableau.length; a++)
			test.add(tableau[a]);
		return test.toArray(new String[0]);
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			if (scanner.hasNextLine()) {

				String message = scanner.nextLine();
				String commande = message.split(" ")[0];
				message = message.replaceFirst(message.split(" ")[0], "");

				String[] args = message.length() != 0 ? message.split(" ") : new String[0];
				ConsoleSender sender = new DiscordConsoleSender();
				instance.getCommandManager().onCommand(sender, commande, args, null);
			}
		}
		scanner.close();
		instance.onDisable();

		System.out.println(PREFIX_CONSOLE + "Disconnect !");
		System.exit(0);
	}

}

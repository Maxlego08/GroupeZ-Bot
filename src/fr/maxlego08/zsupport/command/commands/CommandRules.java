package fr.maxlego08.zsupport.command.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class CommandRules extends VCommand {

	public CommandRules(CommandManager commandManager) {
		super(commandManager);
		this.consoleCanUse = false;
		this.permission = Permission.ADMINISTRATOR;
		this.description = "Afficher les régles du discords";
	}

	@Override
	protected CommandType perform(ZSupport main) {

		sendRules();
		sendFreeInfo();
		sendInfo();

		return CommandType.SUCCESS;
	}

	private void sendFreeInfo() {

		EmbedBuilder builder = new EmbedBuilder();

		builder.setColor(new Color(40, 221, 237));
		builder.setDescription(
				"**FREE PLUGIN**: The support of free plugins is done only in the dedicated channels. It's a community support, if the staff doesn't answer you, just wait. Don't hesitate to ask your question again or to look if someone already asked this question.");

		this.textChannel.sendMessageEmbeds(builder.build()).queue();

	}

	private void sendInfo() {

		EmbedBuilder builder = new EmbedBuilder();

		builder.setColor(new Color(40, 221, 237));
		builder.setDescription(
				"**PLEASE NOTE**: If you do not respect the rules of the discord you risk sanctions that can go from a simple mute to a permanent banishment from the discord.");

		this.textChannel.sendMessageEmbeds(builder.build()).queue();

	}

	private void sendRules() {
		List<String> rules = new ArrayList<String>();
		rules.add(
				"It is forbidden to **mention** a staff member (Remember to disable mentions when replying to a message).");
		rules.add(
				"You can't get support for a paid plugin if you didn't buy it. You have to show a proof of purchase.");
		rules.add("It is forbidden to go to the buyers of the plugins to ask them for help.");
		rules.add(
				"If you threaten to put up a bad review for any reason, you will be banned directly. Any threat because you are unhappy will result in the same punishment.");
		rules.add("Do not insult a player(s) in public.");
		rules.add("Do not advertise your discords and servers.");
		rules.add("Do not post inappropriate content.");
		rules.add("Do not capitalize phrases.");
		rules.add("Don't spam/flood to keep the chat clean.");
		rules.add("Do not post private/IRL things without the permission of the person concerned.");
		rules.add("Don't impersonate someone else.");
		rules.add("Not to have a nickname that contains inappropriate comments.");
		rules.add("Do not have a pornographic character in your nickname.");
		rules.add("For any information or rankrequest for a plugin please create a ticket.");

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Discord Rules");
		builder.setThumbnail(this.guild.getIconUrl());
		builder.setColor(new Color(40, 221, 237));
		builder.setFooter("2023 - " + this.guild.getName(), this.guild.getIconUrl());

		for (int a = 0; a != rules.size(); a++) {
			int index = a + 1;
			builder.addField("`Article " + index + "`", rules.get(a), false);
		}

		this.textChannel.sendMessageEmbeds(builder.build()).queue();
	}

}

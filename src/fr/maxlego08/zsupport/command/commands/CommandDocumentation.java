package fr.maxlego08.zsupport.command.commands;

import java.awt.Color;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class CommandDocumentation extends VCommand {

	public CommandDocumentation(CommandManager commandManager) {
		super(commandManager);
		consoleCanUse = false;
		onlyInCommandChannel = false;
	}

	@Override
	protected CommandType perform(ZSupport main) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Server Info");
		builder.setColor(Color.getHSBColor(45, 45, 45));
		builder.setFooter("2021 - " + guild.getName(), guild.getIconUrl());
		builder.setDescription("zAuctionHouse documentation: https://zauctionhouse.groupez.dev" + "\n"
				+ "zShop documentation: https://github.com/Maxlego08/zShop-API/wiki" + "\n"
				+ "GroupeZ website documentation: https://docs.groupez.dev");

		this.textChannel.sendTyping().queue();
		
		this.textChannel.sendMessageEmbeds(builder.build()).queue(m -> {

				});

		builder.clear();
		return CommandType.SUCCESS;
	}

}

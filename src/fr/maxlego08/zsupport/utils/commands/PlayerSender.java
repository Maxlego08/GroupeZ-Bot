package fr.maxlego08.zsupport.utils.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public interface PlayerSender extends Sender {

	User getUser();
	
	Member getMember();
	
	TextChannel getTextChannel();
	
}

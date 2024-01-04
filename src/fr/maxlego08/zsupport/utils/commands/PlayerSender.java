package fr.maxlego08.zsupport.utils.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public interface PlayerSender extends Sender {

	User getUser();
	
	Member getMember();
	
	MessageChannel getTextChannel();
	
}

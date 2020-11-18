package fr.maxlego08.zsupport.utils.commands;

import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.Message;
import net.dv8tion.jda.api.Permission;

public interface Sender extends Constant{

	long getID();
	
	String getName();
	
	void sendMessage(String message);
	
	void sendMessage(Message message);
	
	void sendMessage(Message message, boolean delete, Object... args);

	boolean hasPermission(Permission permission);
	
}

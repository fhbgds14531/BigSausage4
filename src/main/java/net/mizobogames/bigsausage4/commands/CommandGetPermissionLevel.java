package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.mizobogames.bigsausage4.BigSausage;

import java.util.Arrays;
import java.util.List;

public class CommandGetPermissionLevel extends CommandBase{

	public CommandGetPermissionLevel(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		List<String> args = Arrays.asList(triggerMessage.getContentRaw().toLowerCase().split(" "));
		if(args.size() == 2){
			EnumPermissionLevel level = BigSausage.getFileManager().getPermissionsForUserInGuild(triggerMessage.getGuild(), triggerMessage.getAuthor());
			sendMessageToChannel(triggerMessage.getTextChannel(), "Your permission level is " + level.toString().toLowerCase().replace("_", "-"));
		}else if(args.size() == 3){
			User user = triggerMessage.getGuild().getMemberById(args.get(2).replace("<", "").replace("!", "").replace("@", "").replace(">", "")).getUser();
			EnumPermissionLevel level = BigSausage.getFileManager().getPermissionsForUserInGuild(triggerMessage.getGuild(), user);
			sendMessageToChannel(triggerMessage.getTextChannel(), "The specified user's permission level is " + level.toString().toLowerCase().replace("_", "-"));
		}else{
			sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid number of arguments!");
		}
	}
}

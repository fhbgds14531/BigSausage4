package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;

import java.util.Arrays;
import java.util.List;

public class CommandHelp extends CommandBase{
	public CommandHelp(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel permissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, permissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		List<String> args = Arrays.asList(triggerMessage.getContentRaw().toLowerCase().split(" "));
		if(args.size() < 3){
			sendMessageToChannel(triggerMessage.getTextChannel(), this.getHelpText());
			return;
		}
		CommandBase command = BigSausage.commands.getCommandByTrigger(args.get(2));
		if(command != null){
			sendMessageToChannel(triggerMessage.getTextChannel(), command.getHelpText());
		}
	}
}

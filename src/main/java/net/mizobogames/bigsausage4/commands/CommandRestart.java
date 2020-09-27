package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;

import java.io.IOException;

public class CommandRestart extends CommandBase{

	public CommandRestart(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		try {
			triggerMessage.getTextChannel().sendMessage("Restarting...").queue();
			Runtime.getRuntime().exec("cmd /c start \"\" restart.bat");
			BigSausage.shutdown();
		} catch (IOException e) {
			BigSausage.reporter.reportAndPrintError(e);
		}
	}
}

package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Util;
import net.mizobogames.bigsausage4.io.AuditLogger;
import net.mizobogames.bigsausage4.io.FileManager;

public class CommandResetPermissions extends CommandBase{

	public CommandResetPermissions(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		AuditLogger.auditGloballyAndForGuild(triggerMessage.getGuild(), "User " + Util.getDisplayNameAndIdForUser(triggerMessage.getAuthor()) + " reset permissions in guild " +
				Util.getDisplayNameAndIdForGuild(triggerMessage.getGuild()));
		BigSausage.getFileManager().initPermissionsForGuild(triggerMessage.getGuild());
		sendMessageToChannel(triggerMessage.getTextChannel(), "Reset all permissions for this server.");
	}
}

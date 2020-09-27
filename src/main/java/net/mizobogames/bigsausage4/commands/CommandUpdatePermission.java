package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Util;
import net.mizobogames.bigsausage4.io.AuditLogger;

import java.util.Arrays;
import java.util.List;

public class CommandUpdatePermission extends CommandBase{

	public CommandUpdatePermission(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		List<String> args = Arrays.asList(triggerMessage.getContentRaw().toLowerCase().replace("!", "").split(" "));
		if(args.size() != 4){
			sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid number of arguments!");
			return;
		}else{
			Member author = triggerMessage.getMember();
			EnumPermissionLevel authorLevel = BigSausage.getFileManager().getPermissionsForUserInGuild(triggerMessage.getGuild(), author.getUser());
			Member target = triggerMessage.getGuild().getMemberById(args.get(2).replace("<", "").replace(">", "").replace("@", "").replace("!", ""));
			EnumPermissionLevel level = EnumPermissionLevel.fromString(args.get(3));
			if(level.getLevel() >= authorLevel.getLevel()){
				sendMessageToChannel(triggerMessage.getTextChannel(), "You cannot elevate another user's permissions higher than one level below yours!");
				AuditLogger.addToAuditLogForGuild(triggerMessage.getGuild(), "User " + Util.getDisplayNameAndIdForUser(author.getUser()) + " tried to elevate another user (" +
						Util.getDisplayNameAndIdForUser(target.getUser()) + ")'s permissions above their own! (" +
						BigSausage.getFileManager().getPermissionsForUserInGuild(triggerMessage.getGuild(), target.getUser()).toString() + " -> " + level.toString() + ")");
				return;
			}
			BigSausage.getFileManager().updateUserPermissionsForGuild(target.getUser(), triggerMessage.getGuild(), level);
			AuditLogger.addToAuditLogForGuild(triggerMessage.getGuild(), "User " + Util.getDisplayNameAndIdForUser(triggerMessage.getAuthor()) + " updated permission level in guild " +
					Util.getDisplayNameAndIdForGuild(triggerMessage.getGuild()) + " to " + level.toString());
			sendMessageToChannel(triggerMessage.getTextChannel(), "Updated permission level.");
		}
	}
}

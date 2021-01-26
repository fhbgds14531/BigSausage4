package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.BigSausage.CommandShutdown;
import net.mizobogames.bigsausage4.Util;
import net.mizobogames.bigsausage4.commands.rolls.CommandRoll;
import org.apache.commons.collections4.list.TreeList;

import java.util.*;
import java.util.Map.Entry;

public class Commands{

	private static Map<String, CommandBase> commands = new HashMap<>();

	public Commands(){
		initCommands();
	}

	private void initCommands(){
		addCommand(new CommandHelp("help", "help", "displays help text for a command.",
									 "Use %p %n <command name> to get help for a specific command. Use %p commands for a list of commands.", "%p %n <command>",
									 EnumPermissionLevel.NONE));
		addCommand(new CommandCommands("commands", "commands", "lists all the available commands", "Use %p %n to get a list of all available commands", "%p %n", EnumPermissionLevel.NONE));
		addCommand(new CommandShutdown("shutdown", "sd", "shuts down the bot", "shuts down the bot", "%p %t",
										 EnumPermissionLevel.BOT_CREATOR));
		addCommand(new CommandUploadFile("upload", "upload", "uploads a file to the bot to be linked",
										   "Use %p %n in the textbox when you upload an image or audio file to upload it to the bot.", "%p %n", EnumPermissionLevel.ELEVATED));
		addCommand(new CommandImage("image", "image", " links one or more images", "Use %p %n <image name> to link a specific image, " +
				"or %p %n to link a random image", "%p %n <one or more names>", EnumPermissionLevel.MEDIUM));
		addCommand(new CommandVoice("voice", "voice", " plays one or more voice clips", "Use %p %n <clip name> to play a specific clip, " +
				"or %p %n to play a random clip", "%p %n <one or more names>", EnumPermissionLevel.MEDIUM));
		addCommand(new CommandListFiles("list-files", "list", " lists the name of all files", "Use %p %n <images | audio> to list item of the " +
				"specified type, or %p %n to list everything", "%p %n <desired type>", EnumPermissionLevel.MEDIUM));
		addCommand(new CommandAddTts("add-tts", "add-tts", "Adds a line of text to the tts repository.", "Use %p %n <text> to add text to the database",
									   "%p %n <text>", EnumPermissionLevel.ELEVATED));
		addCommand(new CommandTts("tts", "tts", "Sends a random tts from the database.", "Use %p %n to get a random tts from the ones that "+
				"have been added", "%p %n", EnumPermissionLevel.MEDIUM));
		addCommand(new CommandResetPermissions("reset-permissions", "reset-all-permissions-in-this-server", "Resets all permission levels for all users "+
				"in this server to their default values.", "Use %p %n to reset all permissions.", "%p %n", EnumPermissionLevel.SERVER_OWNER));
		addCommand(new CommandUpdateSetting("update-setting", "update-setting", "Changes a setting for this server.", "Use %p %n <setting-name> "+
				"<value> to update a setting. Use %p %n to get a list of setting names.", "%p %n <setting-name> <value (\"enabled\", \"disabled\", or a positive integer)>", EnumPermissionLevel.ADMIN));
		addCommand(new CommandUpdatePermission("update-permission", "update-permission", "Updates the specified user's permission level in this server." ,
											   "Use %p %n <User mention> <permission level> to update a user's permission level.", "%p %n <@user> <permission level>", EnumPermissionLevel.ADMIN));
		addCommand(new CommandGetPermissionLevel("get-permission-level", "get-permission-level", "Get your own, or another user's permission level" +
				" in this server", "Use %p %n to get your own permission level, or %p %n <@user> to get the level of another user.", "%p %n <@user>", EnumPermissionLevel.MEDIUM));
		addCommand(new CommandStatus("status", "status", "Get the status of the bot's settings fot this guild.",
									 "Get the status of the bot's settings fot this guild.","%p %n", EnumPermissionLevel.MEDIUM));
		addCommand(new CommandRemoveFile("remove-file", "remove-file", "Removes a specified file.", "Use %p %n <name> to remove a file.",
										 "%p %n <filename>", EnumPermissionLevel.ADMIN));
		addCommand(new CommandUpdate("update", "update", "update", "update", "%p %n", EnumPermissionLevel.BOT_CREATOR));
		addCommand(new CommandRestart("restart", "restart", "restart", "restart", "%p %n", EnumPermissionLevel.BOT_CREATOR));
		addCommand(new CommandClear("clear", "clear", "Removes messages from and to the bot within the last 50 messages.", "Use %p %n to clear messages",
									"%p %n", EnumPermissionLevel.ELEVATED));
		addCommand(new CommandBroadcast("broadcast", "broadcast", "broadcast a message to every guild", "broadcast",
										"%p %n <message>", EnumPermissionLevel.BOT_CREATOR));
		addCommand(new CommandBugreport("bugreport", "bugreport", "Sends a bug report to the official channel, the report includes your user ID and name in case you need to be contacted. ",
				"Use: %p %n <description of what went wrong> to send a bug report.", "%p %n <description>", EnumPermissionLevel.MEDIUM));
		addCommand(new CommandRoll("roll", "roll", "Rolls dice and replies with the output.", "The syntax for rolls is \"XdY(+/-)Z\"" +
				" where X is the number of dice, Y is the number of sides on each die, and Z is a modifier for the roll (Z is optional).", "%p %n XdY<+/- Z>", EnumPermissionLevel.MEDIUM));
	}

	public CommandBase getCommandByTrigger(String trigger){
		if(commands.containsKey(trigger)){
			return commands.get(trigger);
		}
		return getCommandByTrigger("help");
	}

	public static class CommandCommands extends CommandBase {

		public CommandCommands(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel permissionLevel){
			super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, permissionLevel);
		}

		@Override
		public void execute(Message triggerMessage){
			TreeList<String> sortedCommands = new TreeList<>();
			Map<EnumPermissionLevel, List<CommandBase>> filteredCommands = Commands.getCommandsByPermissionLevel();
			EnumPermissionLevel authorLevel = BigSausage.getFileManager().getPermissionsForUserInGuild(triggerMessage.getGuild(), triggerMessage.getAuthor());
			for(EnumPermissionLevel level : filteredCommands.keySet()){
				if(level.getLevel() <= authorLevel.getLevel()){
					for(CommandBase command : filteredCommands.get(level)){
						sortedCommands.add(command.getName());
					}
				}
			}
			List<String> reversedCommands = new LinkedList<>(sortedCommands);
			Collections.reverse(reversedCommands);
			List<String> formatted = Util.formatListIntoEqualSpacedLinesWithCommas(reversedCommands, 6);
			StringBuilder names = new StringBuilder("Here are all the commands you have permission to use:```");
			for(String line : formatted){
				names.append("\n");
				names.append(line);
			}
			names.append("```");
			sendReply(triggerMessage, names.toString());
		}
	}

	public static Map<EnumPermissionLevel, List<CommandBase>> getCommandsByPermissionLevel(){
		Map<EnumPermissionLevel, List<CommandBase>> map = new HashMap<>();
		for(EnumPermissionLevel level : EnumPermissionLevel.values()){
			map.put(level, new ArrayList<>());
		}
		for(Entry<String, CommandBase> e : commands.entrySet()){
			map.get(e.getValue().getPermissionLevel()).add(e.getValue());
		}
		return map;
	}

	private static void addCommand(CommandBase c){
		commands.put(c.getTriggerString(), c);
	}
}

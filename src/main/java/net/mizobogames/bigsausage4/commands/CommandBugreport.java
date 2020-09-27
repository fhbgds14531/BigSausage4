package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Util;

import java.util.Arrays;
import java.util.List;

public class CommandBugreport extends CommandBase{

	public CommandBugreport(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		List<String> command = Arrays.asList(triggerMessage.getContentRaw().split(" "));
		String message = "";
		message += "User: " + Util.getDisplayNameAndIdForUser(triggerMessage.getAuthor()) + " " + triggerMessage.getAuthor().getAsMention() + "\n";
		message += "Server: " + Util.getDisplayNameAndIdForGuild(triggerMessage.getGuild()) + ": Owned by: " + Util.getDisplayNameAndIdForUser(triggerMessage.getGuild().getOwner().getUser()) + ", " +
				triggerMessage.getGuild().getMembers().size() + " members.\n";
		message += "Description: ";
		for(String s : command){
			message += s + " ";
		}
		message = message.replace(BigSausage.PREFIX + " " + this.getTriggerString() + " ", "");
		message += "\n";
		Guild supportGuild = BigSausage.jda.getGuildById(382053109788049429L);
		TextChannel reportChannel = supportGuild.getTextChannelById(382053168042737674L);
		reportChannel.sendMessage(message.trim()).queue();
		sendReply(triggerMessage, "Your message has been received, thank you for your feedback! We may be in touch with you if we need more information.");
	}
}

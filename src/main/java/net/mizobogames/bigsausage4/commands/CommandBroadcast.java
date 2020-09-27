package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;

import java.util.ArrayList;
import java.util.List;

public class CommandBroadcast extends CommandBase{

	public CommandBroadcast(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		String message = triggerMessage.getContentRaw().replace(BigSausage.PREFIX + " broadcast ", "");
		String targetGuildID = triggerMessage.getContentDisplay().split(" ")[2];
		List<Guild> targets = new ArrayList<>();
		if(targetGuildID.toLowerCase().contentEquals("all")){
			for(Guild guild : BigSausage.jda.getGuilds()){
				targets.add(guild);
			}
		}else{
			Guild targetGuild = BigSausage.jda.getGuildById(targetGuildID);
			if(targetGuild == null){
				sendReply(triggerMessage, "Unknown guild. Check that you got the right ID.");
				return;
			}
			targets.add(targetGuild);
		}
		message = message.replace(targetGuildID + " ", "");
		for(Guild guild : targets){
			if(guild.getIdLong() != triggerMessage.getGuild().getIdLong()){
				guild.getDefaultChannel().sendMessage(message).queue();
			}
		}
		sendReply(triggerMessage, "Message sent!");
	}
}

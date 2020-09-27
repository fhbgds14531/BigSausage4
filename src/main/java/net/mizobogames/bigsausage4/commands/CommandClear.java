package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Util;
import net.mizobogames.bigsausage4.io.AuditLogger;

import java.util.ArrayList;
import java.util.List;

public class CommandClear extends CommandBase {

	public CommandClear(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		int range = 50;
		MessageHistory history = triggerMessage.getTextChannel().getHistory();
		List<Message> retrievedMessages = history.retrievePast(range).complete();
		List<Message> messagesToDelete = new ArrayList<>();

		messagesToDelete.add(retrievedMessages.get(0));
		for(int i = 1; i < range; i++){
			Message m = retrievedMessages.get(i);
			if(m.getAuthor().getIdLong() == BigSausage.jda.getSelfUser().getIdLong() || m.getContentRaw().startsWith(BigSausage.PREFIX) || m.getContentDisplay().startsWith("@BigSausage") ||
					m.getContentDisplay().startsWith("@Big Sausage - Beta")){
				messagesToDelete.add(m);
			}
		}
		if(messagesToDelete.size() > 1){
			triggerMessage.getTextChannel().deleteMessages(messagesToDelete).queue();
			AuditLogger.addToAuditLogForGuild(triggerMessage.getGuild(), Util.getDisplayNameAndIdForUser(triggerMessage.getAuthor()) + " deleted " + messagesToDelete.size() + " messages in guild " +
					Util.getDisplayNameAndIdForGuild(triggerMessage.getGuild()));
		}else if(messagesToDelete.size() == 1){
			triggerMessage.getTextChannel().deleteMessageById(messagesToDelete.get(0).getId()).queue();
			AuditLogger.addToAuditLogForGuild(triggerMessage.getGuild(), Util.getDisplayNameAndIdForUser(triggerMessage.getAuthor()) + " deleted 1 message in guild " +
					Util.getDisplayNameAndIdForGuild(triggerMessage.getGuild()));
		}else{
			AuditLogger.addToAuditLogForGuild(triggerMessage.getGuild(), Util.getDisplayNameAndIdForUser(triggerMessage.getAuthor()) + " tried to delete messages in guild " +
					Util.getDisplayNameAndIdForGuild(triggerMessage.getGuild()) + " but there were no messages to delete!");
		}
	}
}

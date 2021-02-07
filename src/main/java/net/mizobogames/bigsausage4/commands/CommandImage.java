package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Reporting;
import net.mizobogames.bigsausage4.linking.Linkable;
import net.mizobogames.bigsausage4.linking.Linkable.EnumLinkableType;
import net.mizobogames.bigsausage4.linking.Trigger;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CommandImage extends CommandBase{

	public CommandImage(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		if(Boolean.parseBoolean(BigSausage.settingsManager.getSettingsForGuild(triggerMessage.getGuild()).getProperty("allow_commanded_images"))){
			try{
				Map<Linkable, Integer> objectsToLink = new HashMap<>();
				List<String> args = Arrays.asList(triggerMessage.getContentRaw().split(" "));
				if(args.size() > 2){
					for(Linkable linkable : BigSausage.getFileManager().getLinkablesForGuildOfType(triggerMessage.getGuild(), EnumLinkableType.IMAGE)){
						int count = 0;
						for(Trigger trigger : linkable.getTriggers()){
							for(String s : args){
								if(s.toLowerCase().contentEquals(trigger.getTrigger().toLowerCase())) count++;
							}
						}
						if(count > 0){
							objectsToLink.put(linkable, count);
						}
					}
				}else{
					SecureRandom random = new SecureRandom();
					objectsToLink.put(BigSausage.getFileManager().getLinkablesForGuildOfType(triggerMessage.getGuild(), EnumLinkableType.IMAGE).get(random.nextInt(BigSausage.getFileManager().getLinkablesForGuildOfType(triggerMessage.getGuild(), EnumLinkableType.IMAGE).size())), 1);
				}
				for(Entry<Linkable, Integer> entry : objectsToLink.entrySet()){
					for(int i = entry.getValue(); i > 0; i--){
						triggerMessage.getTextChannel().sendFile(entry.getKey().getLinkedFile()).queue();
					}
				}
			}catch(Exception e){
				Reporting.instance.reportAndPrintError(e);
			}
		}else{
			sendReply(triggerMessage, "That command is currently disabled!");
		}
	}
}

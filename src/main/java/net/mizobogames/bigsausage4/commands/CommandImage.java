package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Thumbnail;
import net.dv8tion.jda.internal.handle.MessageCreateHandler;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Reporting;
import net.mizobogames.bigsausage4.Util;
import net.mizobogames.bigsausage4.linking.Linkable;
import net.mizobogames.bigsausage4.linking.Linkable.EnumLinkableType;
import net.mizobogames.bigsausage4.linking.Trigger;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.Map.Entry;

public class CommandImage extends CommandBase{

	public CommandImage(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		if(BigSausage.getFileManager().getSettingsForGuild(triggerMessage.getGuild()).isCommandedImages()){
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
				Iterator<Entry<Linkable, Integer>> iterator = objectsToLink.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<Linkable, Integer> entry = iterator.next();
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

package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.linking.Linkable;

import java.util.Arrays;
import java.util.List;

public class CommandRemoveFile extends CommandBase{

	public CommandRemoveFile(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		List<String> args = Arrays.asList(triggerMessage.getContentRaw().toLowerCase().split(" "));
		if(args.size() != 3){
			sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid number of arguments!");
		}else{
			String filename = args.get(2);
			Linkable target = null;
			List<Linkable> linkables = BigSausage.getFileManager().getLinkablesForGuild(triggerMessage.getGuild());
			for(Linkable l : linkables){
				if(l.getName().toLowerCase().contentEquals(filename)){
					target = l;
					break;
				}
			}
			if(target != null){
				BigSausage.getFileManager().removeLinkableFromGuild(triggerMessage.getGuild(), target);
				BigSausage.getFileManager().saveLinkables();
				sendReply(triggerMessage, "Removed file \"" + target.getLinkedFile().getName() + "\"");
			}else{
				sendReply(triggerMessage, "The specified file could not be found!");
			}
		}
	}
}

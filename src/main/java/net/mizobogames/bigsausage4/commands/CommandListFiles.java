package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Util;
import net.mizobogames.bigsausage4.linking.Linkable;
import net.mizobogames.bigsausage4.linking.Linkable.EnumLinkableType;

import java.util.ArrayList;
import java.util.List;

public class CommandListFiles extends CommandBase {

	public CommandListFiles(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		boolean bothTypes = false;
		EnumLinkableType typeToLink = null;
		if(triggerMessage.getContentDisplay().split(" ").length == 2){
			bothTypes = true;
		}else if(triggerMessage.getContentDisplay().split(" ").length == 3){
			String arg = triggerMessage.getContentDisplay().split(" ")[2].toLowerCase();
			if(arg.contentEquals("audio") || arg.contentEquals("voice")){
				typeToLink = EnumLinkableType.AUDIO;
			}else if(arg.contentEquals("images") || arg.contentEquals("image")){
				typeToLink = EnumLinkableType.IMAGE;
			}else{
				triggerMessage.getTextChannel().sendMessage("Invalid argument.").queue();
			}
		}else{
			triggerMessage.getTextChannel().sendMessage("Invalid number of arguments.").queue();
		}

		List<Linkable> linkables = BigSausage.getFileManager().getLinkablesForGuild(triggerMessage.getGuild());

		List<Linkable> audio  = new ArrayList<>();
		List<Linkable> images = new ArrayList<>();

		if(typeToLink != null){
			switch(typeToLink){
				case IMAGE:
					sendImages(linkables, triggerMessage.getTextChannel());
					break;
				case AUDIO:
					sendAudio(linkables, triggerMessage.getTextChannel());
			}
		}else{
			sendImages(linkables, triggerMessage.getTextChannel());
			sendAudio(linkables, triggerMessage.getTextChannel());
		}
	}

	private void sendImages(List<Linkable> linkables, TextChannel channel){
		List<Linkable> images = new ArrayList<>();
		for(Linkable linkable : linkables){
			if(linkable.getType() == EnumLinkableType.IMAGE){
				images.add(linkable);
			}
		}
		List<String> imageNames = new ArrayList<String>();
		for(Linkable linkable : images){
			imageNames.add(linkable.getName());
		}
		List<String> formattedImages = Util.formatListIntoEqualSpacedLinesWithCommas(imageNames, 6);
		StringBuilder line = new StringBuilder();
		for(String s : formattedImages){
			line.append(s).append("\n");
		}
		line = new StringBuilder("`" + line + "`");
		line.insert(0, "Images:\n");

		if(images.size() < 1) line = new StringBuilder("There are currently no images.");

		sendMessageToChannel(channel, line.toString());
	}

	private void sendAudio(List<Linkable> linkables, TextChannel channel){
		List<Linkable> audio = new ArrayList<>();
		for(Linkable linkable : linkables){
			if(linkable.getType() == EnumLinkableType.AUDIO){
				audio.add(linkable);
			}
		}
		List<String> audioNames = new ArrayList<String>();
		for(Linkable linkable : audio){
			audioNames.add(linkable.getName());
		}
		List<String> formattedAudio = Util.formatListIntoEqualSpacedLinesWithCommas(audioNames, 6);
		StringBuilder audioLine = new StringBuilder();
		for(String s : formattedAudio){
			audioLine.append(s).append("\n");
		}
		audioLine = new StringBuilder("`" + audioLine + "`");
		audioLine.insert(0, "Audio Clips:\n");

		if(audio.size() < 1) audioLine = new StringBuilder("There are currently no audio clips.");

		sendMessageToChannel(channel, audioLine.toString());
	}
}

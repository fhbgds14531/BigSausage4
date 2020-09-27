package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.GuildSettings;
import net.mizobogames.bigsausage4.Util;

import java.util.Arrays;
import java.util.List;

public class CommandUpdateSetting extends CommandBase{

	public CommandUpdateSetting(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		List<String> args = Arrays.asList(triggerMessage.getContentRaw().toLowerCase().split(" "));
		if(args.size() != 4){
			if(args.size() == 2){
				sendMessageToChannel(triggerMessage.getTextChannel(), "Valid setting names are : ```" +
						"message-parsing-audio		message-parsing-images		commanded-audio			commanded-images\n" +
						"multi-linking				allow-tts-command			 max-queued-clips-per-message```");
				return;
			}
			sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid number of arguments!");
			return;
		}else{
			String setting = args.get(2);
			String value = args.get(3);
			if(!(value.contentEquals("enabled") || value.contentEquals("disabled") || Util.isInteger(value, 10))){
				sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument. Setting values must either be \"enabled\", \"disabled\", or an integer.");
				return;
			}
			GuildSettings settings = BigSausage.getFileManager().getSettingsForGuild(triggerMessage.getGuild());
			switch(setting){
				case "message-parsing-audio":
					if(Util.isInteger(value, 10)){
						sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument! That setting requires a value of either \"enabled\" or \"disabled\".");
						return;
					}
					settings.setMessageParsingAudioTriggers(getState(value));
					break;
				case "message-parsing-images":
					if(Util.isInteger(value, 10)){
						sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument! That setting requires a value of either \"enabled\" or \"disabled\".");
						return;
					}
					settings.setMessageParsingImageTriggers(getState(value));
					break;
				case "commanded-audio":
					if(Util.isInteger(value, 10)){
						sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument! That setting requires a value of either \"enabled\" or \"disabled\".");
						return;
					}
					settings.setCommandedVoiceClips(getState(value));
					break;
				case "commanded-images":
					if(Util.isInteger(value, 10)){
						sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument! That setting requires a value of either \"enabled\" or \"disabled\".");
						return;
					}
					settings.setCommandedImages(getState(value));
					break;
				case "multi-linking":
					if(Util.isInteger(value, 10)){
						sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument! That setting requires a value of either \"enabled\" or \"disabled\".");
						return;
					}
					settings.setAllowMultipleLinkablesPerMessage(getState(value));
					break;
				case "allow-tts-command":
					if(Util.isInteger(value, 10)){
						sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument! That setting requires a value of either \"enabled\" or \"disabled\".");
						return;
					}
					settings.setAllowTtsMessages(getState(value));
					break;
				case "max-queued-clips-per-message":
					if(Util.isInteger(value, 10) && Integer.parseInt(value) > 0){
						settings.setMaxAudioClipsToQueuePerMessage(Integer.parseInt(value));
					}else{
						sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument! That setting requires a positive integer.");
						return;
					}
					break;
				case "all":
					if(Util.isInteger(value, 10)){
						sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument! That setting requires a value of either \"enabled\" or \"disabled\".");
						return;
					}else{
						settings.setMessageParsingAudioTriggers(getState(value));
						settings.setMessageParsingImageTriggers(getState(value));
						settings.setCommandedVoiceClips(getState(value));
						settings.setCommandedImages(getState(value));
						settings.setAllowMultipleLinkablesPerMessage(getState(value));
						settings.setAllowTtsMessages(getState(value));
					}
					break;
				default:
					sendMessageToChannel(triggerMessage.getTextChannel(), "Unknown setting! Valid setting names are : ```" +
							"message-parsing-audio		message-parsing-images		commanded-audio		commanded-images\n" +
							"multi-linking				allow-tts-command			max-queued-clips-per-message		all```");
					break;
			}
			BigSausage.getFileManager().saveSettingsForGuild(settings);
			sendMessageToChannel(triggerMessage.getTextChannel(), "Setting updated!");
		}

	}

	private boolean getState(String s){
		return s.contentEquals("enabled");
	}
}

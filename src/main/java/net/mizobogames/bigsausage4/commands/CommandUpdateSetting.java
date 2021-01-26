package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
						"allow_message_parsing_audio		allow_message_parsing_image		allow_commanded_voice_clips			allow_commanded_images\n" +
						"allow_multi_link				allow_tts			 max_audio_clips_per_message		max_dice_rolls_to_track```");
				return;
			}
			sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid number of arguments!");
		}else{
			String booleanResponse = "Invalid argument! That setting requires a value of either \"enable\" or \"disable\".";
			String integerResponse = "Invalid argument! That setting requires a positive integer.";
			String setting = args.get(2);
			String value = args.get(3);
			if(!(value.contentEquals("enable") || value.contentEquals("disable") || Util.isInteger(value, 10))){
				sendMessageToChannel(triggerMessage.getTextChannel(), "Invalid argument. Setting values must either be \"enable\", \"disable\", or an integer.");
				return;
			}
			Properties guildProperties = BigSausage.settingsManager.getSettingsForGuild(triggerMessage.getGuild());

			switch(setting){
				case "allow_message_parsing_audio":
				case "allow_message_parsing_image":
				case "allow_tts":
				case "allow_multi_link":
				case "allow_commanded_images":
				case "allow_commanded_voice_clips":
					if(Util.isInteger(value, 10)){
						sendMessageToChannel(triggerMessage.getTextChannel(), booleanResponse);
						return;
					}
					guildProperties.setProperty(setting, String.valueOf(getState(value)));
					break;
				case "max_audio_clips_per_message":
				case "max_dice_rolls_to_track":
					if(Util.isInteger(value, 10) && Integer.parseInt(value) > 0){
						guildProperties.setProperty(setting, String.valueOf(Integer.parseInt(value)));
					}else{
						sendMessageToChannel(triggerMessage.getTextChannel(), integerResponse);
						return;
					}
					break;
				case "all":
					if(Util.isInteger(value, 10)){
						sendMessageToChannel(triggerMessage.getTextChannel(), booleanResponse);
						return;
					}else{
						guildProperties.setProperty("allow_message_parsing_audio", String.valueOf(getState(value)));
						guildProperties.setProperty("allow_message_parsing_image", String.valueOf(getState(value)));
						guildProperties.setProperty("allow_tts", String.valueOf(getState(value)));
						guildProperties.setProperty("allow_multi_link", String.valueOf(getState(value)));
						guildProperties.setProperty("allow_commanded_images", String.valueOf(getState(value)));
						guildProperties.setProperty("allow_commanded_voice_clips", String.valueOf(getState(value)));
					}
					break;
				default:
					sendMessageToChannel(triggerMessage.getTextChannel(), "Unknown setting! Valid setting names are : ```" +
							"allow_message_parsing_audio		allow_message_parsing_image		allow_commanded_voice_clips			allow_commanded_images\n" +
							"allow_multi_link				allow_tts			 max_audio_clips_per_message		max_dice_rolls_to_track		all```");
					break;
			}
			sendMessageToChannel(triggerMessage.getTextChannel(), "Setting updated!");
		}

	}

	private boolean getState(String s){
		return s.contentEquals("enabled");
	}
}

package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CommandStatus extends CommandBase{

	public CommandStatus(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		Properties guildProperties = BigSausage.settingsManager.getSettingsForGuild(triggerMessage.getGuild());
		List<String> lines = new ArrayList<>();

		lines.add("allow_message_parsing_audio: " + guildProperties.getProperty("allow_message_parsing_audio"));
		lines.add("allow_message_parsing_image: " + guildProperties.getProperty("allow_message_parsing_image"));
		lines.add("allow_commanded_voice_clips: " + guildProperties.getProperty("allow_commanded_voice_clips"));
		lines.add("allow_commanded_images:      " + guildProperties.getProperty("allow_commanded_images"));
		lines.add("allow_multi_linking:         " + guildProperties.getProperty("allow_multi_linking"));
		lines.add("allow_tts:                   " + guildProperties.getProperty("allow_tts"));
		lines.add("max_audio_clips_per_message: " + guildProperties.getProperty("max_audio_clips_per_message"));
		lines.add("max_dice_rolls_to_track:     " + guildProperties.getProperty("max_dice_rolls_to_track"));

		StringBuilder reply = new StringBuilder("Here is the status of all the settings for this server:```");
		for(String s : lines){
			reply.append("\n").append(s);
		}
		reply.append("```");

		sendMessageToChannel(triggerMessage.getTextChannel(), reply.toString());
	}

	private String getValue(boolean bool){
		return bool ? "enabled" : "disabled";
	}
}

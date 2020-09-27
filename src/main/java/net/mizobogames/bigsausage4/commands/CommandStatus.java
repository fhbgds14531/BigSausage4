package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.GuildSettings;

import java.util.ArrayList;
import java.util.List;

public class CommandStatus extends CommandBase{

	public CommandStatus(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		GuildSettings settings = BigSausage.getFileManager().getSettingsForGuild(triggerMessage.getGuild());
		List<String> lines = new ArrayList<>();

		lines.add("message-parsing-audio:        " + getValue(settings.isMessageParsingAudioTriggers()));
		lines.add("message-parsing-images:       " + getValue(settings.isMessageParsingImageTriggers()));
		lines.add("commanded-audio:              " + getValue(settings.isCommandedVoiceClips()));
		lines.add("commanded-images:             " + getValue(settings.isCommandedImages()));
		lines.add("multi-linking:                " + getValue(settings.isAllowMultipleLinkablesPerMessage()));
		lines.add("allow-tts:                    " + getValue(settings.isAllowTtsMessages()));
		lines.add("max-queued-clips-per-message: " + settings.getMaxAudioClipsToQueuePerMessage());

		String reply = "Here is the status of all the settings for this server:```";
		for(String s : lines){
			reply += "\n" + s;
		}
		reply += "```";

		sendMessageToChannel(triggerMessage.getTextChannel(), reply);
	}

	private String getValue(boolean bool){
		return bool ? "enabled" : "disabled";
	}
}

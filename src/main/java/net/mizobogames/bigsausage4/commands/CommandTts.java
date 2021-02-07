package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;

import java.security.SecureRandom;
import java.util.List;
import java.util.Properties;

public class CommandTts extends CommandBase{

	public CommandTts(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		Properties guildSettings = BigSausage.settingsManager.getSettingsForGuild(triggerMessage.getGuild());
		if(Boolean.parseBoolean(guildSettings.getProperty("allow_tts"))){
			System.out.println(guildSettings);
			List<String> ttsList = BigSausage.getFileManager().getTtsListForGuild(triggerMessage.getGuild());
			if(ttsList.isEmpty()){
				sendMessageToChannel(triggerMessage.getTextChannel(), "The tts file is empty. Try adding some!");
				return;
			}
			MessageBuilder builder = new MessageBuilder();
			builder.setTTS(true);
			builder.setContent(ttsList.get(new SecureRandom().nextInt(ttsList.size())));
			triggerMessage.getTextChannel().sendMessage(builder.build()).queue();
		}else{
			sendReply(triggerMessage, "tts messages are currently disabled!");
		}
	}
}

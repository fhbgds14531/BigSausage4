package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.BigSausage;

public class CommandAddTts extends CommandBase{

	public CommandAddTts(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		String tts = triggerMessage.getContentRaw().replace(BigSausage.PREFIX + " " + this.getTriggerString() + " ", "");
		BigSausage.getFileManager().addTtsForGuild(triggerMessage.getGuild(), tts);
		MessageBuilder builder = new MessageBuilder(tts);
		builder.setTTS(true);
		triggerMessage.getTextChannel().sendMessage(builder.build()).queue();
	}
}

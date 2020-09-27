package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.mizobogames.bigsausage4.BigSausage;

public abstract class CommandBase{

	private String name;
	private String description;
	private String triggerString;
	private String helpText;
	private String usageText;

	private EnumPermissionLevel permissionLevel;

	public CommandBase(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		this.name = commandLongName;
		this.triggerString = commandTriggerString;
		this.description = commandDescription.replace("%p", BigSausage.PREFIX).replace("%n", commandLongName).replace("%t", triggerString);
		this.helpText = helpText.replace("%p", BigSausage.PREFIX).replace("%n", commandLongName).replace("%t", triggerString);
		this.usageText = usageText.replace("%p", BigSausage.PREFIX).replace("%n", commandLongName).replace("%t", triggerString);
		this.permissionLevel = minimumPermissionLevel;
	}

	public abstract void execute(Message triggerMessage);

	public String getName(){
		return this.name;
	}
	public String getDescription(){
		return this.description;
	}
	public String getTriggerString(){
		return triggerString;
	}
	public String getHelpText(){
		if(helpText == null || helpText.isEmpty()){
			this.helpText = "Help text was left empty, sorry. Yell at " + BigSausage.MY_USER.getAsMention();
		}
		String text = "Command: `" + this.getName() + "` " + this.getDescription() + "\n";
		text += "Help: `" + this.helpText + "`\n";
		text += "Usage: `" + this.getUsageText() + "`\n";
		text += "Required Permission Level: `" + this.getPermissionLevel().toString() + "`";
		return text;
	}
	public String getUsageText(){
		return usageText;
	}

	void sendMessageToChannel(TextChannel channel, String message){
		channel.sendMessage(message).queue();
	}

	void sendReply(Message m, String message){
		m.getTextChannel().sendMessage(message).queue();
	}

	void sendReply(Message oldMessage, Message newMessage){
		oldMessage.getTextChannel().sendMessage(newMessage).queue();
	}

	public EnumPermissionLevel getPermissionLevel(){
		return permissionLevel;
	}
}

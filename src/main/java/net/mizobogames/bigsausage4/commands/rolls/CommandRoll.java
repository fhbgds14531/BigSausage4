package net.mizobogames.bigsausage4.commands.rolls;

import net.dv8tion.jda.api.entities.Message;
import net.mizobogames.bigsausage4.commands.CommandBase;
import net.mizobogames.bigsausage4.commands.EnumPermissionLevel;

public class CommandRoll extends CommandBase{

	public CommandRoll(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		DiceRoll roll = new DiceRoll(triggerMessage.getContentDisplay().split(" ")[2]);
		triggerMessage.getTextChannel().sendMessage(roll.roll()).queue();
	}
}

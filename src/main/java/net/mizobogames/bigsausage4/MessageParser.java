package net.mizobogames.bigsausage4;

import net.dv8tion.jda.api.entities.*;
import net.mizobogames.bigsausage4.commands.CommandBase;
import net.mizobogames.bigsausage4.io.AuditLogger;
import net.mizobogames.bigsausage4.io.audio.BSAudioManager;
import net.mizobogames.bigsausage4.linking.Linkable;
import net.mizobogames.bigsausage4.linking.Linkable.EnumLinkableType;
import net.mizobogames.bigsausage4.linking.Trigger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageParser{

	private static Map<Guild, Map<EnumLinkableType, Boolean>> parseForTriggersPerGuild;

	public MessageParser(){
		reloadSettings();
	}

	public static void reloadSettings(){
		parseForTriggersPerGuild = new HashMap<>();
		List<Guild> guilds = BigSausage.jda.getGuilds();
		for(Guild guild : guilds){
			GuildSettings settings = BigSausage.getFileManager().getSettingsForGuild(guild);
			Map<EnumLinkableType, Boolean> guildSettingsMap = new HashMap<>();
			guildSettingsMap.put(EnumLinkableType.AUDIO, settings.isMessageParsingAudioTriggers());
			guildSettingsMap.put(EnumLinkableType.IMAGE, settings.isMessageParsingImageTriggers());
			parseForTriggersPerGuild.put(guild, guildSettingsMap);
		}
	}
	public static boolean parseMessageForCommandAndExecute(Message m){
		try{
			String messageText = m.getContentRaw().toLowerCase();
			String[] messageArgs = messageText.split(" ");
			List<String> argList = Arrays.asList(messageArgs);


			if(!(messageText.startsWith(BigSausage.PREFIX) || m.getContentRaw().replace("!", "").startsWith(BigSausage.jda.getSelfUser().getAsMention()))){
				return false;
			}else{
				if(messageArgs.length == 1){
					System.out.println(messageText);
					BigSausage.commands.getCommandByTrigger("help").execute(m);
					return true;
				}
				CommandBase c = BigSausage.commands.getCommandByTrigger(argList.get(1));
				try{
					if(Util.userHasPermissionInGuild(m.getAuthor(), m.getGuild(), c.getPermissionLevel())){
						c.execute(m);
						String logString = "User \"" + m.getAuthor().getName() + "\" (" + m.getAuthor().getIdLong() + ") executed command \"" + c.getName() + "\" Full command text: \"" + messageText + "\"";
						AuditLogger.addToAuditLogForGuild(m.getGuild(), logString);
						System.out.println(logString);
					}else{
						m.getTextChannel().sendMessage("You don't have permission to use that command.\n" +
															   "Minimum permission level required: `" + c.getPermissionLevel().toString() +
															   "`\n" + "Your permission level:             `" +
															   BigSausage.getFileManager().getPermissionsForUserInGuild(m.getGuild(), m.getAuthor()).toString() + "`");
						AuditLogger.addToAuditLogForGuild(m.getGuild(), "User \"" + m.getAuthor().getName() + "\" (" + m.getAuthor().getIdLong() + ") tried to use a command they didn't have permission for. (" +
								c.getName() + ").");
					}
				}catch(Exception e){
					Reporting.instance.reportAndPrintError(e);
					return false;
				}
				return true;
			}
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
			return false;
		}
	}

	public static void parseMessageForTriggers(Message m){
		List<String> words = Arrays.asList(m.getContentDisplay().toLowerCase().split(" "));
		GuildSettings settings = BigSausage.getFileManager().getSettingsForGuild(m.getGuild());

		List<Linkable> guildLinkables = BigSausage.getFileManager().getLinkablesForGuild(m.getGuild());
		int linkedSoFar = 0;

		for(String word : words){
			boolean foundTrigger = false;
			for(Linkable linkable : guildLinkables){
				for(Trigger t : linkable.getTriggers()){
					if(t.getTrigger().toLowerCase().contentEquals(word)){
						foundTrigger = true;
						break;
					}
				}
				if(foundTrigger){
					foundTrigger = false;
					switch(linkable.getType()){
						case AUDIO:
							if(parseForTriggersPerGuild.get(m.getGuild()).get(EnumLinkableType.AUDIO)){
								User sender = m.getAuthor();
								Member member = m.getGuild().getMember(sender);
								assert member != null;
								GuildVoiceState state = member.getVoiceState();
								assert state != null;
								VoiceChannel voiceChannel = state.getChannel();
								try{
									BSAudioManager.queueFile(linkable.getLinkedFile(), m.getGuild(), voiceChannel, sender, false);
									linkedSoFar++;
									if(!settings.isAllowMultipleLinkablesPerMessage()) return;
									if(linkedSoFar >= settings.getMaxAudioClipsToQueuePerMessage()) return;
								}catch(Exception e){
									BigSausage.reporter.reportAndPrintError(e);
								}
							}
							break;
						case IMAGE:
							if(parseForTriggersPerGuild.get(m.getGuild()).get(EnumLinkableType.IMAGE)){
								m.getTextChannel().sendFile(linkable.getLinkedFile()).queue();
								linkedSoFar++;
								if(!settings.isAllowMultipleLinkablesPerMessage()) return;
							}
							break;
					}
				}
			}
		}
	}

}

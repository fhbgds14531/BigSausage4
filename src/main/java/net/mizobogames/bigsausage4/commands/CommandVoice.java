package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.*;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Reporting;
import net.mizobogames.bigsausage4.Util;
import net.mizobogames.bigsausage4.io.audio.BSAudioManager;
import net.mizobogames.bigsausage4.linking.Linkable;
import net.mizobogames.bigsausage4.linking.Linkable.EnumLinkableType;
import net.mizobogames.bigsausage4.linking.Trigger;

import java.security.SecureRandom;
import java.util.*;
import java.util.Map.Entry;

public class CommandVoice extends CommandBase{

	public CommandVoice(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		if(Boolean.parseBoolean(BigSausage.settingsManager.getSettingsForGuild(triggerMessage.getGuild()).getProperty("allow_commanded_voice_clips"))){
			int maxClips = Integer.parseInt(BigSausage.settingsManager.getSettingsForGuild(triggerMessage.getGuild()).getProperty("max_audio_clips_per_message"));
			try{
				List<Linkable> audioClips = BigSausage.getFileManager().getLinkablesForGuildOfType(triggerMessage.getGuild(), EnumLinkableType.AUDIO);
				if(audioClips.size() == 0){
					sendReply(triggerMessage, "There are no audio clips! Try adding some!");
					return;
				}
				Map<Linkable, Integer> objectsToLink = new LinkedHashMap<>();
				List<String> args = new LinkedList<>(Arrays.asList(triggerMessage.getContentRaw().split(" ")));
				if(args.size() > 2){
					if(Util.isInteger(args.get(2), 10)){
						int numToLink = Integer.parseInt(args.get(2));
						if(numToLink > maxClips) numToLink = maxClips;
						SecureRandom random = new SecureRandom();
						for(int i = 0; i < numToLink; i++){
							objectsToLink.put(audioClips.get(random.nextInt(audioClips.size())), 1);
						}
					}else{
						for(String s : args){
							int count = 0;
							int num = 0;
							for(Linkable linkable : audioClips){
								for(Trigger trigger : linkable.getTriggers()){
									if(s.toLowerCase().contentEquals(trigger.getTrigger().toLowerCase())){
										if(objectsToLink.containsKey(linkable)){
											num = objectsToLink.get(linkable);
											if(num >= maxClips){
												break;
											}
										}else{
											objectsToLink.put(linkable, num + 1);
										}
									}
								}
							}
						}
					}
				}else{
					SecureRandom random = new SecureRandom();
					objectsToLink.put(audioClips.get(random.nextInt(audioClips.size())), 1);
				}
				User sender = triggerMessage.getAuthor();
				Member member = triggerMessage.getGuild().getMember(sender);
				assert member != null;
				GuildVoiceState state = member.getVoiceState();
				assert state != null;
				if(state.inVoiceChannel()){
					VoiceChannel voiceChannel = state.getChannel();
					for(Entry<Linkable, Integer> entry : objectsToLink.entrySet()){
						for(int i = 0; i < entry.getValue(); i++){
							BSAudioManager.queueFile(entry.getKey().getLinkedFile(), triggerMessage.getGuild(), voiceChannel, sender, true);
						}
					}
				}else{
					sendReply(triggerMessage, "You need to be in a voice channel to use that command.");
				}
			}catch(Exception e){
				Reporting.instance.reportAndPrintError(e);
			}
		}else{
			sendReply(triggerMessage, "That command is currently disabled!");
		}
	}
}

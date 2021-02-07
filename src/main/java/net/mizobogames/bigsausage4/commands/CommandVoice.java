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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandVoice extends CommandBase{

	public CommandVoice(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		if(Boolean.parseBoolean(BigSausage.settingsManager.getSettingsForGuild(triggerMessage.getGuild()).getProperty("allow_commanded_voice_clips"))){
			try{
				List<Linkable> audioClips = BigSausage.getFileManager().getLinkablesForGuildOfType(triggerMessage.getGuild(), EnumLinkableType.AUDIO);
				if(audioClips.size() == 0){
					sendReply(triggerMessage, "There are no audio clips! Try adding some!");
					return;
				}
				List<Linkable> objectsToLink = new ArrayList<>();
				List<String> args = Arrays.asList(triggerMessage.getContentRaw().split(" "));
				if(args.size() > 2){
					if(Util.isInteger(args.get(2), 10)){
						int numToLink = Integer.parseInt(args.get(2));
						SecureRandom random = new SecureRandom();
						for(int i = 0; i < numToLink; i++){
							objectsToLink.add(audioClips.get(random.nextInt(audioClips.size())));
						}
					}else{
						for(Linkable linkable : audioClips){
							int count = 0;
							for(Trigger trigger : linkable.getTriggers()){
								for(String s : args){
									if(s.toLowerCase().contentEquals(trigger.getTrigger().toLowerCase())) count++;
								}
							}
							if(count > 0){
								int maxClips = Integer.parseInt(BigSausage.settingsManager.getSettingsForGuild(triggerMessage.getGuild()).getProperty("max_audio_clips_per_message"));
								if(count > maxClips){
									count = maxClips;
								}
								for(int i = 0; i < count; i++){
									objectsToLink.add(linkable);
								}
							}
						}
					}
				}else{
					SecureRandom random = new SecureRandom();
					objectsToLink.add(audioClips.get(random.nextInt(audioClips.size())));
				}
				User sender = triggerMessage.getAuthor();
				Member member = triggerMessage.getGuild().getMember(sender);
				assert member != null;
				GuildVoiceState state = member.getVoiceState();
				assert state != null;
				if(state.inVoiceChannel()){
					VoiceChannel voiceChannel = state.getChannel();
					Collections.reverse(objectsToLink);
					for(Linkable linkable : objectsToLink){
						BSAudioManager.queueFile(linkable.getLinkedFile(), triggerMessage.getGuild(), voiceChannel, sender, true);
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

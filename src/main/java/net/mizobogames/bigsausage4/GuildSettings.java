package net.mizobogames.bigsausage4;

import net.dv8tion.jda.api.entities.Guild;

import java.io.Serializable;

public class GuildSettings implements Serializable{

	private long guild_id;

	private boolean messageParsingAudioTriggers;
	private boolean messageParsingImageTriggers;
	private boolean commandedVoiceClips;
	private boolean commandedImages;
	private boolean allowMultipleLinkablesPerMessage;
	private boolean allowTtsMessages;
	private int maxAudioClipsToQueuePerMessage;

	public GuildSettings(Guild owner){
		this.messageParsingAudioTriggers = false;
		this.messageParsingImageTriggers = false;
		this.commandedVoiceClips = true;
		this.commandedImages = true;
		this.allowMultipleLinkablesPerMessage = true;
		this.allowTtsMessages = true;
		this.maxAudioClipsToQueuePerMessage = 4;
		this.guild_id = owner.getIdLong();
	}

	public boolean isMessageParsingAudioTriggers(){
		return messageParsingAudioTriggers;
	}

	public void setMessageParsingAudioTriggers(boolean messageParsingAudioTriggers){
		this.messageParsingAudioTriggers = messageParsingAudioTriggers;
	}

	public boolean isMessageParsingImageTriggers(){
		return messageParsingImageTriggers;
	}

	public void setMessageParsingImageTriggers(boolean messageParsingImageTriggers){
		this.messageParsingImageTriggers = messageParsingImageTriggers;
	}

	public boolean isCommandedVoiceClips(){
		return commandedVoiceClips;
	}

	public void setCommandedVoiceClips(boolean commandedVoiceClips){
		this.commandedVoiceClips = commandedVoiceClips;
	}

	public boolean isCommandedImages(){
		return commandedImages;
	}

	public void setCommandedImages(boolean commandedImages){
		this.commandedImages = commandedImages;
	}

	public boolean isAllowMultipleLinkablesPerMessage(){
		return allowMultipleLinkablesPerMessage;
	}

	public void setAllowMultipleLinkablesPerMessage(boolean allowMultipleLinkablesPerMessage){
		this.allowMultipleLinkablesPerMessage = allowMultipleLinkablesPerMessage;
	}

	public boolean isAllowTtsMessages(){
		return allowTtsMessages;
	}

	public void setAllowTtsMessages(boolean allowTtsMessages){
		this.allowTtsMessages = allowTtsMessages;
	}

	public int getMaxAudioClipsToQueuePerMessage(){
		return maxAudioClipsToQueuePerMessage;
	}

	public void setMaxAudioClipsToQueuePerMessage(int maxAudioClipsToQueuePerMessage){
		this.maxAudioClipsToQueuePerMessage = maxAudioClipsToQueuePerMessage;
	}

	public long getGuildId(){
		return guild_id;
	}
}

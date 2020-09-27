package net.mizobogames.bigsausage4.io.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Reporting;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BSAudioManager{

	public final AudioPlayerManager playerManager;
	public final Map<Long, GuildAudioManager> audioManagers;

	public static BSAudioManager instance;

	public BSAudioManager(){
		instance = this;

		this.audioManagers = new HashMap<>();

		this.playerManager = new DefaultAudioPlayerManager();

		AudioSourceManagers.registerLocalSource(playerManager);
	}

	public synchronized GuildAudioManager getGuildAudioPlayer(Guild guild) {
		long guildId = Long.parseLong(guild.getId());
		GuildAudioManager guildAudioManager = audioManagers.get(guildId);

		if (guildAudioManager == null) {
			guildAudioManager = new GuildAudioManager(playerManager);
			audioManagers.put(guildId, guildAudioManager);
		}

		guild.getAudioManager().setSendingHandler(guildAudioManager.getSendHandler());

		return guildAudioManager;
	}

	private static void connectToVoiceChannel(VoiceChannel channel) throws Exception {
		AudioManager audioManager = channel.getGuild().getAudioManager();
		if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
			audioManager.openAudioConnection(channel);
		}
	}

	public static void disconnectPlayerFromVoiceChannel(AudioPlayer player){
		GuildAudioManager manager = null;
		Long guildID = null;
		for(Entry<Long, GuildAudioManager> e : instance.audioManagers.entrySet()){
			if(e.getValue().player.equals(player)){
				manager = e.getValue();
				guildID = e.getKey();
				break;
			}
		}
		if(guildID != null){
			Guild guild = BigSausage.jda.getGuildById(guildID);
			assert guild != null;
			guild.getAudioManager().closeAudioConnection();
		}
	}

	public static void queueFile(File f, Guild guild, VoiceChannel channelToJoin, User triggerUser, boolean commanded) throws Exception{
		GuildAudioManager musicManager = instance.getGuildAudioPlayer(guild);
		try {
			instance.playerManager.loadItemOrdered(musicManager, f.toURI().toURL().toString().replace("file:/", ""), new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack track) {
					try{
						connectToVoiceChannel(channelToJoin);
					}catch(Exception e){
						e.printStackTrace();
					}
					musicManager.scheduler.queue(track);
				}
				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
					AudioTrack firstTrack = playlist.getSelectedTrack();
					if (firstTrack == null) {
						firstTrack = playlist.getTracks().get(0);
					}

					try{
						connectToVoiceChannel(channelToJoin);
					}catch(Exception e){
						e.printStackTrace();
					}
					musicManager.scheduler.queue(firstTrack);
				}
				@Override
				public void noMatches() {}
				@Override
				public void loadFailed(FriendlyException exception) {
					System.err.println("Failed to load file!");
					Reporting.instance.reportAndPrintError(exception);
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}

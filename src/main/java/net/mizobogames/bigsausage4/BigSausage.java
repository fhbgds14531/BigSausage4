package net.mizobogames.bigsausage4;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.mizobogames.bigsausage4.commands.CommandBase;
import net.mizobogames.bigsausage4.commands.Commands;
import net.mizobogames.bigsausage4.commands.EnumPermissionLevel;
import net.mizobogames.bigsausage4.io.FileManager;
import net.mizobogames.bigsausage4.io.SettingsManager;
import net.mizobogames.bigsausage4.io.audio.BSAudioManager;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BigSausage extends ListenerAdapter {

	public static final String PREFIX = "!bs";
	private static final String TOKEN_FILE_NAME = "BigSausage.token";
	public static final long ME = 198575970624471040L;
	public static User MY_USER = null;
	public static final String VERSION = "4.2.1";
	public static final String CHANGELOG = "Reworked the settings behind the scenes to make use of java.util.Properties.";
	private static final String IGNORE_TOKEN = "<i";

	public static Commands commands;
	private static FileManager fileManager;
	public static BSAudioManager audioManager;
	public static SettingsManager settingsManager;

	public static JDA jda;
	private static BigSausage instance;

	public static Reporting reporter;

	private List<Guild> guilds;

	private static long startTime;

	public BigSausage(){
		if(instance == null) instance = this;
		if(commands == null) commands = new Commands();
		if(reporter == null) reporter = new Reporting();
		if(audioManager == null) audioManager = new BSAudioManager();
		if(guilds == null) guilds = new ArrayList<>();
	}

	public static BigSausage getBot(){
		return instance;
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		if(MY_USER == null) {
			MY_USER = jda.getUserById(ME);
		}
		if(fileManager == null) fileManager = new FileManager();

		if(settingsManager == null)	settingsManager = SettingsManager.init(guilds);
		for(Guild g : guilds){
			fileManager.initPermissionsAndLinkablesForNewGuild(g);
		}
		fileManager.savePermissions();
		fileManager.saveLinkables();

		MessageParser.reloadSettings();

		System.out.println(Util.ASCIILogo());
		System.out.println("\nReady to serve you!\n");
		long readyTime = System.currentTimeMillis();
		System.out.println("Startup completed in " + Util.getTimeDiffSecondsFromMillis(startTime, readyTime) + " seconds");
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		FileManager.assertDirExists("files/" + event.getGuild().getIdLong());
		System.out.println("Guild " + Util.getDisplayNameAndIdForGuild(event.getGuild()) + " is ready, adding to initialization queue...");
		guilds.add(event.getGuild());
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		fileManager.initPermissionsAndLinkablesForNewGuild(event.getGuild());
		settingsManager.onJoinNewGuild(event.getGuild());
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		Message m = event.getMessage();
		if(m.getAuthor().getIdLong() == jda.getSelfUser().getIdLong()) return;

		if(!MessageParser.parseMessageForCommandAndExecute(m)){
			MessageParser.parseMessageForTriggers(m);
		}
	}

	public static FileManager getFileManager(){
		return fileManager;
	}

	public static void main(String[] args) throws LoginException, IOException{
		startTime = System.currentTimeMillis();
		final String TOKEN = Files.readAllLines(new File(TOKEN_FILE_NAME).toPath()).get(0);
		System.out.println("Logging in...");
		JDABuilder builder = JDABuilder.createDefault(TOKEN);
		jda = builder.setActivity(Activity.watching("use \"" + PREFIX + "\" for commands")).build();
		jda.addEventListener(new BigSausage());
	}

	public static void shutdown(){
		fileManager.saveAll();
		settingsManager.saveAll();
		jda.shutdown();
		System.exit(0);
	}

	public static class CommandShutdown extends CommandBase{

		public CommandShutdown(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel permissionLevel){
			super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, permissionLevel);
		}

		@Override
		public void execute(Message triggerMessage){
			if(Util.userHasPermissionInGuild(triggerMessage.getAuthor(), triggerMessage.getGuild(), EnumPermissionLevel.BOT_CREATOR)){
				System.out.println("Shutting Down...");
				triggerMessage.getChannel().sendMessage("Shutting Down...").queue();
				shutdown();
			}
		}
	}
}

package net.mizobogames.bigsausage4;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
	public static final String VERSION = "4.1";
	public static final String CHANGELOG = "Added !bs roll. Some minor bugfixes and improvements.";
	private static final String IGNORE_TOKEN = "<i";

	public static Commands commands;
	private static FileManager fileManager;
	public static BSAudioManager audioManager;

	public static JDA jda;
	private static BigSausage instance;

	public static Reporting reporter;

	private List<Guild> guilds;

	public BigSausage(){
		instance = this;
		commands = new Commands();
		reporter = new Reporting();
		audioManager = new BSAudioManager();
		guilds = new ArrayList<>();
	}

	public static BigSausage getBot(){
		return instance;
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		MY_USER = jda.getGuildById(382053109788049429L).getMemberById(ME).getUser();
		fileManager = new FileManager();
		for(Guild g : guilds){
			fileManager.initPermissionsAndLinkablesForNewGuild(g);
			fileManager.saveSettingsForGuild(fileManager.getSettingsForGuild(g));
		}
		fileManager.savePermissions();
		fileManager.saveLinkables();
		new MessageParser();
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		FileManager.assertDirExists("files/" + event.getGuild().getIdLong());
		System.out.println("Guild " + Util.getDisplayNameAndIdForGuild(event.getGuild()) + " is ready, adding to initialization queue...");
		guilds.add(event.getGuild());
		System.out.println("Done!\n");
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		fileManager.initPermissionsAndLinkablesForNewGuild(event.getGuild());
		fileManager.saveSettingsForGuild(new GuildSettings(event.getGuild()));
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
		final String TOKEN = Files.readAllLines(new File(TOKEN_FILE_NAME).toPath()).get(0);
		System.out.println("Logging in...");
		jda = new JDABuilder(TOKEN).build();
		jda.addEventListener(new BigSausage());
	}

	public static void shutdown(){
		fileManager.saveAll();
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

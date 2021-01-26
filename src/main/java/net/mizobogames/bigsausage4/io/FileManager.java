package net.mizobogames.bigsausage4.io;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Reporting;
import net.mizobogames.bigsausage4.Util;
import net.mizobogames.bigsausage4.commands.EnumPermissionLevel;
import net.mizobogames.bigsausage4.linking.Linkable;
import net.mizobogames.bigsausage4.linking.Linkable.EnumLinkableType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileManager {

	private File filesDir;
	private File permissionsFile;
	private File linkablesFile;

	private Map<Long, Map<Long, EnumPermissionLevel>> permissionsForGuilds;
	private Map<Long, List<Linkable>> linkablesPerGuild;

	private boolean permissionsNeedsInitialization = true;
	private boolean linkablesNeedsInitialization = true;

	private String ttsPath = "files/%G/tts/tts.txt";

	public FileManager(){
		filesDir = new File("files");
		loadAll();
	}

	public void initPermissionsAndLinkablesForNewGuild(Guild guild){
		initPermissionsForNewGuild(guild);
		initLinkablesForNewGuild(guild);
	}

	private void initPermissionsForNewGuild(Guild guild){
		if(!permissionsForGuilds.containsKey(guild.getIdLong())){
			initPermissionsForGuild(guild);
		}//else{
		/*
		System.out.println("Guild \"" + guild.getName() + "\" (" + guild.getIdLong() + ") already has an entry in the permissions file. Skipping...");
		}
		*/
	}

	public void initPermissionsForGuild(Guild guild){
		Map<Long, EnumPermissionLevel> permissionLevelMap = new HashMap<>();
		assertDirExists("files/" + guild.getIdLong() + "/");
		for(Member member : guild.getMembers()){
			EnumPermissionLevel level;
			if(member.isOwner()){
				level = EnumPermissionLevel.SERVER_OWNER;
			}else if(member.hasPermission(Permission.ADMINISTRATOR)){
				level = EnumPermissionLevel.ADMIN;
			}else if(member.getUser().getId().contentEquals(BigSausage.MY_USER.getId())){
				level = EnumPermissionLevel.BOT_CREATOR;
			}else{
				level = EnumPermissionLevel.MEDIUM;
			}
			permissionLevelMap.put(member.getUser().getIdLong(), level);
		}
		permissionsForGuilds.put(guild.getIdLong(), permissionLevelMap);
		String log = "Initialized permissions map for guild \"" + guild.getName() + "\" (" + guild.getIdLong() + ")";
		AuditLogger.auditGloballyAndForGuild(guild, log);
		System.out.println(log);
	}

	public void initLinkablesForNewGuild(Guild guild){
		if(!linkablesPerGuild.containsKey(guild.getIdLong())){
			linkablesPerGuild.put(guild.getIdLong(), new ArrayList<>());
		}//else{
			/*
		System.out.println("Guild " + Util.getDisplayNameAndIdForGuild(guild) + " already has a linkables entry. Skipping...");
		}
		*/
	}

	public void addLinkableToGuild(Guild guild, Linkable linkable){
		linkablesPerGuild.get(guild.getIdLong()).add(linkable);
	}

	public void removeLinkableFromGuild(Guild guild, Linkable linkable){
		linkablesPerGuild.get(guild.getIdLong()).remove(linkable);
		linkable.getLinkedFile().delete();
	}

	public static void assertDirExists(String path){
		File dir = new File(path);
		if(!dir.exists() || !dir.isDirectory()){
			dir.mkdir();
			System.out.println("Created directory \"" + dir.getName() + "\"");
		}
	}

	public Object readObjectFromFile(File file){
		try{
			if(!file.exists()) throw new IOException("File does not exist!");
			FileInputStream fis = new FileInputStream(file.getPath());
			ObjectInputStream ois = new ObjectInputStream(fis);

			Object input = ois.readObject();

			ois.close();
			fis.close();

			return input;
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
			return null;
		}
	}

	private void saveObjectToFile(File file, Object obj, boolean shouldOverwrite){
		try{
			if(shouldOverwrite){
				if(file.exists()) file.delete();
			}else{
				if(file.exists()){
					String name = file.getName().substring(0, file.getName().lastIndexOf("."));
					String extension = file.getName().substring(file.getName().lastIndexOf("."));
					int count = 0;
					while(file.exists()){
						count++;
						file = new File(name + "_" + count + extension);
					}
				}
			}
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file.getPath());
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(obj);

			oos.close();
			fos.close();
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
		}
	}

	public void addPermissionLevelForUserInGuild(Guild guild, User user, EnumPermissionLevel level){
		Map<Long, EnumPermissionLevel> userPermissions = permissionsForGuilds.get(guild.getIdLong());
		if(userPermissions == null) userPermissions = new HashMap<>();
		userPermissions.put(user.getIdLong(), level);
		permissionsForGuilds.put(guild.getIdLong(), userPermissions);
	}

	public EnumPermissionLevel getPermissionsForUserInGuild(Guild guild, User user){
		Map<Long, EnumPermissionLevel> userPermissions = permissionsForGuilds.get(guild.getIdLong());
		if(userPermissions == null) return EnumPermissionLevel.NONE;
		if(userPermissions.containsKey(user.getIdLong())){
			return userPermissions.get(user.getIdLong());
		} else {
			if(guild.getMember(user).isOwner()) return EnumPermissionLevel.SERVER_OWNER;
			if(guild.getMember(user).hasPermission(Permission.ADMINISTRATOR)) return EnumPermissionLevel.ADMIN;
			if(user.getIdLong() == BigSausage.MY_USER.getIdLong()) return EnumPermissionLevel.BOT_CREATOR;
			return EnumPermissionLevel.NONE;
		}
	}

	public List<Linkable> getLinkablesForGuild(Guild guild){
		return linkablesPerGuild.get(guild.getIdLong());
	}

	public List<Linkable> getLinkablesForGuildOfType(Guild guild, EnumLinkableType type){
		List<Linkable> linkables = linkablesPerGuild.get(guild.getIdLong());
		List<Linkable> filtered = new ArrayList<>();
		for(Linkable linkable : linkables){
			if(linkable.getType() == type) filtered.add(linkable);
		}
		return filtered;
	}

	private void loadAll(){
		long time1 = System.currentTimeMillis();
		loadPermissions();
		loadLinkables();
		long time2 = System.currentTimeMillis();
		System.out.println("Loaded all permissions and linkables in " + Util.getTimeDiffSecondsFromMillis(time1, time2) + " seconds");
	}

	public void loadPermissions(){
		Map<Long, Map<Long, EnumPermissionLevel>> loaded = null;
		try{
			permissionsFile = new File("files/permissions.bssf");
			assertDirExists(filesDir.getPath());

			Object permissionsInput = readObjectFromFile(permissionsFile);
			if(permissionsInput instanceof Map){
				loaded = (Map<Long, Map<Long, EnumPermissionLevel>>) permissionsInput;
			}else{
				throw new IOException("The object saved in the permissions file is not a Map.");
			}
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
		}
		if(loaded == null){
			loaded = new HashMap<>();
			System.err.println("Could not successfully load permissions, loading blank map instead.");
		} else {
			System.out.println("Permissions file Successfully loaded.");
			this.permissionsNeedsInitialization = false;
		}
		this.permissionsForGuilds = loaded;
	}

	public void loadLinkables(){
		Map<Long, List<Linkable>> loaded = null;
		try{
			filesDir = new File("files");
			linkablesFile = new File("files/linkables.bssf");
			assertDirExists(filesDir.getPath());

			Object linkablesInput = readObjectFromFile(linkablesFile);
			if(linkablesInput instanceof Map){
				loaded = (Map<Long, List<Linkable>>) linkablesInput;
			}else{
				throw new IOException("The object saved in the linkables file is not a Map.");
			}
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
		}
		if(loaded == null){
			loaded = new HashMap<>();
			System.err.println("Could not successfully load linkables, loading blank map instead.");
		} else{
			System.out.println("Linkables file Successfully loaded.");
			this.linkablesNeedsInitialization = false;
		}
		this.linkablesPerGuild = loaded;
	}

	public List<String> getTtsListForGuild(Guild guild){
		try{
			String guildPath = ttsPath.replace("%G", String.valueOf(guild.getIdLong()));
			assertDirExists(guildPath.replace("/tts.txt", ""));
			File ttsFile = new File(guildPath);
			if(! ttsFile.exists()) ttsFile.createNewFile();
			return Files.readAllLines(ttsFile.toPath());
		}catch(Exception e){
			BigSausage.reporter.reportAndPrintError(e);
			return new ArrayList<>();
		}
	}

	public void addTtsForGuild(Guild guild, String line){
		try{
			String guildPath = ttsPath.replace("%G", String.valueOf(guild.getIdLong()));
			assertDirExists(guildPath.replace("/tts.txt", ""));
			File ttsFile = new File(guildPath);
			if(! ttsFile.exists()) ttsFile.createNewFile();
			Files.write(ttsFile.toPath(), Collections.singletonList(line), StandardOpenOption.APPEND);
		}catch(Exception e){
			BigSausage.reporter.reportAndPrintError(e);
		}
	}

	public File getAuditFileForGuild(Guild guild){
		try{
			assertDirExists("files/" + guild.getIdLong() + "/audit/");
			String filePath = "files/" + guild.getIdLong() + "/audit/log.txt";
			File auditLogFile = new File(filePath);
			if(! auditLogFile.exists()) auditLogFile.createNewFile();
			return auditLogFile;
		}catch(Exception e){
			BigSausage.reporter.reportAndPrintError(e);
		}
		return null;
	}

	public void saveAll(){
		saveLinkables();
		savePermissions();
	}

	public void savePermissions(){
		saveObjectToFile(permissionsFile, permissionsForGuilds, true);
	}

	public void saveLinkables(){
		saveObjectToFile(linkablesFile, linkablesPerGuild, true);
	}

	public void updateUserPermissionsForGuild(User user, Guild guild, EnumPermissionLevel newPermissionLevel){
		permissionsForGuilds.get(guild.getIdLong()).put(user.getIdLong(), newPermissionLevel);
		savePermissions();
	}

	public Properties loadPropertiesForGuild(Guild guild, Properties guildProperties){
		Properties loadedProperties = new Properties(guildProperties);
		try{
			File propertiesFile = new File("files/" + guild.getIdLong() + "/settings/guild_properties.bss");
			if(!propertiesFile.exists()){
				savePropertiesForGuild(guild, loadedProperties);
			}
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(propertiesFile));
			loadedProperties.load(in);
			in.close();
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
		}
		return loadedProperties;
	}

	public void savePropertiesForGuild(Guild guild, Properties properties){
		try{
			File propertiesFile;
			if(guild != null){
				propertiesFile = new File("files/" + guild.getIdLong() + "/settings/guild_properties.bss");
			}else{
				propertiesFile = new File("files/default_properties.bss");
			}
			if(!propertiesFile.exists()) propertiesFile.createNewFile();
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(propertiesFile));
			properties.store(out, "=== Settings Version: " + SettingsManager.SETTINGS_VERSION + " ===");
			out.close();
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
		}

	}
}

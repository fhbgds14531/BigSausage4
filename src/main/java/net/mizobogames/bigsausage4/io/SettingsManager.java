package net.mizobogames.bigsausage4.io;

import net.dv8tion.jda.api.entities.Guild;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Reporting;
import net.mizobogames.bigsausage4.Util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SettingsManager {

	public static final String SETTINGS_VERSION = "2.0";

	private Map<Guild, Properties> propertiesMap;
	private static Properties defaultSettings;
	private static File defaultPropsFile = new File("files/default_properties.bss");

	private SettingsManager(){
		propertiesMap = new HashMap<>();
	}

	public static SettingsManager init(List<Guild> guilds){
		defaultSettings = new Properties();
		SettingsManager manager = new SettingsManager();
		try{
			FileInputStream in = new FileInputStream(defaultPropsFile);
			defaultSettings.load(in);
			in.close();
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
		}

		return manager.load(guilds);
	}

	public Properties getSettingsForGuild(Guild guild){
		if(!propertiesMap.containsKey(guild)){
			loadGuild(guild);
		}
		return propertiesMap.get(guild);
	}

	private SettingsManager load(List<Guild> guilds){
		System.out.println("Loading settings for " + guilds.size() + " guilds...");
		long time1 = System.currentTimeMillis();
		for(Guild guild : guilds){
			loadGuild(guild);
		}
		long time2 = System.currentTimeMillis();

		System.out.println("Done! in " + Util.getTimeDiffSecondsFromMillis(time1, time2) + " seconds.");
		return this;
	}

	private void loadGuild(Guild guild){
		Properties guildProps = new Properties(defaultSettings);
		guildProps = BigSausage.getFileManager().loadPropertiesForGuild(guild, guildProps);
		propertiesMap.put(guild, guildProps);
	}

	public void onJoinNewGuild(Guild guild){
		loadGuild(guild);
	}

}

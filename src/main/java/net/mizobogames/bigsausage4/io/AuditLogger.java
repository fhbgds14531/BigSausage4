package net.mizobogames.bigsausage4.io;

import net.dv8tion.jda.api.entities.Guild;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Reporting;
import net.mizobogames.bigsausage4.Util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditLogger {

	public static void auditGloballyAndForGuild(Guild guild, String line){
		auditGloballyAndForGuild(guild, Collections.singletonList(line));
	}

	public static void auditGloballyAndForGuild(Guild guild, List<String> lines){
		addToAuditLogForGuild(guild, lines);
		addToAuditLogForGuild(null, lines);
	}

	public static void addToAuditLogForGuild(Guild guild, String line){
		addToAuditLogForGuild(guild, Collections.singletonList(line));
	}

	public static void addToAuditLogForGuild(Guild guild, List<String> lines){

		File guildLogFile;
		if(guild == null){
			guildLogFile = new File("defaultAuditLog.txt");
		}else{
			guildLogFile = BigSausage.getFileManager().getAuditFileForGuild(guild);
		}
		try{
			if(!guildLogFile.exists()) guildLogFile.createNewFile();
			List<String> formatted = new ArrayList<>();
			for(String s : lines){
				String time = Util.getTimecode();
				formatted.add("[" + time + "] " + s);
			}

			Files.write(guildLogFile.toPath(), formatted, StandardOpenOption.APPEND);
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
		}
	}

}

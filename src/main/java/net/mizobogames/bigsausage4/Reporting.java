package net.mizobogames.bigsausage4;

import net.dv8tion.jda.api.entities.TextChannel;
import net.mizobogames.bigsausage4.io.FileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Reporting{

	private static final String LOG_LOC = "logs";
	private static final int FILE_NAME_TIMEOUT = 3;

	public static Reporting instance;
	private TextChannel reportChannel = null;

	public Reporting(){
		instance = this;
	}

	public boolean reportAndPrintError(Exception e){
		if(reportChannel == null) reportChannel = BigSausage.jda.getGuildById(382053109788049429L).getTextChannelById(577710759820394506L);
		System.err.println("\n\n==================== Reporting error ====================");
		List<String> lines = new ArrayList<>();

		addStackTraceToList(e, lines);
		for(Throwable t : e.getSuppressed()) addStackTraceToList(t, lines);
		Throwable cause = e.getCause();
		if(cause != null) addStackTraceToList(cause, lines);

		Util.printList(lines, System.err);

		FileManager.assertDirExists(LOG_LOC);

		Date currentTime = new Date();
		currentTime.setTime(System.currentTimeMillis());
		DateFormat df = DateFormat.getDateTimeInstance(3, 3);
		String now = df.format(currentTime);

		now = now.replace("/", "-").replace(":", "").replace(" ", "_");

		String errFileName = LOG_LOC + "/ERR_" + now + ".txt";
		File errFile = new File(LOG_LOC + "/ERR_" + now + ".txt");
		int count = 0;
		while(errFile.exists()){
			count++;
			errFile = new File(LOG_LOC + "/ERR_" + now + "_" + count + ".txt");
			if(count > FILE_NAME_TIMEOUT){
				System.err.println("Could not create error file \"" + errFile.getName() + "\". " + FILE_NAME_TIMEOUT + " files with that name already exist. File name timeout reached.");
				return false;
			}
		}

		boolean createdFile = false;
		try{
			createdFile = errFile.createNewFile();
			if(createdFile){
				Files.write(errFile.toPath(), lines, StandardOpenOption.WRITE);
			}
		}catch(IOException ex){
			System.err.println("\nERROR CREATING ERROR LOG FILE \"" + errFileName + "\"! HOW IRONIC!!");
			ex.printStackTrace();
			return false;
		}

		lines.add(0, "Automatic error report:");
		if(BigSausage.MY_USER != null) lines.add(0, BigSausage.MY_USER.getAsMention());
		Util.sendListToTextChannel(lines, reportChannel);

		System.err.println("=========================== Done! ===========================");
		return true;
	}

	private static List<String> addStackTraceToList(Throwable t, List<String> list){
		list.add(t.toString());
		for(StackTraceElement ste : t.getStackTrace()){
			list.add("\tat " + ste);
		}
		return list;
	}

}

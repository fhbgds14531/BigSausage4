package net.mizobogames.bigsausage4;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.mizobogames.bigsausage4.commands.EnumPermissionLevel;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util{

	public static void printList(List<String> lines, PrintStream stream){
		for(String s : lines){
			stream.println(s);
		}
	}

	public static void sendListToTextChannel(List<String> list, TextChannel channel){
		StringBuilder message = new StringBuilder();
		for(String line : list){
			if(message.length() > 1999 - line.length()){
				channel.sendMessage(message.toString()).queue();
				message = new StringBuilder();
			}
			message.append(line).append("\n");
		}
		channel.sendMessage(message.toString()).queue();
	}

	public static void sendListToTextChannelAsSeparateLines(List<String> list, TextChannel channel){
		for(String line : list){
			channel.sendMessage(line).queue();
		}
	}

	public static int getLongestStringLength(List<String> strings){
		int longestLength = 0;
		for(String string : strings){
			if(string != null){
				if(string.length() > longestLength){
					longestLength = string.length();
				}
			}
		}
		return longestLength;
	}

	public static List<String> formatListIntoEqualSpacedLinesWithCommas(List<String> toBeFormatted, int itemsPerLine){
		int maxLength = 0;
		if(toBeFormatted.size() > itemsPerLine) getLongestStringLength(toBeFormatted);
		List<String> formatted = new ArrayList<>();
		StringBuilder line = new StringBuilder();
		int count = 0;
		for(String item : toBeFormatted){
			count++;
			int makeupLength = maxLength - item.length();
			line.append(appendArbitraryNumberOfCharacter(item, ' ', makeupLength)).append(", ");
			if(count >= itemsPerLine){
				count = 0;
				formatted.add(replace4SpacesWithTabs(line.substring(0, line.toString().lastIndexOf(','))));
				line = new StringBuilder();
			}
		}
		if(formatted.isEmpty() && (line.length() > 0)){
			System.out.println(replace4SpacesWithTabs(line.substring(0, line.toString().lastIndexOf(','))));
			formatted.add(replace4SpacesWithTabs(line.substring(0, line.toString().lastIndexOf(','))));
		}
		return formatted;
	}

	public static String appendArbitraryNumberOfCharacter(String stringToModify, char characterToAdd, int numberToAdd){
		if(numberToAdd < 1) return stringToModify;
		StringBuilder stringToModifyBuilder = new StringBuilder(stringToModify);
		for(int i = 0; i < numberToAdd; i++){
			stringToModifyBuilder.append(characterToAdd);
		}
		stringToModify = stringToModifyBuilder.toString();
		return stringToModify;
	}

	public static String replace4SpacesWithTabs(String input){
		return input.replace("    ", "\t");
	}

	public static boolean userHasPermissionInGuild(User user, Guild guild, EnumPermissionLevel requiredLevel){
		if(user.getIdLong() == BigSausage.ME) return true;
		return BigSausage.getFileManager().getPermissionsForUserInGuild(guild, user).getLevel() >= requiredLevel.getLevel();
	}

	public static boolean isInteger(String s, int radix) {
		Scanner sc = new Scanner(s.trim());
		if(!sc.hasNextInt(radix)) return false;
		// we know it starts with a valid int, now make sure
		// there's nothing left!
		sc.nextInt(radix);
		return !sc.hasNext();
	}

	public static String getTimecode(){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	public static String getDisplayNameAndIdForUser(User user){
		return "\"" + user.getName() + "\" (" + user.getIdLong() + ")";
	}

	public static String getDisplayNameAndIdForGuild(Guild guild){
		return "\"" + guild.getName() + "\" (" + guild.getIdLong() + ")";
	}

	public static String ASCIILogo(){
		return 	"  ____  _          _____                                  \n" +
				" |  _ \\(_)        / ____|                                 \n" +
				" | |_) |_  __ _  | (___   __ _ _   _ ___  __ _  __ _  ___ \n" +
				" |  _ <| |/ _` |  \\___ \\ / _` | | | / __|/ _` |/ _` |/ _ \\\n" +
				" | |_) | | (_| |  ____) | (_| | |_| \\__ \\ (_| | (_| |  __/\n" +
				" |____/|_|\\__, | |_____/ \\__,_|\\__,_|___/\\__,_|\\__, |\\___|\n" +
				"           __/ |                                __/ |     \n" +
				"          |___/                                |___/      ";
	}

	public static double getTimeDiffSecondsFromMillis(long time1, long time2){
		return (time2 - time1)/1000d;
	}
}

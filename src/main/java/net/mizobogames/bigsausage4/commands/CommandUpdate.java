package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.mizobogames.bigsausage4.BigSausage;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandUpdate extends CommandBase{

	public CommandUpdate(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		List<String> args = Arrays.asList(triggerMessage.getContentRaw().toLowerCase().split(" "));
		if (triggerMessage.getAuthor().getIdLong() == BigSausage.ME) {
			if (args.size() == 2) { // Regular update (only update if the version number doesn't match)
				try {
					URL url = new URL("https://raw.githubusercontent.com/fhbgds14531/BigSausage-Versions/master/newVersion.txt?raw=true");
					File version = new File("newVersion.txt");
					download(url, version);
					String newVersionString = Files.readAllLines(version.toPath()).get(0);
					if (!newVersionString.contentEquals(BigSausage.VERSION)) {
						doUpdate(newVersionString, triggerMessage.getTextChannel());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (args.size() == 3 && args.get(2).toLowerCase().contentEquals("force")) { // force update
				try {
					URL url = new URL("https://raw.githubusercontent.com/fhbgds14531/BigSausage-Versions/master/newVersion.txt?raw=true");
					File version = new File("newVersion.txt");
					download(url, version);
					String newVersionString = Files.readAllLines(version.toPath()).get(0);
					doUpdate(newVersionString, triggerMessage.getTextChannel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (args.size() == 4 && args.get(2).toLowerCase().contentEquals("force")) { // force update to a specific version
				try {
					String newVersionString = args.get(3);
					List<String> content = new ArrayList<String>();
					URL update = new URL("https://github.com/fhbgds14531/BigSausage-Versions/blob/master/" + newVersionString + "/BigSausage.jar?raw=true");
					InputStream in = update.openStream(); // throws an IOException
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line;
					while ((line = reader.readLine()) != null) {
						content.add(line);
					}
					if (content.contains("    <title>Page not found &middot; GitHub</title>")) { // if this is true, the supplied version number is invalid.
						triggerMessage.getTextChannel().sendMessage("The supplied verison string is invalid. I can't update to a version that doesn't exist!").queue();
					} else {
						doUpdate(newVersionString, triggerMessage.getTextChannel());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void doUpdate(String newVersionString, TextChannel outputChannel) throws Exception {
		URL update = new URL("https://github.com/fhbgds14531/BigSausage-Versions/blob/master/" + newVersionString + "/BigSausage.jar?raw=true");
		URLConnection c = update.openConnection();
		InputStream in = c.getInputStream();

		FileOutputStream out = new FileOutputStream(new File("BigSausage_1.jar"));
		int n = -1;
		byte[] buffer = new byte[2048];
		while ((n = in.read(buffer)) != -1) {
			if (n > 0) {
				out.write(buffer, 0, n);
			}
		}
		in.close();
		out.close();
		tryRestartForUpdate(outputChannel);
	}

	private void download(URL url, File location) {
		try {
			URLConnection c = url.openConnection();
			InputStream in = c.getInputStream();

			FileOutputStream out = new FileOutputStream(location);
			int n = -1;
			byte[] buffer = new byte[2048];
			while ((n = in.read(buffer)) != -1) {
				if (n > 0) {
					out.write(buffer, 0, n);
				}
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void tryRestartForUpdate(TextChannel outputChannel) {
		try {
			outputChannel.sendMessage("Restarting...").queue();
			Runtime.getRuntime().exec("cmd /c start \"\" rename.bat");
			BigSausage.shutdown();
		} catch (IOException e) {
			BigSausage.reporter.reportAndPrintError(e);
		}
	}
}

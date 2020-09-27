package net.mizobogames.bigsausage4.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.mizobogames.bigsausage4.BigSausage;
import net.mizobogames.bigsausage4.Reporting;
import net.mizobogames.bigsausage4.Util;
import net.mizobogames.bigsausage4.io.FileManager;
import net.mizobogames.bigsausage4.linking.Linkable;
import net.mizobogames.bigsausage4.linking.Linkable.EnumLinkableType;

import java.io.File;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.Arrays;

public class CommandUploadFile extends CommandBase{

	public CommandUploadFile(String commandLongName, String commandTriggerString, String commandDescription, String helpText, String usageText, EnumPermissionLevel minimumPermissionLevel){
		super(commandLongName, commandTriggerString, commandDescription, helpText, usageText, minimumPermissionLevel);
	}

	@Override
	public void execute(Message triggerMessage){
		try{
			if(Util.userHasPermissionInGuild(triggerMessage.getAuthor(), triggerMessage.getGuild(), EnumPermissionLevel.MEDIUM)){
				if(! triggerMessage.getAttachments().isEmpty()){
					for(Attachment attachment : triggerMessage.getAttachments()){
						if(attachment.getSize() > 4999999){
							sendReply(triggerMessage, "The file \"" + attachment.getFileName() + "\" is too large! Try to keep file sizes under 5MB.");
						}else{
							boolean flag = false;
							File attachmentFile = new File("files/" + triggerMessage.getGuild().getId() + "/" + attachment.getFileName().toLowerCase());
							if(attachmentFile.exists()){
								sendReply(triggerMessage, "A file with that name already exists, please upload a unique file.");
							}else{
								File guildDir = new File("files/" + triggerMessage.getGuild().getId());
								if(! guildDir.exists()) guildDir.mkdirs();
								if(attachment.isImage()){
									downloadAndAddLinkable(triggerMessage.getGuild(), attachment, attachmentFile, EnumLinkableType.IMAGE);
									flag = true;
								}else{
									if(isAudioFilenameValid(attachment.getFileName())){
										downloadAndAddLinkable(triggerMessage.getGuild(), attachment, attachmentFile, EnumLinkableType.AUDIO);
										flag = true;
									}
								}
								if(flag){
									BigSausage.getFileManager().saveAll();
									sendReply(triggerMessage, "Uploaded file \"" + attachment.getFileName() + "\" with the default trigger \"" + attachment.getFileName().substring(0, attachment.getFileName().lastIndexOf(".")) + "\"");
								}else{
									sendReply(triggerMessage, "Failed to upload file \"" + attachment.getFileName() + "\".");
								}
							}
						}
					}
				}else{
					sendReply(triggerMessage, "You need to attach a file in order to upload it.");
				}
			}else{
				sendReply(triggerMessage, "You don't have permission to use that command.\nMinimum permission level required: `" + this.getPermissionLevel().toString() + "`\n" +
						"Your permission level:             `" + BigSausage.getFileManager().getPermissionsForUserInGuild(triggerMessage.getGuild(), triggerMessage.getAuthor()).toString() + "`");
			}
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
		}
	}

	private void downloadAndAddLinkable(Guild guild, Attachment attachment, File fileToDownloadTo, EnumLinkableType type) throws IOException{
		Linkable linkable = null;
		try{
			linkable = new Linkable(null, attachment.getFileName().toLowerCase().substring(0, attachment.getFileName().lastIndexOf(".")), attachment.downloadToFile(fileToDownloadTo).get(), type);
		}catch(Exception e){
			Reporting.instance.reportAndPrintError(e);
		}
		if(linkable != null){
			BigSausage.getFileManager().addLinkableToGuild(guild, linkable);
		}
	}

	private boolean isAudioFilenameValid(String filename) {
		return filename.toLowerCase().matches("([^\\s]+(\\.(?i)(wav|flac|mp3|m4a|ogg))$)");
	}
}

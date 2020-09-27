package net.mizobogames.bigsausage4.linking;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Linkable implements Serializable{

	private List<Trigger> triggers;
	private String name;
	private EnumLinkableType type;
	private File linkedFile;

	public Linkable(List<Trigger> triggers, @NotNull String name, @NotNull File linked, @NotNull EnumLinkableType type) throws IOException{
		if(triggers == null){
			triggers = new ArrayList<>();
		}
		if(triggers.isEmpty()){
			triggers.add(new Trigger(name, this));
		}
		this.triggers = triggers;
		this.name = name;
		this.type = type;
		if(!linked.exists()){
			throw new IOException("Nonexistent file passed as argument.");
		}
		this.linkedFile = linked;
	}

	public List<Trigger> getTriggers(){
		return this.triggers;
	}

	public String getName(){
		return name;
	}

	public EnumLinkableType getType(){
		return type;
	}

	public void addTrigger(String triggerString){
		this.addTrigger(new Trigger(triggerString, this));
	}

	private void addTrigger(Trigger t){
		this.triggers.add(t);
	}

	public void removeTrigger(Trigger t){
		this.triggers.remove(t);
	}

	public File getLinkedFile(){
		return linkedFile;
	}

	public enum EnumLinkableType{
		AUDIO,
		IMAGE;
	}
}

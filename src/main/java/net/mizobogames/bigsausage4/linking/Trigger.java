package net.mizobogames.bigsausage4.linking;

import java.io.Serializable;

public class Trigger implements Serializable{

	private String trigger;
	private Linkable associatedLinkable;

	public Trigger(String triggerString, Linkable linkable){
		this.trigger = triggerString;
		this.associatedLinkable = linkable;
	}

	public String getTrigger(){
		return this.trigger;
	}

	public Linkable getAssociatedLinkable(){
		return associatedLinkable;
	}
}

package net.mizobogames.bigsausage4.commands;

public enum EnumPermissionLevel{
	NONE(0),
	MEDIUM(1),
	ELEVATED(2),
	ADMIN(3),
	SERVER_OWNER(4),
	BOT_CREATOR(Integer.MAX_VALUE);

	int level;

	EnumPermissionLevel(int level){
		this.level = level;
	}

	public int getLevel(){
		return level;
	}

	public static EnumPermissionLevel fromString(String s){
		switch(s){
			case "medium":
				return MEDIUM;
			case "elevated":
				return ELEVATED;
			case "admin":
				return ADMIN;
			case "server-owner":
				return SERVER_OWNER;
			case "bot-creator":
				return BOT_CREATOR;
			default:
				return NONE;
		}
	}
}

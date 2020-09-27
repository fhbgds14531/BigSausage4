package net.mizobogames.bigsausage4.commands.rolls;

import net.mizobogames.bigsausage4.BSException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiceRoll{

	private final int rollsToTrack = 10;

	private List<Die> dice;
	private int modifier;

	private static final String regex = "(\\d+)d(\\d+)([\\+\\-](\\d+))*";

	Pattern pattern;
	Matcher matcher;

	public DiceRoll(String rollText) {
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(rollText);

		if(matcher.matches()){
			this.dice = parseForDice();
			this.modifier = parseForModifier();
		}
	}

	private List<Die> parseForDice(){
		List<Die> diceList = new ArrayList<>();
		int numberOfDice = Integer.parseInt(matcher.group(1));
		int numberOfSides = Integer.parseInt(matcher.group(2));
		for(int i = 0; i < numberOfDice; i++){
			diceList.add(new Die(numberOfSides));
		}
		return diceList;
	}

	private int parseForModifier(){
		String group = matcher.group(4);
		if(group == null || group.isEmpty()){
			group = "0";
		}
		return Integer.parseInt(group);
	}

	public String roll(){
		if(dice == null || dice.isEmpty()){
			return "Invalid input, could not resolve parameters.";
		}

		long result = 0;
		SecureRandom random = new SecureRandom();
		List<Integer> rolls = new ArrayList<>();
		for(Die die : dice){
			int roll = die.roll(random) + 1;
			rolls.add(roll);
			result += roll;
		}
		result += modifier;
		String output = "Result: " + result + "\nRolls: `";
		int count = 0;
		for(Integer integer : rolls){
			output += integer;
			count++;
			if(count >= rollsToTrack || count >= rolls.size()){
				if(count != rolls.size()){
					output += "...";
				}
				break;
			}else{
				output += ", ";
			}
		}

		output += "`";
		if(modifier != 0){
			output += "\nModifier: `" + matcher.group(3) + "`";
		}
		return String.valueOf(output);
	}

}

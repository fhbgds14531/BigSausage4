package net.mizobogames.bigsausage4.commands.rolls;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiceRoll{

	private final int rollsToTrack;

	private List<Die> dice;
	private int modifier;

	private static final String regex = "(\\d+)d(\\d+)([+\\-](\\d+))*";

	Pattern pattern;
	Matcher matcher;

	public DiceRoll(String rollText, int rollsToTrack) {
		this.rollsToTrack = rollsToTrack;
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
		StringBuilder output = new StringBuilder("Result: " + result + "\nRolls: `");
		int count = 0;
		for(Integer integer : rolls){
			output.append(integer);
			count++;
			if(count >= rollsToTrack || count >= rolls.size()){
				if(count != rolls.size()){
					output.append("...");
				}
				break;
			}else{
				output.append(", ");
			}
		}

		output.append("`");
		if(modifier != 0){
			output.append("\nModifier: `").append(matcher.group(3)).append("`");
		}
		return output.toString();
	}

}

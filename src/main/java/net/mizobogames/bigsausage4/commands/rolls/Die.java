package net.mizobogames.bigsausage4.commands.rolls;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Die{

	int sides;

	public Die(int numberOfSides){
		this.sides = numberOfSides;
	}

	public int roll(SecureRandom random){
		int result = 0;

		result = random.nextInt(sides);

		return result;
	}
}

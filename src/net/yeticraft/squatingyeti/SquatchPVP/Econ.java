package net.yeticraft.squatingyeti.SquatchPVP;

import net.milkbowl.vault.economy.Economy;

public class Econ {
	public static Economy economy;
	
	public static double getPercentMoney(String player, double percent) {
		return economy.getBalance(player) * percent;
	}
	
	public static boolean takeMoney(String player, double amount) {
		
		if (amount == 0)
			return false;
		
		if (!economy.has(player, amount))
			return false;
		
		economy.withdrawPlayer(player, amount);
		return true;
	}
	
	public static void giveMoney(String player, double amount) {
		economy.depositPlayer(player, amount);
	}
	
	public static String format(double amount) {
		return economy.format(amount).replace(".00", "");
	}

}

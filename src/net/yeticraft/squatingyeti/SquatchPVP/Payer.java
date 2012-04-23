package net.yeticraft.squatingyeti.SquatchPVP;

import java.util.Random;

import org.bukkit.entity.Player;

public class Payer {
	
	public static enum PayType {
		SPIRIT,
		FLATRATE,
		PERCENTKDR,
		PERCENT,
		PERCENTRANGE,
		RANGE;
	}
	
    public static boolean feeAsPercent;
    public static double feeAmount;
    public static PayType payType;
    public static int percent;
    public static double amount;
    public static int threshold;
    public static int modifier;
    public static int max;
    public static int hi;
    public static int lo;
    public static boolean whole;
    

    public static void dropMoney(Player victim) {
        //Cancel if there is no fee
        if (feeAmount == 0)
            return;
        
        //Cancel if the fee is disabled in this World
       // if (feeDisabledIn.contains(victim.getWorld().getName()))
         //   return;

        //Cancel if the Player is allowed to ignore the fee
        if (SquatchPVP.hasPermission(victim, "ignoredeathfee"))
            return;

        //Get the amount that will be taken from the Player
        double dropped;
        if (feeAsPercent)
            dropped = Econ.getPercentMoney(victim.getName(), feeAmount/100);
        else
            dropped = feeAmount;

        //Cancel if the Player is not dropping any money
        dropped = trim(dropped);
        if (dropped == 0)
            return;

        Econ.takeMoney(victim.getName(), dropped);
        victim.sendMessage(SquatchPVPMessages.getDeathFeeMsg(dropped));
    }


    public static void rewardPvP(Player victim, Ratio victimRatio) {
        //Find the Player that killed the victim Player
        Player killer = SquatchPVP.server.getPlayer(victimRatio.inCombatWith);
        
        //Mark the ratio of the killed Player as not in combat
        victimRatio.resetCombat();

        //Cancel if one of the Players does not have proper Permission
        if (!SquatchPVP.hasPermission(killer, victim))
            return;

        Ratio killerRatio = SquatchPVP.getRatio(killer.getName());
        
        victimRatio.incrementDeaths();
        killerRatio.incrementKills();
        
        double victimKDR = victimRatio.kdr;
        double killerKDR = killerRatio.kdr;
        
        Random random = new Random();
        double reward = 0;

        //Determine the reward amount based on the reward type
        switch (payType) {
            case PERCENTKDR:
                reward = Econ.getPercentMoney(victim.getName(), (victimKDR / killerKDR) / 100.0);
                break;

            case PERCENT:
                reward = Econ.getPercentMoney(victim.getName(), percent / 100.0);
                break;

            case PERCENTRANGE:
                double rangePercent = random.nextInt((hi + 1) - lo);
                rangePercent = (rangePercent + lo) / 100;
                reward = Econ.getPercentMoney(victim.getName(), rangePercent);
                break;

            case SPIRIT:
                //Check if the killed Player is no longer a hunter
                if (victimRatio.decrementSpirit(victim))
                    SquatchPVP.server.broadcastMessage(SquatchPVPMessages.getHunterNoMoreBroadcast(victimRatio.name, killerRatio.name, String.valueOf(victimRatio.spirit)));
                
                if (victim.isOnline())
                    victim.sendMessage(SquatchPVPMessages.getSpiritDecreasedMsg(victimRatio.name, killerRatio.name, String.valueOf(victimRatio.spirit)));
                
                int percentOfSteal;
                
                //The killer's spirit does not change if they killed a hunter
                if (victimRatio.isHunter()) {
                    //100% chance of theft because the killed Player is a hunter
                    percentOfSteal = 100;
                    
                    killer.sendMessage(SquatchPVPMessages.getSpiritNoChangeMsg(victimRatio.name, killerRatio.name, String.valueOf(killerRatio.spirit)));
                }
                else {
                    //Chance of theft is determined by the killed Players spirit
                    percentOfSteal = (int)percent + victimRatio.spirit;
                    
                    //Check if the killer is now a hunter
                    if (killerRatio.incrementSpirit(killer))
                        SquatchPVP.server.broadcastMessage(SquatchPVPMessages.getHunterBroadcast(victimRatio.name, killerRatio.name, String.valueOf(killerRatio.spirit)));
                    
                    killer.sendMessage(SquatchPVPMessages.getSpiritIncreasedMsg(victimRatio.name, killerRatio.name, String.valueOf(killerRatio.spirit)));
                }
                
                //Roll to see if theft will occur
                int roll = random.nextInt(100);
                if (roll >= percentOfSteal)
                    return;

                //Calculate the pay amount
                int multiplier = (int)((killerRatio.spirit - Ratio.hunterLevel) / threshold);
                
                double bonus = multiplier * (modifier);
                if (bonus > 0) {
                    if (bonus > max)
                        bonus = max;
                }
                else if (bonus < 0)
                    if (bonus < max)
                        bonus = max;

                double spiritPercent = random.nextInt((hi + 1) - lo);
                spiritPercent = (spiritPercent + lo) / 100;
                reward = Econ.getPercentMoney(victim.getName(), spiritPercent);
                reward = reward + (reward * bonus);
                
                SquatchPVP.save();
                break;

            case RANGE:
                reward = random.nextInt((hi + 1) - lo);
                reward = reward + lo;
                break;

            case FLATRATE: reward = amount; break;
        }
        
        reward = trim(reward);
        
        //Cancel if the killed Player has insufficient funds
        if (!Econ.takeMoney(victim.getName(), reward)) {
            if (victim.isOnline())
                victim.sendMessage(SquatchPVPMessages.getVictimNotEnoughMoneyMsg(reward, victimRatio.name, killerRatio.name, String.valueOf(victimRatio.spirit)));
            killer.sendMessage(SquatchPVPMessages.getKillerNotEnoughMoneyMsg(reward, victimRatio.name, killerRatio.name, String.valueOf(killerRatio.spirit)));
            return;
        }

        Econ.giveMoney(killer.getName(), reward);
        if (victim.isOnline())
            victim.sendMessage(SquatchPVPMessages.getVictimMsg(reward, victimRatio.name, killerRatio.name, String.valueOf(victimRatio.spirit)));
        killer.sendMessage(SquatchPVPMessages.getKillerMsg(reward, victimRatio.name, killerRatio.name, String.valueOf(killerRatio.spirit)));
    }
    

    private static double trim(double money) {
        if (whole)
            return (int)money;
        
        //Get rid of numbers after the 100ths decimal place
        return ((long)(money * 100)) / 100;
    }
}

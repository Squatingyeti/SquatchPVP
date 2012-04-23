package net.yeticraft.squatingyeti.SquatchPVP;

import org.bukkit.entity.Player;

public class Ratio {
	
	public static int hunterLevel;
	public static String hunterGroup;
	public String name;
	public int kills = 0;
    public int deaths = 0;
    public double kdr = 0;
    public int spirit = 0;
    public boolean inCombat = false;
    public String inCombatWith;
    private int instance = 0;
    public String group;
    public static boolean removeGroup;
    
    public Ratio(String name) {
    	this.name = name;
    }
    
    
    public Ratio(String name, int kills, int deaths, int spirit) {
    	this.name = name;
    	this.kills = kills;
    	this.deaths = deaths;
    	this.spirit = spirit;
    	
    	calculateKDR();
    }
    
    public void incrementKills() {
    	kills++;
    	calculateKDR();
    }
    
    public void incrementDeaths() {
    	deaths++;
    	calculateKDR();
    }
    
    private void calculateKDR() {
    	kdr = deaths == 0 ? kills : (double)kills /deaths;
    	
    	long tmp = (long)(kdr * 100);
    	kdr = (double)tmp /100;
    }
    
    public boolean incrementSpirit(Player player) {
    	if (!player.isOnline())
    		return false;
    	
    	spirit = spirit + 2;
    	
    	if (spirit != hunterLevel + 1 && spirit != hunterLevel +2)
    		return false;
    	
    	if (!hunterGroup.isEmpty()) {
    		if (removeGroup) {
    			
    			group = SquatchPVP.permission.getPrimaryGroup(player);
    			SquatchPVP.permission.playerRemoveGroup(player, group);
    		}
    		
    		SquatchPVP.permission.playerAddGroup(player, hunterGroup);
    	}
    	return true;
    }
    
    public boolean decrementSpirit(Player player) {
    	if (!player.isOnline())
    		return false;
    	
    	spirit--;
    	
    	if (spirit < 0)
    		spirit = 0;
    	
    	if (spirit != hunterLevel)
    		return false;
    	
    	if (SquatchPVP.permission.playerInGroup(player, hunterGroup)) {
    		SquatchPVP.permission.playerRemoveGroup(player, hunterGroup);
    		
    		if (group != null && removeGroup) {
    			SquatchPVP.permission.playerAddGroup(player, group);
    			group = null;
    		}
    	}
    	return true;
    }
    
    public boolean isHunter() {
    	return spirit > hunterLevel;
    }
    /*
    public void startCombat(String player) {
    	instance++;
    	final int THIS_INSTANCE = instance;
    	inCombat = true;
    	inCombatWith = player;
    	
    	Thread combat = new Thread() {
    		@Override
    		public void run() {
    			try {
    				
    				Thread.currentThread().sleep(combatTimeOut);
    			}
    		}
    	}
    } */
    
    public void resetCombat() {
    	inCombat = false;
    	inCombatWith = null;
    }
    
    public int compareTo(Ratio rat) {
    	if (kdr < rat.kdr)
    		return 1;
    	else if (kdr > rat.kdr)
    		return -1;
    	else
    		return 0;
    }
}
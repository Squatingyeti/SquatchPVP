package net.yeticraft.squatingyeti.SquatchPVP;

import java.util.HashMap;

import net.milkbowl.vault.permission.Permission;
import net.yeticraft.squatingyeti.SquatchPVP.SquatchPVPListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public class Ratio implements Comparable<Ratio> {
	
	public static Permission permission;
	public static int hunterLevel;
	public static int combatTimeOut;
	public static String hunterGroup;
	public static String hunterTag;
	public String name;
	public static long sneakTimeOut;
	public int kills = 0;
    public int deaths = 0;
    public double kdr = 0;
    public int spirit = 0;
    public boolean inCombat = false;
    public String inCombatWith;
    private int instance = 0;
    public String group;
    public static boolean removeGroup;
    static HashMap<String, Long> sneakList = new HashMap<String, Long>();
    
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
    	kdr = (double)kills / (deaths == 0 ? 1: deaths);
    	
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
    	if (!hunterTag.isEmpty())
    		player.setDisplayName(hunterTag + name);
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
    				if (instance == THIS_INSTANCE)
                        resetCombat();
    			}
    			catch (Exception e) {
    			}
    		}
    	};
    	combat.start();
    } 

    public void resetCombat() {
    	inCombat = false;
    	inCombatWith = null;
    }
    public static boolean sneakCheck(Player player) {
    	PermissionManager pexPlayer = PermissionsEx.getPermissionManager();
		PermissionUser pPlayer = pexPlayer.getUser(player);
		if (!player.hasPermission("squatchPVP.sneak")) {
			sneakList.remove(player.getName());
			player.sendMessage(ChatColor.AQUA + "sneakList removed at top");
			return false;
		} 
    	if (!sneakList.containsKey(player.getName())) {
    		sneakList.put(player.getName(), System.currentTimeMillis());
    		player.sendMessage(ChatColor.YELLOW + "You were added to sneakList");
    		return true;
    	}

    	if (System.currentTimeMillis() - sneakList.get(player.getName()) > sneakTimeOut) {
    		pPlayer.removePermission("squatchpvp.sneak");
    		player.sendMessage(ChatColor.RED + "Sneak permission removed");
    		sneakList.remove(player.getName());
    		player.sendMessage(ChatColor.YELLOW + "You were removed from sneakList");
    		return false;
    	}
    	if (sneakList.containsKey(player.getName())){
    		player.sendMessage(ChatColor.YELLOW + "sneakList already contains your name");
    		return true;
    	}
    	return true;
    }

    
    @Override
    public int compareTo(Ratio rat) {
    	if (kdr < rat.kdr)
    		return 1;
    	else if (kdr > rat.kdr)
    		return -1;
    	else
    		return 0;
    }
}
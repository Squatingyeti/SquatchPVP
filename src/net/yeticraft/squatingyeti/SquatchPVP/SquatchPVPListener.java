package net.yeticraft.squatingyeti.SquatchPVP;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;


public class SquatchPVPListener implements Listener {
	public static boolean disableFeeForPVP;
	public static LinkedList<String> feeDisabledIn;
	public static long sneakTimeOut;
	boolean timeoutReached = false;
	public static HashMap<Player, Long> hiddenTimer = new HashMap<Player, Long>();
	public static HashMap<Player, Boolean> hiddenState = new HashMap<Player, Boolean>();
	public static SquatchPVP plugin;
    
	public SquatchPVPListener(SquatchPVP plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		SquatchPVPListener.plugin = plugin;
	}
    
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		
		Entity wounded = event.getEntity();
		if (!(wounded instanceof Player))
			return;
		
		Entity attacker = event.getDamager();
		if (attacker instanceof Projectile)
			attacker = ((Projectile)attacker).getShooter();
		
		if (!(attacker instanceof Player))
			return;
		
		if (attacker.equals(wounded))
			return;
		
		Ratio ratio = SquatchPVP.getRatio(((Player)wounded).getName());
		ratio.startCombat(((Player)attacker).getName());
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		
		Entity entityKilled = event.getEntity();
		if (!(entityKilled instanceof Player))
			return;
		
		Player victim = (Player)entityKilled;
		Ratio ratio = SquatchPVP.getRatio(victim.getName());
		
		if (!ratio.inCombat) {
			Payer.dropMoney(victim);
			return;
		}
		
		if (!disableFeeForPVP)
			Payer.dropMoney(victim);
		
		if (!feeDisabledIn.contains(victim.getWorld().getName()))
			Payer.rewardPVP(victim, ratio);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		Player player = e.getPlayer();
		
		// Iterating through hidden players and hiding them from newly logged on player
		Iterator<Player> it = hiddenState.keySet().iterator();
		while (it.hasNext())
		{
		   Player hiddenPlayer = it.next();
		   if (hiddenState.get(hiddenPlayer)){
			   player.hidePlayer(hiddenPlayer);
		   }
		}
		
	}
	
	@EventHandler (priority = EventPriority.LOW) 
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		
		// Player not sneak capable.. return out
		if (!hiddenState.containsKey(player)) return; 
		
		// Hide state false : sneaking false (return, nothing to do) 
		if (!hiddenState.get(player) && !player.isSneaking()){
			return;
		}
		
		// Checking to see if they are too close to someone while hidden. (Only returns if someone is close.) 
		if (player.isSneaking()){
			
			for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
				
				// Skip the player
				if (onlinePlayer.equals(player)) continue; 

				// Show player if they are less than 7
				if (onlinePlayer.getLocation().distance(player.getLocation()) <= 7){
					hiddenState.put(player, false);
					toggleHideState(player);
					return;
				}
			}
			
		}
		
		// hide state true : sneaking false (update hiddenState to false and show player) 
		if (hiddenState.get(player) && !player.isSneaking()){
			hiddenState.put(player, false);
			toggleHideState(player);
			return;
		}

		// Hide state false : sneaking true (update hiddenState to true and hide player) 
		if (!hiddenState.get(player) && player.isSneaking()){
			// add them to the sneak timer if they don't already have an entry
			if (!hiddenTimer.containsKey(player)){
				hiddenTimer.put(player, System.currentTimeMillis());
			}
			
			hiddenState.put(player, true);
			toggleHideState(player);
			return;
			
		}

		// Hide state true: sneaking true (Check to see if their timer is up)
		long elapsedTime = System.currentTimeMillis() - hiddenTimer.get(player);
		if (elapsedTime > 20000){
			hiddenState.put(player, false);
			toggleHideState(player);
			hiddenTimer.remove(player);
			hiddenState.remove(player);
			player.sendMessage(ChatColor.GREEN + "You are no longer hiding.");
			return;
		}
		
		return;
		
	}

	/**
	 * This method toggles the hide state of a player for all online players.
	 * It checks their status in the sneak
	 * @param player
	 */
	public void toggleHideState(Player player){
		
		// Updating online players of this new players view status (hidden or shown)
		for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {

			// Skip the online player who is currently hidden
			if (onlinePlayer.equals(player)) continue; 
			
			// Hiding or showing based on entry in the hashmap
			if (hiddenState.get(player)) onlinePlayer.hidePlayer(player);
			else onlinePlayer.showPlayer(player);
			
		}
		
		
	}
	
	
}
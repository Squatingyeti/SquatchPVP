package net.yeticraft.squatingyeti.SquatchPVP;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public class SquatchPVPListener implements Listener {
	public static boolean disableFeeForPVP;
	public static LinkedList<String> feeDisabledIn;
	public static long sneakTimeOut;
	boolean timeoutReached = false;
	static HashMap<String, Long> sneakList = new HashMap<String, Long>();
    
	
    
	
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
	
	@EventHandler (priority = EventPriority.LOW) 
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		
		if (!player.hasPermission("squatchpvp.sneak")) return; // Player doesn't have permission
		if (System.currentTimeMillis() - sneakList.get(player.getName()) > sneakTimeOut) { // Player has surpassed the sneakTimeout
			PermissionManager pexPlayer = PermissionsEx.getPermissionManager();
			PermissionUser pPlayer = pexPlayer.getUser(player);
			pPlayer.removePermission("squatchpvp.sneak");
			timeoutReached = true;
		}
		if (!player.isSneaking()) return; // Player isn't sneaking
		if (sneakList.containsKey(player.getName())) { // If player is already hidden, we will see if he/she should be shown
			sneakState(player);
			return; 
		}
		
		//Player has permission, is sneaking, and is not in the sneakList... going to process him/her as a new user.
		sneakList.put(player.getName(), System.currentTimeMillis());
		timeoutReached = false;
		
	}

	public void sneakState(Player player){
		if(timeoutReached)sneakList.remove(player.getName());
		
		for (Player nearbyPlayer : Bukkit.getServer().getOnlinePlayers()) {
			
			if (nearbyPlayer.equals(player)) { // Skip 
				continue; 
			}
			
			if (timeoutReached) {// Player has surpassed the sneak timeout.. showing his yeti bits to everyone
				nearbyPlayer.showPlayer(player); 
				continue;
			}
			
			if (nearbyPlayer.getLocation().distance(player.getLocation()) < 7.0) { // PLayer is closer than 7 blocks
				nearbyPlayer.showPlayer(player);
				continue;
			}
			
			// At this point the player has permission, is sneaking, is on the sneaklist, is not the 
			// nearby player, is within the time limit, and is greater than 7 blocks away. Let's hide his hairy ass.
			nearbyPlayer.hidePlayer(player);
			
		}
		
	}
	
}
package net.yeticraft.squatingyeti.SquatchPVP;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;


public class SquatchPVPListener implements Listener {
	public static boolean disableFeeForPVP;
	public static LinkedList<String> feeDisabledIn;
	Set<String> hidden = new HashSet<String>();
    
	
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
		if (!player.hasPermission("squatchpvp.sneak") && (!hidden.contains(player.getName().toLowerCase()))) {
			player.sendMessage(ChatColor.RED + "No permission & no hidden hash");
			return;
		}
		if (!player.hasPermission("squatchpvp.sneak") && (hidden.contains(player.getName().toLowerCase()))){
			hidden.remove(player.getName().toLowerCase());
			player.sendMessage(ChatColor.RED + "No sneak permission");
			return;
		}
		
		if (!player.isSneaking() && (hidden.contains(player.getName().toLowerCase()))){ 
			player.sendMessage(ChatColor.YELLOW + "you are in the hidden hash, but not sneaking");
			show(player);
			return;
			/*for (Player other : Bukkit.getServer().getOnlinePlayers()) {
				if (!other.equals(player) && !other.canSee(player)) {
					other.showPlayer(player);
					return;
				}
			} */
		}
		if (player.isSneaking()) {
			if (Ratio.sneakCheck(player) == true && (hidden.contains(player.getName().toLowerCase()))) {
				player.sendMessage(ChatColor.GREEN + "sneakCheck true & already hidden hash");
				hide(player);
				distCheck(player);
				return;
			}
			
			if (Ratio.sneakCheck(player) == true && (!hidden.contains(player.getName().toLowerCase()))) {
				hidden.add(player.getName().toLowerCase());
				player.sendMessage(ChatColor.GREEN + "added to hidden hash");
				player.sendMessage(ChatColor.YELLOW + "sneakCheck returned true");
				hide(player);
				distCheck(player);
				return;
				/*for (Player other : Bukkit.getServer().getOnlinePlayers()) {
				if (!other.equals(player) && other.canSee(player)) {
	            	other.hidePlayer(player);
	            	return;
					} */
				}

			
					
				if (Ratio.sneakCheck(player) == false) {
					hidden.remove(player.getName().toLowerCase());
					player.sendMessage(ChatColor.RED + "sneakCheck returned false");
					show(player);
					return;
					/*for (Player other : Bukkit.getServer().getOnlinePlayers()) {
						if (!other.equals(player) && !other.canSee(player)) {
							other.showPlayer(player);
							return;
						}
					} */
				}
				else {
					player.sendMessage(ChatColor.YELLOW + "already in hidden hash and sneaking");
					hide(player);
				}
			}
		}


	public void show(Player player) {
		for (Player other : Bukkit.getServer().getOnlinePlayers()) {
			if (!other.equals(player) && !other.canSee(player)) {
				other.showPlayer(player);
				player.sendMessage(ChatColor.YELLOW + "You were shown by the show method");
				return;
			}
		}
	}
	
	public void hide(Player player) {
		for (Player other : Bukkit.getServer().getOnlinePlayers()) {
			if (!other.equals(player) && other.canSee(player)) {
            	other.hidePlayer(player);
            	double dist = other.getLocation().distance(player.getLocation());
            	if (dist <= 7) 
            		show(player);
            	player.sendMessage(ChatColor.YELLOW + "You were hidden by the hide method");
            	return;
			}
		}
	}
	
	 public void updateHideState(Player player){
	        String playerName = player.getName();
	        Server server = Bukkit.getServer();
	        if (hidden.contains(playerName.toLowerCase())){
	        	hidden.remove(player.getName().toLowerCase());
	        	player.sendMessage(ChatColor.GREEN + "You were removed from hidden hash");
	        }
	        else{
	            for (Player looking : server.getOnlinePlayers()){
	                if (!looking.canSee(player)) looking.showPlayer(player);
           }
       }
	}
	public void distCheck(Player player) {
		for (Player other : Bukkit.getServer().getOnlinePlayers()) {
			double dist = other.getLocation().distance(player.getLocation());
        	if (dist <= 7) 
        		show(player);
        	player.sendMessage(ChatColor.YELLOW + "Distance too close...no longer hidden");
        	return;
		}
	}
}
package net.yeticraft.squatingyeti.SquatchPVP;

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

public class SquatchPVPListener implements Listener {
	public static boolean disableFeeForPVP;
	public static LinkedList<String> feeDisabledIn;

	
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
		if (!(player.isSneaking())){ 
			for (Player other : Bukkit.getServer().getOnlinePlayers()) {
				if (!other.equals(player) && !other.canSee(player)) {
					other.showPlayer(player);
					return;
				}
			}
		}
		
		if (!(player.hasPermission("squatchpvp.sneak")))
			return;
		if (player.isSneaking()) {
			if (Ratio.sneakCheck(player) == true) {
				player.hidePlayer(player);
				for (Player other : Bukkit.getServer().getOnlinePlayers()) {
				if (!other.equals(player) && other.canSee(player)) {
	            	other.hidePlayer(player);
	            	return;
					}
				}
					
				if (Ratio.sneakCheck(player) == false) {
					for (Player other : Bukkit.getServer().getOnlinePlayers()) {
						if (!other.equals(player) && !other.canSee(player)) {
							other.showPlayer(player);
							return;
						}
					}
				}
			}
		}
	}
}
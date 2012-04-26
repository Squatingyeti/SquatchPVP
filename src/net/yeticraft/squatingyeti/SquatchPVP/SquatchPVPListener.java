package net.yeticraft.squatingyeti.SquatchPVP;

import java.util.HashSet;
import java.util.Set;

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
		//ratio.startCombat(((Player)attacker).getName());
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
	}
	
	@EventHandler (priority = EventPriority.MONITOR) 
		public void onPlayerMoveEvent(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		if (!(player.isSneaking()))
			return;
		if (!(player.hasPermission("squatchpvp.sneak")))
			return;
		Ratio.sneakHide(player);
	}
	
}

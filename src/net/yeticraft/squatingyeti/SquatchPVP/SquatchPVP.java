package net.yeticraft.squatingyeti.SquatchPVP;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import net.yeticraft.squatingyeti.SquatchPVP.Payer.PayType;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SquatchPVP extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");
	
	public static Server server;
	public static Permission permission;
	public static PluginManager pm;
	public static boolean negative;
	public static String spiritName;
	public static String hunterName;
	public static int cooldownTime;
	public static Properties p;
	public static HashMap<String, Ratio> ratios = new HashMap<String, Ratio>();
	public static String dataFolder;
	
	
	public void onDisable() {
		log.info("[SquatchPVP] has been disabled");
	}
	
	@Override
	public void onEnable() {
		server = getServer();
		pm = server.getPluginManager();
		
		File dir = this.getDataFolder();
		if (!dir.isDirectory());
			dir.mkdir();
			
		dataFolder = dir.getPath();
		
		loadSettings();
		
		RegisteredServiceProvider<Permission> permissionProvider = 
				getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null)
			permission = permissionProvider.getProvider();
		
		RegisteredServiceProvider<Economy> economyProvider = 
				getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			Econ.economy = economyProvider.getProvider();
		
		loadData();
		
		pm.registerEvents(new SquatchPVPListener(), this);
		
		SquatchPVPCommand.command = (String)this.getDescription().getCommands().keySet().toArray()[0];
        getCommand(SquatchPVPCommand.command).setExecutor(new SquatchPVPCommand());
		
		log.info("[SquatchPVP] is enabled");
		
		if (cooldownTime != 0)
			cooldown();
	}
	
	public void loadSettings() {
		try {
			
			File file = new File(dataFolder + "/config.properties");
			if (!file.exists())
				this.saveResource("config.properties", true);
			
			p = new Properties();
			FileInputStream fis = new FileInputStream(file);
			p.load(fis);
			
			SquatchPVPMessages.setVictimMsg(loadValue("VictimMessage"));
			SquatchPVPMessages.setKillerMsg(loadValue("KillerMessage"));
			SquatchPVPMessages.setVictimNotEnoughMsg(loadValue("VictimNotEnoughMoney"));
			SquatchPVPMessages.setKillerNotEnoughMsg(loadValue("KillerNotEnoughMoney"));
			SquatchPVPMessages.setHunterBroadcast(loadValue("HunterBroadcast"));
			SquatchPVPMessages.setHunterNoMoreBroadcast(loadValue("HunterNoMoreBroadcast"));
			SquatchPVPMessages.setSpiritDecreasedMsg(loadValue("SpiritDecreased"));
			SquatchPVPMessages.setSpiritIncreasedMsg(loadValue("SpiritIncreased"));
			SquatchPVPMessages.setSpiritNoChangeMsg(loadValue("SpiritNoChange"));
			SquatchPVPMessages.setSneakMsg(loadValue("SneakMessage"));
			
			String feeType = loadValue("DeathFeeType");
			if (feeType.equalsIgnoreCase("none")) {
				Payer.feeAsPercent = false;
				Payer.feeAmount = 0;
			}
			else {
				SquatchPVPMessages.setDeathFeeMsg(loadValue("DeathFeeMessage"));
				Payer.feeAmount = Double.parseDouble(loadValue("DeathFee"));
				SquatchPVPListener.disableFeeForPVP = Boolean.parseBoolean(loadValue("DisableFeeForPVP"));
				
				if (feeType.equalsIgnoreCase("percent"))
					Payer.feeAsPercent = true;
				else if (feeType.equalsIgnoreCase("flatfee")) {
					Payer.feeAsPercent = false;
					Payer.feeAmount = Double.parseDouble(loadValue("DeathFee"));
				}
			}
			
			SquatchPVPListener.disableFeeForPVP = Boolean.parseBoolean(loadValue("DisableFeeForPVP"));
			
			spiritName = loadValue("SpiritName");
			hunterName = loadValue("HunterName");
			cooldownTime = Integer.parseInt(loadValue("CooldownTime")) * 20;
			
			Payer.payType = PayType.valueOf(loadValue("PayType").toUpperCase().replace(" ", ""));
			Payer.percent = Integer.parseInt(loadValue("Percent"));
			
			Payer.amount = Double.parseDouble(loadValue("Amount"));
			Ratio.hunterLevel = (int)Payer.amount;	
			
			Payer.hi = Integer.parseInt(loadValue("High"));
			Payer.lo = Integer.parseInt(loadValue("Low"));
			
			Payer.threshold = Integer.parseInt(loadValue("SpiritThreshold"));
			Payer.modifier = Integer.parseInt(loadValue("SpiritModifier")) / 100;
			Payer.max = Integer.parseInt(loadValue("ModifierMax")) / 100;
			Payer.whole = Boolean.parseBoolean(loadValue("WholeNumbers"));
			
			negative = Boolean.parseBoolean(loadValue("Negative"));
			
			Ratio.hunterGroup = loadValue("HunterGroup");
			Ratio.removeGroup = Boolean.parseBoolean(loadValue("RemoveFromCurrentGroup"));
			
			fis.close();
		}
		catch (Exception missingProp) {
			System.err.println("Failed to load SquatchPVP");
			missingProp.printStackTrace();
		}
	}
	
	private String loadValue(String key) {
		
		if (!p.containsKey(key)) {
			System.err.println("[SquatchPVP] Missing value for " + key + " in config");
			System.err.println("[SquatchPVP] Regen config file");
		}
		return p.getProperty(key);
	}
	
	public static boolean hasPermission(Player killer, Player victim) {
		return permission.has(killer, "squatchpvp.getpay") && permission.has(victim, "squatchpvp.givepay");
	}
	
	public static boolean hasPermission(Player player, String node) {
		return permission.has(player, "squatchpvp."+node);
	}
	
	public void cooldown() {
		server.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Ratio ratio: ratios.values()) {
					Player player = server.getPlayer(ratio.name);
					if (player != null)
						ratio.decrementSpirit(player);
				}
				
				save();
			}
		}, 0L, new Long(cooldownTime));
	}
	
	private static void loadData() {
		try {
			File file = new File(dataFolder + "/squatchpvp.ratios");
			if (!file.exists()) {
				File old = new File(dataFolder + "/squatchpvp.save");
				if (old.exists())
					old.renameTo(file);
				else
					return;
			}
			
			BufferedReader bReader = new BufferedReader(new FileReader(file));
			String line = bReader.readLine();
			while (line != null) {
				try {
					String[] split = line.split(";");
					
					String player = split[0];
					int kills = Integer.parseInt(split[1]);
					int deaths = Integer.parseInt(split[2]);
					int spirit = Integer.parseInt(split[3]);
					
					Ratio ratio = new Ratio(player, kills, deaths, spirit);
					ratios.put(player, ratio);
					
					if (split.length == 5)
						ratio.group = split[4];
					
					line = bReader.readLine();
				}
				catch (Exception corruptData) {
					/*do nothing*/
				}
			}
			bReader.close();
		}
		catch (Exception loadFailed) {
			System.out.println("[SquatchPVP] Load failure");
			loadFailed.printStackTrace();
		}
	}
	
	public static void save() {
		try {
			File file = new File(dataFolder + "/squatchpvp.ratios");
			if (!file.exists())
				file.createNewFile();
			
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(dataFolder + "/squatchpvp.ratios"));
			for (Ratio ratio: ratios.values()) {
				//format "name;kills;deaths;spirit(;group)
				bWriter.write(ratio.name.concat(";"));
				bWriter.write(ratio.kills + ";");
				bWriter.write(ratio.deaths + ";");
				bWriter.write(String.valueOf(ratio.spirit));
				
				if (ratio.group != null)
					bWriter.write(";"+ratio.group);
				
				bWriter.newLine();
			}
			
			bWriter.close();
		}
		catch (Exception saveFailed) {
			System.err.println("[SquatchPVP] Failed to save!");
			saveFailed.printStackTrace();
		}
	}
	
	public static Ratio getRatio(String player) {
		for (Ratio ratio: ratios.values())
			if (ratio.name.equals(player))
				return ratio;
		
		Ratio newRatio = new Ratio(player);
		ratios.put(player, newRatio);
		return newRatio;
	}
	
	public static Ratio findRatio(String player) {
		for (Ratio ratio: ratios.values())
			if (ratio.name.equals(player))
				return ratio;
		
		return null;
	}

	public static LinkedList<Ratio> getRatios() {
		LinkedList<Ratio> ratioList = new LinkedList(ratios.values());
		Collections.sort(ratioList);
		return ratioList;
	}
}

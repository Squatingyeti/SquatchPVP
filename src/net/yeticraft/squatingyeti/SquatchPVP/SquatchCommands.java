package net.yeticraft.squatingyeti.SquatchPVP;

import java.util.Iterator;
import java.util.LinkedList;

import net.yeticraft.squatingyeti.SquatchPVP.Payer.PayType;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class SquatchCommands implements CommandExecutor {
	public static Permission permission;
	
	 private static enum Action { HELP, HUNTERS, SPIRIT, KDR, RANK, SNEAK, TOP, RESET }
	    static String command;
	    
	    /**
	* Listens for SquatchPVP commands to execute them
	*
	* @param sender The CommandSender who may not be a Player
	* @param command The command that was executed
	* @param alias The alias that the sender used
	* @param args The arguments for the command
	* @return true always
	*/
	    @Override
	    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
	        //Cancel if the command is not from a Player
	        if (!(sender instanceof Player))
	            return true;
	        
	        Player player = (Player)sender;
	        PermissionManager pexPlayer = PermissionsEx.getPermissionManager();
			PermissionUser pPlayer = pexPlayer.getUser(player);
	        //Display the help page if the Player did not add any arguments
	        if (args.length == 0) {
	            sendHelp(player);
	            return true;
	        }
	        
	        Action action;
	        
	        try {
	            action = Action.valueOf(args[0].toUpperCase());
	        }
	        catch (Exception notEnum) {
	            sendHelp(player);
	            return true;
	        }
	        
	        //Execute the correct command
	        switch (action) {
	            case HUNTERS:
	                if (args.length == 1)
	                    hunters(player);
	                else
	                    sendHelp(player);
	                
	                return true;
	                
	            case SPIRIT:
	                switch (args.length) {
	                    case 1: spirit(player, player.getName()); return true;
	                    case 2: spirit(player, args[1]); return true;
	                    default: sendHelp(player); return true;
	                }
	                
	            case KDR:
	                switch (args.length) {
	                    case 1: kdr(player, player.getName()); return true;
	                    case 2: kdr(player, args[1]); return true;
	                    default: sendHelp(player); return true;
	                }
	                
	            case RANK:
	                switch (args.length) {
	                    case 1: rank(player, player.getName()); return true;
	                    case 2: rank(player, args[1]); return true;
	                    default: sendHelp(player); return true;
	                }
	            
	            case SNEAK:
	            	String pName = player.getName();
	            	double sneakFee = 75;
	            	if (args.length == 1) {
	            		if (Econ.takeMoney(pName, sneakFee) == false) {
	            			player.sendMessage("You do not have enough TPs to sneak");
	            			return true;
	            		}
	            		else if (Econ.takeMoney(pName, sneakFee) == true) {
	            			player.sendMessage("player is" + pName);
	            			player.sendMessage(+ sneakFee + " removed from your account");
	            			pPlayer.addPermission("squatchpvp.sneak");
	            			Ratio.sneakList.remove(player.getName());
	            			player.sendMessage(ChatColor.GREEN + "sneak permission added");
	            			return true;
	            			
	            		}
	            		
	            	}
	            	
	            case TOP:
	                switch (args.length) {
	                    case 1: top(player, 5); return true;
	                        
	                    case 2:
	                        try {
	                            top(player, Integer.parseInt(args[1]));
	                            return true;
	                        }
	                        catch (Exception notInt) {
	                            break;
	                        }
	                        
	                    default: break;
	                }
	                
	                sendHelp(player);
	                return true;
	                
	            case RESET:
	                switch (args.length) {
	                    case 2:
	                        if (args[1].equals("kdr"))
	                            reset(player, true, player.getName());
	                        else if (args[1].equals("spirit"))
	                            reset(player, false, player.getName());
	                        else
	                            break;
	                        
	                        return true;
	                        
	                    case 3:
	                        if (args[1].equals("kdr"))
	                            reset(player, true, args[2]);
	                        else if (args[1].equals("spirit"))
	                            reset(player, false, args[2]);
	                        else
	                            break;
	                        
	                        return true;
	                        
	                    default: break;
	                }
	                
	                sendResetHelp(player);
	                return true;
	                
	            default: sendHelp(player); return true;
	        }
	    }
	    
	    /**
	* Displays the current Hunters
	*
	* @param player The Player executing the command
	*/
	    private static void hunters(Player player) {
	        String hunters = "§eCurrent "+SquatchPVP.hunterName+"s:§2 ";
	        
	        //Append the name of each Outlaw
	        for (Ratio ratio: SquatchPVP.getRatios())
	            if (ratio.isHunter())
	                hunters = hunters.concat(ratio.name+", ");
	        
	        player.sendMessage(hunters.substring(0, hunters.length() - 2));
	    }
	    
	    /**
	* Displays the current spirit value of the specified Ratio
	*
	* @param player The Player executing the command
	* @param name The name of the Ratio
	*/
	    private static void spirit(Player player, String name) {
	        //Return if the Ratio does not exist
	        Ratio ratio = SquatchPVP.findRatio(name);
	        if (ratio == null) {
	            player.sendMessage("No PvP Ratio found for "+name);
	            return;
	        }
	        
	        //Add '-' before the spirit values if negative is set to true
	        if (SquatchPVP.negative && ratio.spirit != 0) {
	            player.sendMessage("§2Current "+SquatchPVP.spiritName+" level:§b -"+ratio.spirit);
	            player.sendMessage("§2"+SquatchPVP.hunterName+" status at §b-"+Ratio.hunterLevel);
	        }
	        else {
	            player.sendMessage("§2Current "+SquatchPVP.spiritName+" level:§b "+ratio.spirit);
	            player.sendMessage("§2"+SquatchPVP.hunterName+" status at §b"+Ratio.hunterLevel);
	        }
	    }
	    
	    /**
	* Displays the current kdr of the specified Ratio
	*
	* @param player The Player executing the command
	* @param name The name of the Ratio
	*/
	    private static void kdr(Player player, String name) {
	        //Return if the Ratio does not exist
	        Ratio ratio = SquatchPVP.findRatio(name);
	        if (ratio == null) {
	            player.sendMessage("No PvP Ratio found for "+name);
	            return;
	        }
	        
	        player.sendMessage("§2Current Kills:§b "+ratio.kills);
	        player.sendMessage("§2Current Deaths:§b "+ratio.deaths);
	        player.sendMessage("§2Current KDR:§b "+ratio.kdr);
	    }
	    
	    /**
	* Displays the current kdr rank of the specified Ratio
	*
	* @param player The Player executing the command
	* @param name The name of the Ratio
	*/
	    private static void rank(Player player, String name) {
	        int rank = 1;
	        String playerName = player.getName();
	        
	        //Increase rank by one for each Ratio that has a higher kdr
	        for (Ratio ratio: SquatchPVP.getRatios())
	            if (ratio.name.equals(playerName)) {
	                player.sendMessage("§2Current Rank:§b "+rank);
	                return;
	            }
	            else
	                rank++;
	        
	        player.sendMessage("No PvP Ratio found for "+name);
	    }
	    
	    /**
	* Displays the top KDRs
	*
	* @param player The Player executing the command
	* @param amount The amount of KDRs to be displayed
	*/
	    private static void top(Player player, int amount) {
	        player.sendMessage("§eKDR Leaderboard:");
	        
	        //Sort the Ratios
	        LinkedList<Ratio> ratios = SquatchPVP.getRatios();
	        
	        //Verify that amount is not too big
	        int size = ratios.size();
	        if (amount > size)
	            amount = size;
	        
	        Iterator<Ratio> itr = SquatchPVP.getRatios().iterator();
	        Ratio ratio;
	        
	        //Display the name and KDR of the first x Ratios
	        for (int i = 0; i < amount; i++) {
	            ratio = itr.next();
	            player.sendMessage("§2"+ratio.name+":§b "+ratio.kdr);
	        }
	    }
	    
	    /**
	* Resets kdr or spirit values
	*
	* @param player The Player executing the command
	* @param kdr True if reseting kdr, false if reseting spirit
	* @param name The name of the Ratio, 'all' to specify all Ratios,
	* or null to specify the ratio of the given player
	*/
	    private static void reset(Player player, boolean kdr, String name) {
	        //Cancel if the Player does not have the proper permissions
	        if (!SquatchPVP.hasPermission(player, "reset")) {
	            player.sendMessage("You do not have permission to do that.");
	            return;
	        }
	        
	        if (kdr) //Reset kdr
	            if (name.equals("all")) //Reset all Ratios
	                for (Ratio ratio: SquatchPVP.getRatios()) {
	                    ratio.kills = 0;
	                    ratio.deaths = 0;
	                    ratio.kdr = 0;
	                }
	            else { //Reset a specified Ratio
	                //Use the Ratio of the given Player if name is null
	                if (name == null)
	                    name = player.getName();
	                
	                //Return if the Ratio does not exist
	                Ratio ratio = SquatchPVP.findRatio(name);
	                if (ratio == null) {
	                    player.sendMessage("No PvP Ratio found for "+name);
	                    return;
	                }
	                
	                ratio.kills = 0;
	                ratio.deaths = 0;
	                ratio.kdr = 0;
	            }
	        else //Reset spirit
	            if (name.equals("all")) //Reset all Ratios
	                for (Ratio ratio: SquatchPVP.getRatios())
	                    while (ratio.spirit != 0)
	                        ratio.decrementSpirit(player);
	            else { //Reset a specified Ratio
	                //Use the Ratio of the given Player if name is null
	                if (name == null)
	                    name = player.getName();
	                
	                //Return if the Ratio does not exist
	                Ratio ratio = SquatchPVP.findRatio(name);
	                if (ratio == null) {
	                    player.sendMessage("No PvP Ratio found for "+name);
	                    return;
	                }
	                
	                while (ratio.spirit != 0)
	                    ratio.decrementSpirit(player);
	            }
	        
	        SquatchPVP.save();
	    }
	    
	    /**
	* Displays the Reset Help Page to the given Player
	*
	* @param player The Player needing help
	*/
	    private static void sendResetHelp(Player player) {
	        player.sendMessage("§e SquatchPVP Reset Help Page:");
	        player.sendMessage("§2/"+command+" reset kdr (Player)§b Set kills and deaths to 0");
	        player.sendMessage("§2/"+command+" reset kdr all§b Set everyone's kills and deaths to 0");
	        
	        //Only display spirit commands if the reward type is set to spirit
	        if (Payer.payType.equals(PayType.SPIRIT)) {
	            player.sendMessage("§2/"+command+" reset "+SquatchPVP.spiritName+" (Player)§b Set "+SquatchPVP.spiritName+" level to 0");
	            player.sendMessage("§2/"+command+" reset "+SquatchPVP.spiritName+" all§b Set everyone's "+SquatchPVP.spiritName+" level to 0");
	        }
	    }
	    
	    /**
	* Displays the SquatchPVP Help Page to the given Player
	*
	* @param player The Player needing help
	*/
	    private static void sendHelp(Player player) {
	        player.sendMessage("§e SquatchPVP Help Page:");
	        
	        //Only display spirit commands if the reward type is set to spirit
	        if (Payer.payType.equals(PayType.SPIRIT)) {
	            player.sendMessage("§2/"+command+" "+SquatchPVP.hunterName+"s§b List current "+SquatchPVP.hunterName+"s");
	            player.sendMessage("§2/"+command+" "+SquatchPVP.spiritName+" (Player)§b List current "+SquatchPVP.spiritName+" level");
	        }
	        
	        player.sendMessage("§2/"+command+" kdr (Player)§b List current KDR");
	        player.sendMessage("§2/"+command+" rank (Player)§b List current rank");
	        player.sendMessage("§2/"+command+" top (amount)§b List top x KDRs");
	        player.sendMessage("§2/"+command+" reset§b List Admin reset commands");
	    }
	}
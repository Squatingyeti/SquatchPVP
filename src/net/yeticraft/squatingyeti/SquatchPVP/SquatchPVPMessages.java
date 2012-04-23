package net.yeticraft.squatingyeti.SquatchPVP;

public class SquatchPVPMessages {
	
	private static String victim;
	private static String killer;
	private static String victimNotEnough;
	private static String killerNotEnough;
	private static String hunterBroadcast;
	private static String hunterNoMoreBroadcast;
	private static String spiritDecreased;
	private static String spiritIncreased;
	private static String spiritNoChange;
	private static String sneak;
	private static String deathFee;
	
	
	public static void setVictimMsg(String msg) {
        victim = format(msg);
    }
    
    public static void setKillerMsg(String msg) {
        killer = format(msg);
    }
    
    public static void setVictimNotEnoughMsg(String msg) {
        victimNotEnough = format(msg);
    }
    
    public static void setKillerNotEnoughMsg(String msg) {
        killerNotEnough = format(msg);
    }
    
    public static void setHunterBroadcast(String msg) {
        hunterBroadcast = format(msg);
    }
    
    public static void setHunterNoMoreBroadcast(String msg) {
        hunterNoMoreBroadcast = format(msg);
    }
    
    public static void setSpiritDecreasedMsg(String msg) {
        spiritDecreased = format(msg);
    }
    
    public static void setSpiritIncreasedMsg(String msg) {
        spiritIncreased = format(msg);
    }
    
    public static void setSpiritNoChangeMsg(String msg) {
        spiritNoChange = format(msg);
    }
    
    public static void setDeathFeeMsg(String msg) {
        deathFee = format(msg);
    }
    
    public static void sneakMsg(String msg) {
    	sneak = format(msg);
    }
    
    public static String getVictimMsg(double amount, String killed, String killer, String spirit) {
        String msg = victim.replace("<killed>", killed).replace("<killer>", killer);
        
        //Add '-' before spirit values if negative is set to true
        if (SquatchPVP.negative && !spirit.equals("0"))
            msg = msg.replace("<spirit>", "-"+spirit);
        else
            msg = msg.replace("<spirit>", spirit);
        
        return msg.replace("<amount>", Econ.format(amount));
    }
    

    public static String getKillerMsg(double amount, String killed, String killerName, String spirit) {
        String msg = killer.replace("<killed>", killed).replace("<killer>", killerName);
        
        //Add '-' before spirit values if negative is set to true
        if (SquatchPVP.negative && !spirit.equals("0"))
            msg = msg.replace("<spirit>", "-"+spirit);
        else
            msg = msg.replace("<spirit>", spirit);
        
        return msg.replace("<amount>", Econ.format(amount));
    }

    public static String getVictimNotEnoughMoneyMsg(double amount, String killed, String killer, String spirit) {
        String msg = victimNotEnough.replace("<killed>", killed).replace("<killer>", killer);
        
        //Add '-' before spirit values if negative is set to true
        if (SquatchPVP.negative && !spirit.equals("0"))
            msg = msg.replace("<spirit>", "-"+spirit);
        else
            msg = msg.replace("<spirit>", spirit);
        
        return msg.replace("<amount>", Econ.format(amount));
    }

    public static String getKillerNotEnoughMoneyMsg(double amount, String killed, String killer, String spirit) {
        String msg = killerNotEnough.replace("<killed>", killed).replace("<killer>", killer);
        
        //Add '-' before spirit values if negative is set to true
        if (SquatchPVP.negative && !spirit.equals("0"))
            msg = msg.replace("<spirit>", "-"+spirit);
        else
            msg = msg.replace("<spirit>", spirit);
        
        return msg.replace("<amount>", Econ.format(amount));
    }

    public static String getHunterBroadcast(String killed, String killer, String spirit) {
        String msg = hunterBroadcast.replace("<killed>", killed).replace("<killer>", killer);
        
        //Add '-' before spirit values if negative is set to true
        if (SquatchPVP.negative && !spirit.equals("0"))
            msg = msg.replace("<spirit>", "-"+spirit);
        else
            msg = msg.replace("<spirit>", spirit);
        
        return msg;
    }
    
    public static String getHunterNoMoreBroadcast(String killed, String killer, String spirit) {
        String msg = hunterNoMoreBroadcast.replace("<killed>", killed).replace("<killer>", killer);
        
        //Add '-' before spirit values if negative is set to true
        if (SquatchPVP.negative && !spirit.equals("0"))
            msg = msg.replace("<spirit>", "-"+spirit);
        else
            msg = msg.replace("<spirit>", spirit);
        
        return msg;
    }

    public static String getSpiritDecreasedMsg(String killed, String killer, String spirit) {
        String msg = spiritDecreased.replace("<killed>", killed).replace("<killer>", killer);
        
        //Add '-' before spirit values if negative is set to true
        if (SquatchPVP.negative && !spirit.equals("0"))
            msg = msg.replace("<spirit>", "-"+spirit);
        else
            msg = msg.replace("<spirit>", spirit);
        
        return msg;
    }

    public static String getSpiritIncreasedMsg(String killed, String killer, String spirit) {
        String msg = spiritIncreased.replace("<killed>", killed).replace("<killer>", killer);
        
        //Add '-' before spirit values if negative is set to true
        if (SquatchPVP.negative && !spirit.equals("0"))
            msg = msg.replace("<spirit>", "-"+spirit);
        else
            msg = msg.replace("<spirit>", spirit);
        
        return msg;
    }

    public static String getSpiritNoChangeMsg(String killed, String killer, String spirit) {
        String msg = spiritNoChange.replace("<killed>", killed).replace("<killer>", killer);
        
        //Add '-' before spirit values if negative is set to true
        if (SquatchPVP.negative && !spirit.equals("0"))
            msg = msg.replace("<spirit>", "-"+spirit);
        else
            msg = msg.replace("<spirit>", spirit);
        
        return msg;
    }
    
    public static String getDeathFeeMsg(double amount) {
        return deathFee.replace("<amount>", Econ.format(amount));
    }

    static String format(String string) {
        return string.replaceAll("&", "§").replaceAll("<ae>", "æ").replaceAll("<AE>", "Æ")
                .replaceAll("<o/>", "ø").replaceAll("<O/>", "Ø")
                .replaceAll("<a>", "å").replaceAll("<A>", "Å");
    }
}
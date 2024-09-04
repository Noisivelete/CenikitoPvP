/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import net.noisivelet.cenikito.cenikitopvp.PvP;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.mercyRule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Francis
 */
public class MercyRuleSwitch implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(strings.length != 2) return false;
        String str = strings[0];
        Player p = Bukkit.getPlayer(str);
        Long minutos = Long.valueOf(strings[1]);
        PvP.addToMercyRule(p, System.currentTimeMillis()+minutos*60*1000, true);
        
        cs.sendMessage("OK");
        return true;
    }
    
}

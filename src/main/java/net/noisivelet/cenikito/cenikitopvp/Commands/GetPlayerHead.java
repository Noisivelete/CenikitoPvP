/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import net.noisivelet.cenikito.cenikitopvp.SpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Francis
 */
public class GetPlayerHead implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!cs.isOp()) return false;
        
        Player p = Bukkit.getPlayer(strings[0]);
        if(cs instanceof Player executor){
            executor.getInventory().addItem(SpigotPlugin.getHead(p));
            cs.sendMessage("OK");
        } else {
            cs.sendMessage("No disponible en consola.");
        }
        return true;
    }
    
}

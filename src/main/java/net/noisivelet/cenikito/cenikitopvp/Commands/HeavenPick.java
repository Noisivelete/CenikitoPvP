/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import java.util.UUID;
import net.noisivelet.cenikito.cenikitopvp.SpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Francis
 */
public class HeavenPick implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!cs.isOp()){
            cs.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"No tienes permiso para ejecutar ese comando.");
            return true;
        }
        
        if(strings.length != 1) return false;
        if(strings[0].equals(".everyone")){
            SpigotPlugin.heavenlyPickaxeTask();
            return true;
        }
        Player p = Bukkit.getPlayer(strings[0]);
        
        ItemStack heavenPick = SpigotPlugin.getHeavenPick(p.getUniqueId());
        
        p.getInventory().addItem(heavenPick);
        cs.sendMessage("OK");
        return true;
    }
    
}

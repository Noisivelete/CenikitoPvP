/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Francis
 */
public class CheckClosestDistance implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!(cs instanceof Player)) return false;
        Player player = (Player)cs;
        Location playerLocation = player.getLocation();
        World playerWorld = player.getWorld();
        double smallestDistance = 999999;
        for(Player itPlayer : Bukkit.getOnlinePlayers()){
            if(!player.equals(itPlayer)){
                World itPlayerWorld = itPlayer.getWorld();
                if(playerWorld.equals(itPlayerWorld)){
                    Location itPlayerLocation = itPlayer.getLocation();
                    double distance = playerLocation.distanceSquared(itPlayerLocation);
                    if(distance < smallestDistance)
                        smallestDistance = distance;
                }
            }
            
        }
        
        cs.sendMessage(ChatColor.YELLOW+"Menor distancia a un jugador: " + ChatColor.RED+smallestDistance);
        return true;
    }
    
}

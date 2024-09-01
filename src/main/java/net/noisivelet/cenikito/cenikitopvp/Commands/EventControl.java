/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.noisivelet.cenikito.cenikitopvp.SpigotPlugin;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.CONFIG;
import net.noisivelet.cenikito.cenikitopvp.utils.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Francis
 */
public class EventControl{
    public static void pvp() throws SQLException {
        boolean active = CONFIG.get(PluginConfig.Key.IS_PVP_ENABLED).equals("1");
        if(active){
            for(Player p : Bukkit.getOnlinePlayers()){
                SpigotPlugin.addToSafeTeam(p);
                p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"¡PvP desactivado! Hasta que otro evento lo active, no se permite hacer daño a otros jugadores.");
            }
        } else {
            SpigotPlugin.sendAlert(""+ChatColor.RED+ChatColor.BOLD+"¡PvP está activado!");
            for(Player p : Bukkit.getOnlinePlayers()){
                SpigotPlugin.addToPvPTeam(p);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 1, 1);
            }
        }
        
        CONFIG.store(PluginConfig.Key.IS_PVP_ENABLED, active?"0":"1");
        
    }
    
    public static void nether() throws SQLException {
        CONFIG.store(PluginConfig.Key.IS_NETHER_ENABLED, "1");
        SpigotPlugin.sendAlert(""+ChatColor.RED+ChatColor.BOLD+"¡El Nether está activado!");
        for(Player p : Bukkit.getOnlinePlayers()){
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 1, 1);
        }
    }
    
    public static void end() throws SQLException {
        CONFIG.store(PluginConfig.Key.IS_END_ENABLED, "1");
        SpigotPlugin.sendAlert(""+ChatColor.DARK_PURPLE+ChatColor.BOLD+"¡El End está activado!");
        for(Player p : Bukkit.getOnlinePlayers()){
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 1, 1);
        }
    }
    
    public static void close(){
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "whitelist on");
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!p.isOp())
                p.kickPlayer(ChatColor.YELLOW+"¡El servidor se cierra por hoy! ¡Gracias por jugar!");
        }
    }
    
}

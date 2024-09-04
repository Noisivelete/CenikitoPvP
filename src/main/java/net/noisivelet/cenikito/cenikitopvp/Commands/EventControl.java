/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import java.sql.SQLException;
import net.noisivelet.cenikito.cenikitopvp.Sidebar;
import net.noisivelet.cenikito.cenikitopvp.SpigotPlugin;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.CONFIG;
import net.noisivelet.cenikito.cenikitopvp.utils.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

/**
 *
 * @author Francis
 */
public class EventControl{
    public static void start() throws SQLException {
        Bukkit.getWorld("cenikitopvp").getWorldBorder().setSize(5000, 420);
        Bukkit.getWorld("cenikitopvp").setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        Sidebar.setNextEventTime(System.currentTimeMillis()+60*60*2*1000);
        Sidebar.setNextEventPayload("pvp");
        Sidebar.setNextEventName("Se activa PvP");
        Sidebar.updateSidebar();
    }
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
            if(CONFIG.get(PluginConfig.Key.IS_NETHER_ENABLED).equals("0")){
                Sidebar.setNextEventTime(System.currentTimeMillis()+60*1000);
                Sidebar.setNextEventPayload("nether");
                Sidebar.setNextEventName("El Nether se activa");
                Sidebar.updateSidebar();
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

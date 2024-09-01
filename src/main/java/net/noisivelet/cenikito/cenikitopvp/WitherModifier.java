/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

/**
 *
 * @author Francis
 */
public class WitherModifier implements Listener {
    @EventHandler
    public void onWitherSpawn(EntitySpawnEvent event){
        Entity e = event.getEntity();
        if(e.getType() != EntityType.WITHER) return;
        
        Wither w = (Wither)e;
        ((LivingEntity)e).setCollidable(false);
        
        w.setInvulnerabilityTicks(300*20);
        Location l = event.getLocation();
        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"¡Un "+ChatColor.RED+"Wither"+ChatColor.YELLOW+" va a aparecer en el Nether "+ChatColor.LIGHT_PURPLE+"("+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ()+")"+ChatColor.YELLOW+" en 5 minutos!");
        }
        
    }
}

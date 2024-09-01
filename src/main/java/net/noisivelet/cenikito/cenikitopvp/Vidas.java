/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 *
 * @author Francis
 */
public class Vidas implements Listener{
    ConcurrentHashMap<UUID, Integer> vidas = new ConcurrentHashMap<>();
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player p = event.getEntity();
        if(!vidas.containsKey(p.getUniqueId())){
            vidas.put(p.getUniqueId(), 10);
        }
        vidas.put(p.getUniqueId(), vidas.get(p.getUniqueId())-1);
        
        p.sendMessage("");
    }
    
}

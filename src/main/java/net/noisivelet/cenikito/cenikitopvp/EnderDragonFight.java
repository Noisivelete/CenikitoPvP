/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.block.data.type.Bed;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 *
 * @author Francis
 */
public class EnderDragonFight implements Listener{
    @EventHandler
    public void onPlayerEndTeleport(PlayerTeleportEvent event){
        if(event.getCause() != TeleportCause.END_PORTAL) return;
        event.getTo().getWorld().getChunkAt(new Location(event.getTo().getWorld(),0,0,0));
        DragonBattle dragon = event.getTo().getWorld().getEnderDragonBattle();
        if(!dragon.hasBeenPreviouslyKilled()){
            event.getPlayer().sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"La presencia del Dragón es abrumadora... "+ChatColor.RED+"No puedes oír más que sus latidos en la isla principal.");
            PvP.startDragonCombat(event.getPlayer());
        }
    }
    
    @EventHandler
    public void onBedPlaceEventEnd(BlockPlaceEvent event){
        if(!(event.getBlock().getBlockData() instanceof Bed)) return;
        DragonBattle dragon = event.getPlayer().getLocation().getWorld().getEnderDragonBattle();
        if(dragon != null && dragon.getEnderDragon() != null){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"¡Hay cosas más urgentes de las que ocuparse que dormir! ¡Mira al cielo!");
        }
    }
    
    @EventHandler
    public void onBedInteractEvent(PlayerInteractEvent event){
        if(event.getPlayer().getWorld().getEnvironment() != Environment.THE_END) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!(event.getClickedBlock().getBlockData() instanceof Bed)) return;
        DragonBattle dragon = event.getPlayer().getLocation().getWorld().getEnderDragonBattle();
        if(dragon != null && dragon.getEnderDragon() != null){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"¡Hay cosas más urgentes de las que ocuparse que dormir! ¡Mira al cielo!");
            
        }
    }
    
    @EventHandler
    public void onDragonRespawn(EntitySpawnEvent event){
        if(event.getEntityType() != EntityType.ENDER_DRAGON) return;
        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"¡El "+ChatColor.RED+"dragón "+ChatColor.YELLOW+"ha sido revivido!");
        }
    }
}

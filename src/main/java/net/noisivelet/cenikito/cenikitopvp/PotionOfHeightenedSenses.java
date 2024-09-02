/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.world.item.alchemy.Potion;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.USERS;
import net.noisivelet.cenikito.cenikitopvp.utils.UserDatabase.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Francis
 */
public class PotionOfHeightenedSenses implements Listener{

    @EventHandler
    public void onPotionDrank(PlayerItemConsumeEvent event){
        ItemStack is = event.getItem();
        if(is.getType() != Material.POTION) return;
        NamespacedKey key = new NamespacedKey("cenikitopvp", "senses");
        ItemMeta meta = is.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if(container.has(key)){
            try {
                PlayerData data = USERS.get(event.getPlayer().getUniqueId());
                if(data.isHeightenedSenses()) return;
                data.setHeightenedSenses(true);
                event.getPlayer().sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"¡Tus sentidos se agudizan! Ahora puedes detectar cómo de cerca está un enemigo además de sus latidos.");
            } catch (SQLException ex) {
                Logger.getLogger(PotionOfHeightenedSenses.class.getName()).log(Level.SEVERE, null, ex);
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.DARK_GRAY+"[*] "+ChatColor.GRAY+"Ha ocurrido un error interno consumiendo la poción; por ello, no se ha aplicado el efecto y la poción no se ha consumido.");
                
            }
            
        }
    }
    
    public static ItemStack getPotion(){
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta)potion.getItemMeta();
        meta.setColor(Color.BLACK);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 60*60*20, 0), true);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey("cenikitopvp", "senses");
        container.set(key, PersistentDataType.BOOLEAN, true);
        meta.setDisplayName(""+ChatColor.DARK_PURPLE+ChatColor.BOLD+"Poción de Sentidos Agudizados");
        meta.setLore(List.of(
                ChatColor.LIGHT_PURPLE+"Tomar esta poción incrementa tu capacidad",
                ChatColor.LIGHT_PURPLE+"de detectar jugadores enemigos cercanos",
                ChatColor.LIGHT_PURPLE+"de manera permanente."
        ));
        
        potion.setItemMeta(meta);
        return potion;
    }
    
}

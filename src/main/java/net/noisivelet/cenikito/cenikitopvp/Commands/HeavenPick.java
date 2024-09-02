/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.noisivelet.cenikito.cenikitopvp.SpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import static org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack.asNMSCopy;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

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
        
        if(cs instanceof Player player){
            ItemStack heavenPick;
            if(strings.length == 0)
                heavenPick = SpigotPlugin.getHeavenPick(player.getUniqueId());
            else
                heavenPick = SpigotPlugin.getHeavenPick(UUID.randomUUID());
            
            player.getInventory().addItem(heavenPick);
            player.sendMessage("OK");
            return true;
        } else {
            cs.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"No disponible en consola.");
            return true;
        }
    }
    
}

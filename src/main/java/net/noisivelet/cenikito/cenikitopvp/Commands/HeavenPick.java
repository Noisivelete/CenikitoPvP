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
            ItemStack pick = new ItemStack(Material.NETHERITE_PICKAXE);
            pick.addUnsafeEnchantment(Enchantment.FORTUNE, 15);
            pick.addUnsafeEnchantment(Enchantment.EFFICIENCY, 10);
            Repairable im = (Repairable)pick.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            im.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(NamespacedKey.fromString("generic.attack_damage"), 1, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
            im.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(NamespacedKey.fromString("generic.attack_speed"), 1, Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));

            im.setDisplayName(""+ChatColor.DARK_PURPLE+ChatColor.BOLD+"Regalo de los cielos");
            im.setLore(
                    List.of(
                            ChatColor.GRAY+"Eficiencia X",
                            ChatColor.GRAY+"Fortuna XV",
                            ChatColor.LIGHT_PURPLE+"Una vez cada 2 horas, los dioses te brindan su ayuda,",
                            ChatColor.LIGHT_PURPLE+"para evitar que te quedes rezagado/a.",
                            ""+ChatColor.WHITE,
                            ""+ChatColor.DARK_AQUA+ChatColor.BOLD+"[Ligado]",
                            ""+ChatColor.AQUA+ChatColor.ITALIC+"Solo puede ser cogido del suelo y usado por ti.",
                            ""+ChatColor.AQUA+ChatColor.ITALIC+"Indestructible mientras esté en el suelo."
                            
                    )
            );
            im.setRarity(ItemRarity.EPIC);
            im.setRepairCost(999);
            PersistentDataContainer pdc = im.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey("cenikitopvp", "owner");
            if(strings.length != 0)
                pdc.set(key, PersistentDataType.STRING, UUID.randomUUID().toString());
            else
                pdc.set(key, PersistentDataType.STRING, player.getUniqueId().toString());
            pick.setItemMeta(im);
            pick.setDurability((short)3000);
            player.getInventory().addItem(pick);
            
            
            player.sendMessage("OK");
            return true;
        } else {
            cs.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"No disponible en consola.");
            return true;
        }
    }
    
}

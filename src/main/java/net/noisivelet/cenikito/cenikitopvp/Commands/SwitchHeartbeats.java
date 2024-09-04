/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.CONFIG;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.USERS;
import net.noisivelet.cenikito.cenikitopvp.utils.PluginConfig;
import net.noisivelet.cenikito.cenikitopvp.utils.UserDatabase.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Francis
 */
public class SwitchHeartbeats implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!(cs instanceof Player)) return false;
        try {
            if(CONFIG.get(PluginConfig.Key.IS_PVP_ENABLED).equals("0")){
                cs.sendMessage(ChatColor.DARK_GRAY+"[*] "+ChatColor.GRAY+"La funcionalidad de latidos se activará cuando se active el PvP en el servidor.");
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        Player p = (Player)cs;
        PlayerData data;
        try {
            data = USERS.get(p.getUniqueId());
        } catch (SQLException ex) {
            Logger.getLogger(SwitchHeartbeats.class.getName()).log(Level.SEVERE, null, ex);
            p.sendMessage(ChatColor.DARK_GRAY+"[*] "+ChatColor.GRAY+"No se puede acceder a la base de datos en este momento. Inténtalo de nuevo más tarde.");
            return true;
        }
        
        boolean hb = data.isHearingHeartbeats();
        
        data.setHearingHeartbeats(!hb);
        
        if(hb){
            p.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"A partir de ahora, NO oirás latidos cuando haya gente cerca.");
        } else {
            p.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"A partir de ahora, oirás latidos cuando haya gente cerca tuya.");
        }
        
        return true;
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.USERS;
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
public class HpSenseTypesChange implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!(cs instanceof Player)){
            cs.sendMessage("No disponible en consola.");
            return true;
        }
        Player p = (Player)cs;
        if(strings.length != 1){
            sendUsage(cs);
            return true;
        }
        PlayerData data;
        try {
            data = USERS.get(p.getUniqueId());
        } catch (SQLException ex) {
            cs.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"No se puede acceder a la base de datos. Espera un poco y vuelve a intentarlo.");
            return true;
        }
        
        if(!data.isHeightenedSenses()){
            p.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"No puedes modificar esta opción ya que no puedes ver la vida de los enemigos. Tómate una "+ChatColor.DARK_PURPLE+ChatColor.BOLD+"Poción de Sentidos Agudizados"+ChatColor.RESET+ChatColor.RED+" primero.");
            return true;
        }
        
        String confirmStr;
        switch(strings[0]){
            case "toda" -> {
                data.setHpSenseTypes(PlayerData.SensingHealthType.ALL);
                confirmStr = "se mostrará la vida de todo lo que ataques";
            }
            
            case "jugador" -> {
                data.setHpSenseTypes(PlayerData.SensingHealthType.PLAYERS);
                confirmStr = "solo se mostrará la vida de jugadores a los que ataques";
            }
            
            case "nada" -> {
                data.setHpSenseTypes(PlayerData.SensingHealthType.NONE);
                confirmStr = "no se mostrará la vida de nada a lo que ataques";
            }
            
            default -> {
                sendUsage(cs);
                return true;
            }
        }
        p.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"Preferencias cambiadas correctamente. A partir de ahora, "+ChatColor.YELLOW+confirmStr+ChatColor.GREEN+".");
        return true;
    }
    
    private void sendUsage(CommandSender cs){
        cs.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"Uso: "+ChatColor.AQUA+"/mostrarvida <toda|jugador|nada>");
    }
    
}

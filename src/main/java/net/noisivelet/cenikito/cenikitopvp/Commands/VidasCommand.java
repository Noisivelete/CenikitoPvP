/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.noisivelet.cenikito.cenikitopvp.SpigotPlugin;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.USERS;
import net.noisivelet.cenikito.cenikitopvp.utils.UserDatabase.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Francis
 */
public class VidasCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!cs.isOp()) return false;
        
        Player p = Bukkit.getPlayer(strings[0]);
        int nuevasVidas = Integer.valueOf(strings[1]);
        PlayerData data;
        try {
            data = USERS.get(p.getUniqueId());
        } catch (SQLException ex) {
            Logger.getLogger(VidasCommand.class.getName()).log(Level.SEVERE, null, ex);
            cs.sendMessage("SQLException");
            return true;
        }
        data.setVidas(nuevasVidas);
        SpigotPlugin.pvp.getObjective("vidas").getScore(p).setScore(nuevasVidas*2);
        cs.sendMessage("OK");
        return true;
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.Commands;

import net.noisivelet.cenikito.cenikitopvp.Sidebar;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Francis
 */
public class SidebarEditCommands implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!cs.isOp()){
            cs.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"Acceso denegado.");
            return true;
        }
        
        Sidebar.setNextEventTime(Long.parseLong(strings[0]));
        Sidebar.setNextEventPayload(strings[1]);
        String nombre = "";
        for(int i = 2; i<strings.length;i++){
            String str = strings[i];
            nombre+=str+" ";
        }
        nombre=nombre.strip();
        Sidebar.setNextEventName(nombre);
        Sidebar.updateSidebar();
        cs.sendMessage("OK");
        return true;
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import java.util.concurrent.TimeUnit;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.pvp;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

/**
 *
 * @author Francis
 */
public class Sidebar {
    static String eventName="Ninguno";
    static long eventTime=-1;
    
    static void updateSidebar(){
        Objective sidebar = pvp.getObjective("sidebar");
        sidebar.unregister();
        sidebar = pvp.registerNewObjective("sidebar", Criteria.DUMMY, ""+ChatColor.GOLD+ChatColor.BOLD+SpigotPlugin.addLeading("CenikitoPvP", 5));
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score10 = sidebar.getScore(""+ChatColor.DARK_GREEN+ChatColor.BOLD+ChatColor.UNDERLINE+"Próximo evento");
        score10.setScore(10);
        Score score9 = sidebar.getScore(ChatColor.GREEN+SpigotPlugin.addLeading(getTimeUntilNextEvent(), 5));
        score9.setScore(9);
        Score score8 = sidebar.getScore(ChatColor.AQUA+getNextEventName());
        score8.setScore(8);
    }

    public static String getTimeUntilNextEvent() {
        if(eventTime == -1){
            return "--:--:--";
        }
        return SpigotPlugin.timeToString(eventTime-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
    
    public static String getNextEventName(){
        return eventName;
    }
    
    public static void setNextEventName(String eventName){
        Sidebar.eventName = eventName;
    }
    
    public static void setNextEventTime(long when){
        Sidebar.eventTime = when;
    }
}

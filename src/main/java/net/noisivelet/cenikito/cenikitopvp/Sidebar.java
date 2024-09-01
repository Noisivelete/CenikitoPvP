/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.noisivelet.cenikito.cenikitopvp.Commands.EventControl;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.CONFIG;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.pvp;
import net.noisivelet.cenikito.cenikitopvp.utils.PluginConfig;
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
    public static String UNKNOWN_TIME = "--:--:--";
    public static String UNKNOWN_NAME = "Ninguno";
    static public void updateSidebar(){
        String time = getTimeUntilNextEvent();
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
        
        if(time.startsWith("-")){
            executePayload();
            try {
                CONFIG.store(PluginConfig.Key.NEXT_EVENT_NAME, "Ninguno");
                CONFIG.store(PluginConfig.Key.NEXT_EVENT_TIME, "-1");
            } catch (SQLException ex) {
                Logger.getLogger(Sidebar.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    public static String getTimeUntilNextEvent() {
        long eventTime;
        try {
            eventTime = Long.parseLong(CONFIG.get(PluginConfig.Key.NEXT_EVENT_TIME));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return UNKNOWN_TIME;
        }
        if(eventTime == -1){
            return UNKNOWN_TIME;
        }
        return SpigotPlugin.timeToString(eventTime-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
    
    public static String getNextEventName(){
        try {
            return CONFIG.get(PluginConfig.Key.NEXT_EVENT_NAME);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return UNKNOWN_NAME;
        }
    }
    
    public static void setNextEventName(String eventName){
        try {
            CONFIG.store(PluginConfig.Key.NEXT_EVENT_NAME, eventName);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void setNextEventTime(long when){
        try {
            CONFIG.store(PluginConfig.Key.NEXT_EVENT_TIME, when+"");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void executePayload() {
        try {
            String payload = CONFIG.get(PluginConfig.Key.NEXT_EVENT_PAYLOAD);
            if(payload.isEmpty()) return;
            
            try {
                Method payloadMethod = EventControl.class.getMethod(payload, null);
                payloadMethod.invoke(null);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(Sidebar.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Sidebar.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Sidebar.class.getName()).log(Level.SEVERE, null, ex);
            }
            CONFIG.store(PluginConfig.Key.NEXT_EVENT_PAYLOAD, "");
        } catch (SQLException | SecurityException ex) {
            Logger.getLogger(Sidebar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setNextEventPayload(String string) {
        try {
            CONFIG.store(PluginConfig.Key.NEXT_EVENT_PAYLOAD, string);
        } catch (SQLException ex) {
            Logger.getLogger(Sidebar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

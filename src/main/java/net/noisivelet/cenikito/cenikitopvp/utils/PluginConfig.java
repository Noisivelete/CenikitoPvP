/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.noisivelet.cenikito.cenikitopvp.utils.SQLDatabase;

/**
 *
 * @author Francis
 */
public class PluginConfig {
    ConcurrentHashMap<Key,String> cache;
        public PluginConfig(){
            cache=new ConcurrentHashMap<>();
        }
        public enum Key{
            NEXT_EVENT_TIME, NEXT_EVENT_NAME, NEXT_EVENT_PAYLOAD, IS_PVP_ENABLED, IS_NETHER_ENABLED, IS_END_ENABLED
        }
        
        public void store(Key key, String value) throws SQLException{
            SQLDatabase.queryDML("UPDATE config SET value=? WHERE `key`=?", value, key.name());
            cache.put(key, value);
        }
        
        public String get(Key key) throws SQLException{
            if(!cache.containsKey(key)){
                cache.put(key, SQLDatabase.querySelect("SELECT value FROM config WHERE `key`=?", key.name()).get(0).get("value"));
            }
            
            return cache.get(key);
        }
}

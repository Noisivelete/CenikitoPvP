/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Francis
 */
public class UserDatabase {
    public static class PlayerData{
        private UUID uuid;
        private boolean creadoEnderChest;
        private int vidas;
        private long mercyRuleUntil;

        public PlayerData(UUID uuid, boolean creadoEnderChest, int vidas, long mercyRuleUntil) {
            this.uuid = uuid;
            this.creadoEnderChest = creadoEnderChest;
            this.vidas = vidas;
            this.mercyRuleUntil = mercyRuleUntil;
        }
        
        public PlayerData(UUID uuid){
            this.uuid = uuid;
            creadoEnderChest = false;
            vidas = 10;
            mercyRuleUntil = 0;
            try {
                SQLDatabase.queryInsert("INSERT INTO jugadores(uuid) VALUES(?)", uuid.toString());
            } catch (SQLException ex) {
                Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        public boolean isCreadoEnderChest() {
            return creadoEnderChest;
        }

        public void setCreadoEnderChest(boolean creadoEnderChest) {
            this.creadoEnderChest = creadoEnderChest;
            try {
                SQLDatabase.queryDML("UPDATE jugadores SET creado_ender_chest=? WHERE uuid=?", creadoEnderChest?"1":"0", uuid.toString());
            } catch (SQLException ex) {
                Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public int getVidas() {
            return vidas;
        }

        public void setVidas(int vidas) {
            this.vidas = vidas;
            try {
                SQLDatabase.queryDML("UPDATE jugadores SET vidas=? WHERE uuid=?", ""+vidas, uuid.toString());
            } catch (SQLException ex) {
                Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public long getMercyRuleUntil() {
            return mercyRuleUntil;
        }

        public void setMercyRuleUntil(long mercyRuleUntil) {
            this.mercyRuleUntil = mercyRuleUntil;
            try {
                SQLDatabase.queryDML("UPDATE jugadores SET mercy_rule_until=? WHERE uuid=?", ""+mercyRuleUntil, uuid.toString());
            } catch (SQLException ex) {
                Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    ConcurrentHashMap<UUID,PlayerData> cache;
        public UserDatabase(){
            cache=new ConcurrentHashMap<>();
        }
        
        public PlayerData get(UUID key) throws SQLException{
            if(!cache.containsKey(key)){
                ArrayList<HashMap<String,String>> query = SQLDatabase.querySelect("SELECT * FROM jugadores WHERE uuid=?", key.toString());
                if(query.isEmpty())
                    cache.put(key, new PlayerData(key));
                else{
                    HashMap<String,String> row = query.get(0);
                    cache.put(key, new PlayerData(UUID.fromString(row.get("uuid")), row.get("creado_ender_chest").equals("1"), Integer.parseInt(row.get("vidas")), Long.parseLong(row.get("mercy_rule_until"))));
                }
            }
            
            return cache.get(key);
        }
}

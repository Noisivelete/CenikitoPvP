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
        public enum SensingHealthType{
            ALL, PLAYERS, NONE
        }
        private UUID uuid;
        private boolean creadoEnderChest;
        private int vidas;
        private long mercyRuleUntil;
        private boolean heightenedSenses;
        private boolean hearingHeartbeats;
        private long lastTotemUsed;
        private SensingHealthType hpSenseTypes; 

        public PlayerData(UUID uuid, boolean creadoEnderChest, int vidas, long mercyRuleUntil, boolean heightenedSenses, boolean hearingHeartbeats, long lastTotemUsed, SensingHealthType hpSenseTypes) {
            this.uuid = uuid;
            this.creadoEnderChest = creadoEnderChest;
            this.vidas = vidas;
            this.mercyRuleUntil = mercyRuleUntil;
            this.heightenedSenses = heightenedSenses;
            this.hearingHeartbeats = hearingHeartbeats;
            this.lastTotemUsed = lastTotemUsed;
            this.hpSenseTypes = hpSenseTypes;
        }
        
        public PlayerData(UUID uuid){
            this.uuid = uuid;
            creadoEnderChest = false;
            vidas = 10;
            mercyRuleUntil = 0;
            hearingHeartbeats = true;
            heightenedSenses = false;
            lastTotemUsed = 0;
            hpSenseTypes = SensingHealthType.ALL;
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

        public boolean isHeightenedSenses() {
            return heightenedSenses;
        }

        public void setHeightenedSenses(boolean heightenedSenses) {
            this.heightenedSenses = heightenedSenses;
            try {
                SQLDatabase.queryDML("UPDATE jugadores SET heightened_senses=? WHERE uuid=?", heightenedSenses?"1":"0", uuid.toString());
            } catch (SQLException ex) {
                Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public boolean isHearingHeartbeats() {
            return hearingHeartbeats;
        }

        public void setHearingHeartbeats(boolean hearingHeartbeats) {
            this.hearingHeartbeats = hearingHeartbeats;
            try {
                SQLDatabase.queryDML("UPDATE jugadores SET hearing_heartbeats=? WHERE uuid=?", hearingHeartbeats?"1":"0", uuid.toString());
            } catch (SQLException ex) {
                Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public long getLastTotemUsed() {
            return lastTotemUsed;
        }

        public void setLastTotemUsed(long lastTotemUsed) {
            this.lastTotemUsed = lastTotemUsed;
            try {
                SQLDatabase.queryDML("UPDATE jugadores SET last_totem_used=? WHERE uuid=?", lastTotemUsed+"", uuid.toString());
            } catch (SQLException ex) {
                Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public SensingHealthType getHpSenseTypes() {
            return hpSenseTypes;
        }

        public void setHpSenseTypes(SensingHealthType hpSenseTypes) {
            this.hpSenseTypes = hpSenseTypes;
            try {
                SQLDatabase.queryDML("UPDATE jugadores SET hp_sense_types=? WHERE uuid=?", hpSenseTypes.name(), uuid.toString());
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
                    cache.put(key, new PlayerData(
                            UUID.fromString(row.get("uuid")),
                            row.get("creado_ender_chest").equals("1"),
                            Integer.parseInt(row.get("vidas")),
                            Long.parseLong(row.get("mercy_rule_until")),
                            row.get("heightened_senses").equals("1"),
                            row.get("hearing_heartbeats").equals("1"),
                            Long.parseLong(row.get("last_totem_used")),
                            PlayerData.SensingHealthType.valueOf(row.get("hp_sense_types"))));
                }
            }
            
            return cache.get(key);
        }
}

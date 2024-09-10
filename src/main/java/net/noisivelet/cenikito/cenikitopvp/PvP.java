/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.CONFIG;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.USERS;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.addToPvPTeam;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.getHead;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.mercyRule;
import net.noisivelet.cenikito.cenikitopvp.utils.PluginConfig;
import net.noisivelet.cenikito.cenikitopvp.utils.UserDatabase.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Francis
 */
public class PvP implements Listener {

    public static ConcurrentHashMap<UUID, Long> inCombat = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        long now = System.currentTimeMillis();
        UUID uuid = event.getPlayer().getUniqueId();
        Long combatExpiration = inCombat.remove(uuid);
        if (combatExpiration != null && combatExpiration > now) {
            event.getPlayer().setHealth(0); //Matarlo
            event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), getHead(event.getPlayer()));
        }
    }
    
    /*public static final HashSet<PotionEffectType> combatEffects = new HashSet<>();
    static{
        combatEffects.add(PotionEffectType.BAD_OMEN);
        combatEffects.add(PotionEffectType.BAD_OMEN);
        combatEffects.add(PotionEffectType.BAD_OMEN);
        combatEffects.add(PotionEffectType.BAD_OMEN);
        combatEffects.add(PotionEffectType.BAD_OMEN);
        combatEffects.add(PotionEffectType.BAD_OMEN);
        combatEffects.add(PotionEffectType.BAD_OMEN);
        combatEffects.add(PotionEffectType.BAD_OMEN);
        
    }*/
    
    @EventHandler
    public void onPotionSplashEvent(PotionSplashEvent event){
        
        ProjectileSource shooter = event.getEntity().getShooter();
        if(shooter == null) return; //Era una trampa;
        
        if(!(shooter instanceof Player)) return;
        
        boolean hasPoison = false;
        for(PotionEffect effect : event.getPotion().getEffects()){
            if(effect.getType() == PotionEffectType.POISON)
                hasPoison = true;
        }
        if(!hasPoison) return;
        
        Player attacker = (Player)shooter;
        
        for(LivingEntity entity : event.getAffectedEntities()){
            if(entity instanceof Player p){
                if(!mercyRule.contains(((Player) entity).getUniqueId()))
                    startCombat(attacker, p);
            }
        }
        
    }
    
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event){
        Entity defender = event.getEntity();
        if(!(defender instanceof Player)) return;
        
        Entity damager = event.getDamageSource().getCausingEntity();
        if(damager == null || !(damager instanceof Player)) return;
        
        boolean pvpActive = true;
        try {
            pvpActive = CONFIG.get(PluginConfig.Key.IS_PVP_ENABLED).equals("1");
        } catch (SQLException ex) {
            Logger.getLogger(PvP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(!pvpActive){
            event.setCancelled(true);
            damager.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"PvP está desactivado.");
            return;
        }
        
        
        if(mercyRule.containsKey(defender.getUniqueId())){
            event.setCancelled(true);
            damager.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"No puedes atacar a ese jugador aún; murió no hace mucho.");
            return;
        }
        
        if(mercyRule.containsKey(damager.getUniqueId())){
            mercyRule.remove(damager.getUniqueId()).cancel();
            damager.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"Has cancelado tu regla de clemencia al atacar a un jugador.");
            PlayerData data;
            try {
                data = USERS.get(damager.getUniqueId());
            } catch (SQLException ex) {
                Logger.getLogger(PvP.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            data.setMercyRuleUntil(0);
        }

        startCombat((Player)damager, (Player)defender);
    }

    /*@EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        if (defender.getType() != EntityType.PLAYER) {
            return;
        }
        System.out.println("DAMAGE DONE: "+ event.getDamage());
        Entity damager = event.getDamageSource().getCausingEntity();
        
        if(damager.getType() != EntityType.PLAYER) return;
        
        if(mercyRule.contains(defender.getUniqueId())){
            event.setCancelled(true);
            damager.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"No puedes atacar a ese jugador aún; murió no hace mucho.");
            return;
        }

        startCombat((Player)damager, (Player)defender);

    }*/
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player p = event.getEntity();
        PlayerData pdata;
        try {
            pdata = USERS.get(p.getUniqueId());
        } catch (SQLException ex) {
            Logger.getLogger(PvP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        int vidasRestantes = pdata.getVidas();
        if(vidasRestantes == 1){
            pdata.setVidas(0);
            p.setGameMode(GameMode.SPECTATOR);
            SpigotPlugin.sendAlert(""+ChatColor.DARK_PURPLE+ChatColor.BOLD+"¡"+ChatColor.DARK_RED+ChatColor.BOLD+p.getName()+ChatColor.DARK_PURPLE+ChatColor.BOLD+" ha sido eliminado!");
        } else
            pdata.setVidas(vidasRestantes - 1);
        
        if(inCombat.containsKey(p.getUniqueId())){
            ItemStack head = SpigotPlugin.getHead(event.getEntity());
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), head);
            
            Entity killer = event.getDamageSource().getCausingEntity();
            
            if(killer instanceof Player k){
                k.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*10, 3));
            }
            
            UUID uuid = p.getUniqueId();
            long time = inCombat.remove(uuid);
            if(time != 0L){ //Evita añadir a la regla de clemencia a gente que esté en combate contra el dragón.
                addToMercyRule(p);
                p.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"Has dejado de estar en combate. Puedes desconectarte.");
            }
        }
        
        p.sendMessage(ChatColor.DARK_PURPLE+"[*] "+ChatColor.LIGHT_PURPLE+"Te quedan "+ChatColor.RED+(vidasRestantes-1)+ChatColor.LIGHT_PURPLE+" vidas.");
        if(vidasRestantes == 1)
            p.sendMessage(ChatColor.DARK_GRAY+"[*] "+ChatColor.GRAY+"Has sido eliminado. Podrás ver a los demás participantes por medio del modo espectador.");
        SpigotPlugin.pvp.getObjective("vidas").getScore(p).setScore((vidasRestantes-1) * 2);
    }
    
    public void combatTask(Player p){
        while (inCombat.containsKey(p.getUniqueId())) {
            long ending = inCombat.get(p.getUniqueId());
            long now = System.currentTimeMillis();
            if (ending > now) {
                long restante = ending - now;
                long segundos = restante / 1000;
                long millis = restante % 1000;
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""+ChatColor.RED+ChatColor.BOLD+"⚔ 00:"+(segundos<10?"0"+segundos:segundos)+"."+millis));
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PvP.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN+"⚔ --:--:---"));
                inCombat.remove(p.getUniqueId());
                SpigotPlugin.addToPvPTeam(p);
                p.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"Has dejado de estar en combate. Puedes desconectarte.");
            }
        }
    }
    
    public static void startDragonCombat(Player player){
        SpigotPlugin.addToCombatTeam(player);
        inCombat.put(player.getUniqueId(), 0L);
        player.sendMessage("" + ChatColor.DARK_RED + ChatColor.BOLD + "[*] " + ChatColor.RED + ChatColor.BOLD + "ESTÁS EN COMBATE CONTRA " + ChatColor.YELLOW + ChatColor.BOLD + "EL DRAGÓN" + ChatColor.RED + ChatColor.BOLD + ". NO TE DESCONECTES.");
        dragonCombatTasks.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(SpigotPlugin.plugin, ()->{PvP.combatTaskDragon(player);}, 10, 20));
    }
    
    public void startCombat(Player damager, Player defender){
        if (!inCombat.containsKey(damager.getUniqueId())) {
            SpigotPlugin.addToCombatTeam(damager);
            ((Player) damager).sendMessage("" + ChatColor.DARK_RED + ChatColor.BOLD + "[*] " + ChatColor.RED + ChatColor.BOLD + "Has iniciado un combate contra " + ChatColor.YELLOW + ChatColor.BOLD + defender.getName() + ChatColor.RED + ChatColor.BOLD + ". No te desconectes.");
            final Player damagerfinal=(Player)damager;
            Bukkit.getScheduler().runTaskAsynchronously(SpigotPlugin.plugin, ()->{combatTask((Player)damagerfinal);});
        }

        if (!inCombat.containsKey(defender.getUniqueId())) {
            SpigotPlugin.addToCombatTeam(defender);
            ((Player) defender).sendMessage("" + ChatColor.DARK_RED + ChatColor.BOLD + "[*] " + ChatColor.RED + ChatColor.BOLD + "ESTÁS EN COMBATE CONTRA " + ChatColor.YELLOW + ChatColor.BOLD + damager.getName() + ChatColor.RED + ChatColor.BOLD + ". NO TE DESCONECTES.");
            Bukkit.getScheduler().runTaskAsynchronously(SpigotPlugin.plugin, ()->{combatTask((Player)defender);});
        }

        long now = System.currentTimeMillis();
        long expiry = now + (1000 * 15);
        inCombat.put(damager.getUniqueId(), expiry);
        inCombat.put(defender.getUniqueId(), expiry);
    }
    
    public static void removeMercyRuleTask(Player p){
        mercyRule.remove(p.getUniqueId());
        SpigotPlugin.addToPvPTeam(p);
        p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"Tu regla de clemencia ha finalizado. Puedes volver a ser atacado/a.");
        PlayerData data;
        try {
            data = USERS.get(p.getUniqueId());
        } catch (SQLException ex) {
            Logger.getLogger(PvP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        data.setMercyRuleUntil(0);
    }
    
    public static void addToMercyRule(Player p){
        addToMercyRule(p, System.currentTimeMillis()+15*60*1000, false);
    }
    
    public static void addToMercyRule(Player p, long until, boolean wasAdmin){
        SpigotPlugin.addToSafeTeam(p);
        long remaining = until - System.currentTimeMillis();
        long remainingTicks = (remaining / 1000) * 20;
        BukkitTask task = Bukkit.getScheduler().runTaskLater(SpigotPlugin.plugin, ()->{removeMercyRuleTask(p);}, remainingTicks);
        mercyRule.put(p.getUniqueId(), task);
        try {
            PlayerData data = USERS.get(p.getUniqueId());
            data.setMercyRuleUntil(until);
        } catch (SQLException ex) {
            Logger.getLogger(PvP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(!wasAdmin)
            p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"Se ha activado la regla de clemencia: Eres inmune al daño de otros jugadores durante "+ChatColor.RED+SpigotPlugin.timeToString(remaining, TimeUnit.MILLISECONDS)+ChatColor.YELLOW+"o hasta que ataques a alguien.");
        else
            p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"Un administrador te ha activado la regla de clemencia: Serás inmune al daño de otros jugadores durante "+ChatColor.RED+SpigotPlugin.timeToString(remaining, TimeUnit.MILLISECONDS)+ChatColor.YELLOW+"o hasta que ataques a alguien.");
    }

    public static ConcurrentHashMap<UUID, BukkitTask> dragonCombatTasks = new ConcurrentHashMap<>();
    public static void combatTaskDragon(Player player) {
        DragonBattle battle = player.getWorld().getEnderDragonBattle();
        System.out.println(battle);
        if(battle != null)
            System.out.println(battle.getEnderDragon());
        if(battle != null && !battle.hasBeenPreviouslyKilled())
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED+"⚔ ∞:∞"));
        else{
            inCombat.remove(player.getUniqueId());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN+"⚔ --:--:---"));
            SpigotPlugin.addToPvPTeam(player);
            player.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"Has dejado de estar en combate. Puedes desconectarte.");
            dragonCombatTasks.remove(player.getUniqueId()).cancel();
        }
        
    }
}

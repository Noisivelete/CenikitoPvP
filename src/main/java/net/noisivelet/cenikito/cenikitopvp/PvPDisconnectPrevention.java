/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import static net.noisivelet.cenikito.cenikitopvp.SpigotPlugin.vidas;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Francis
 */
public class PvPDisconnectPrevention implements Listener {

    ConcurrentHashMap<UUID, Long> inCombat = new ConcurrentHashMap<>();
    ConcurrentHashMap<UUID, BukkitTask> mercyRule = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        long now = System.currentTimeMillis();
        UUID uuid = event.getPlayer().getUniqueId();
        Long combatExpiration = inCombat.remove(uuid);
        if (combatExpiration != null && combatExpiration > now) {
            event.getPlayer().setHealth(0); //Matarlo
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
        
        
        if(mercyRule.containsKey(defender.getUniqueId())){
            event.setCancelled(true);
            damager.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"No puedes atacar a ese jugador aún; murió no hace mucho.");
            return;
        }
        
        if(mercyRule.containsKey(damager.getUniqueId())){
            mercyRule.remove(damager.getUniqueId()).cancel();
            damager.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"Has cancelado tu regla de clemencia al atacar a un jugador.");
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
        if(!vidas.containsKey(p.getUniqueId())){
            vidas.put(p.getUniqueId(), 10);
        }
        int vidasRestantes = vidas.get(p.getUniqueId());
        if(vidasRestantes == 1){
            vidas.put(p.getUniqueId(), 0);
            p.setGameMode(GameMode.SPECTATOR);
            SpigotPlugin.sendAlert(""+ChatColor.DARK_PURPLE+ChatColor.BOLD+"¡"+ChatColor.DARK_RED+ChatColor.BOLD+p.getName()+ChatColor.DARK_PURPLE+ChatColor.BOLD+" ha sido eliminado!");
        } else
            vidas.put(p.getUniqueId(), vidas.get(p.getUniqueId())-1);
        
        if(inCombat.containsKey(p.getUniqueId())){
            Entity killer = event.getDamageSource().getCausingEntity();
            
            if(killer instanceof Player k){
                k.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*10, 3));
            }
            
            UUID uuid = p.getUniqueId();
            inCombat.remove(uuid);
            p.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"Has dejado de estar en combate. Puedes desconectarte.");
            addToMercyRule(p);
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
                    Logger.getLogger(PvPDisconnectPrevention.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN+"⚔ --:--:---"));
                inCombat.remove(p.getUniqueId());
                SpigotPlugin.pvp.getTeam("pvp").addPlayer(p);
                p.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"Has dejado de estar en combate. Puedes desconectarte.");
            }
        }
    }
    
    public void startCombat(Player damager, Player defender){
        if (!inCombat.containsKey(damager.getUniqueId())) {
            SpigotPlugin.pvp.getTeam("combat").addPlayer(damager);
            ((Player) damager).sendMessage("" + ChatColor.DARK_RED + ChatColor.BOLD + "[*] " + ChatColor.RED + ChatColor.BOLD + "Has iniciado un combate contra " + ChatColor.YELLOW + ChatColor.BOLD + defender.getName() + ChatColor.RED + ChatColor.BOLD + ". No te desconectes.");
            final Player damagerfinal=(Player)damager;
            Bukkit.getScheduler().runTaskAsynchronously(SpigotPlugin.plugin, ()->{combatTask((Player)damagerfinal);});
        }

        if (!inCombat.containsKey(defender.getUniqueId())) {
            SpigotPlugin.pvp.getTeam("combat").addPlayer(defender);
            ((Player) defender).sendMessage("" + ChatColor.DARK_RED + ChatColor.BOLD + "[*] " + ChatColor.RED + ChatColor.BOLD + "ESTÁS EN COMBATE CONTRA " + ChatColor.YELLOW + ChatColor.BOLD + damager.getName() + ChatColor.RED + ChatColor.BOLD + ". NO TE DESCONECTES.");
            Bukkit.getScheduler().runTaskAsynchronously(SpigotPlugin.plugin, ()->{combatTask((Player)defender);});
        }

        long now = System.currentTimeMillis();
        long expiry = now + (1000 * 15);
        inCombat.put(damager.getUniqueId(), expiry);
        inCombat.put(defender.getUniqueId(), expiry);
    }
    
    public void removeMercyRuleTask(Player p){
        mercyRule.remove(p.getUniqueId());
        SpigotPlugin.pvp.getTeam("pvp").addPlayer(p);
        p.setDisplayName(ChatColor.RED+"🗡 "+p.getName());
        p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"Tu regla de clemencia ha finalizado. Puedes volver a ser atacado/a.");
    }
    
    public void addToMercyRule(Player p){
        SpigotPlugin.pvp.getTeam("safe").addPlayer(p);
        p.setDisplayName(ChatColor.YELLOW+"🏳 "+p.getName());
        BukkitTask task = Bukkit.getScheduler().runTaskLater(SpigotPlugin.plugin, ()->{removeMercyRuleTask(p);}, 15*60*20);
        mercyRule.put(p.getUniqueId(), task);
        p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"Se ha activado la regla de clemencia: Eres inmune al daño de otros jugadores durante los siguientes 15 minutos, o hasta que ataques a alguno.");
        
    }
}

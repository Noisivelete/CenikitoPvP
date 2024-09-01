/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.noisivelet.cenikito.cenikitopvp.Commands.VidasCommand;
import net.noisivelet.cenikito.cenikitopvp.packets.WrapperPlayServerLogin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import static org.bukkit.event.EventPriority.LOWEST;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author Francis
 */
public class SpigotPlugin extends JavaPlugin implements Listener{
    public static Scoreboard pvp;
    public static ConcurrentHashMap<UUID, Integer> vidas = new ConcurrentHashMap<>();
    
    public static Plugin plugin;
    
    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginManager().registerEvents(new PvPDisconnectPrevention(), this);
        Bukkit.getPluginManager().registerEvents(new WitherModifier(), this);
        this.getCommand("heavenpick").setExecutor(new TestCommands());
        this.getCommand("evento").setExecutor(new SidebarEditCommands());
        this.getCommand("vidas").setExecutor(new VidasCommand());
        
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, PacketType.Play.Server.LOGIN) {
                    @Override
                    public void onPacketSending(final PacketEvent e) {
                        WrapperPlayServerLogin sl = new WrapperPlayServerLogin(e.getPacket());
                        sl.setHardcore(true);
                    }
                });
        
        ScoreboardManager man = Bukkit.getScoreboardManager();
        pvp = man.getNewScoreboard();
        
        Team pvpTeam = pvp.registerNewTeam("pvp");
        Team safeTeam = pvp.registerNewTeam("safe");
        Team inCombatTeam = pvp.registerNewTeam("combat");
        
        pvpTeam.setColor(ChatColor.RED);
        safeTeam.setColor(ChatColor.YELLOW);
        pvpTeam.setAllowFriendlyFire(true);
        safeTeam.setAllowFriendlyFire(true);
        pvpTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
        safeTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        pvpTeam.setPrefix("🗡 ");
        safeTeam.setPrefix("🏳 ");
        
        inCombatTeam.setColor(ChatColor.DARK_RED);
        inCombatTeam.setAllowFriendlyFire(true);
        inCombatTeam.setPrefix("⚔ ");
        
        
        Objective o = pvp.registerNewObjective("vidas", Criteria.DUMMY, "Vidas");
        o.setRenderType(RenderType.HEARTS);
        o.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        
        Objective sidebar = pvp.registerNewObjective("sidebar", Criteria.DUMMY, "CenikitoPvP");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        Bukkit.getScheduler().runTaskTimer(this, ()->{
            Sidebar.updateSidebar();
        }, 0, 20*60);
        
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player p = event.getPlayer();
        sendAlert("TEST ALERT: "+ChatColor.DARK_PURPLE+ChatColor.BOLD+"TEST WITH COLOR");
        /*event.getPlayer().setMaxHealth(2);
        event.getPlayer().sendMessage("[*] Setting HP to 2");*/
        p.setScoreboard(pvp);
        int vidasJugador = vidas.getOrDefault(p.getUniqueId(), 10);
        pvp.getTeam("pvp").addPlayer(event.getPlayer());
        pvp.getObjective("vidas").getScore(event.getPlayer()).setScore(vidasJugador * 2);
        p.setDisplayName(ChatColor.RED+"🗡 "+event.getPlayer().getName());
        if(vidasJugador == 0)
            p.setGameMode(GameMode.SPECTATOR);
    }
    
    @EventHandler
    public void preventEndPortalBuild(PlayerInteractEvent event){
        Block clicked = event.getClickedBlock();
        if(clicked == null) return;
        if(event.getClickedBlock().getType() != Material.END_PORTAL_FRAME) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack handStack = event.getItem();
        if(handStack == null) return;
        Material hand = handStack.getType();
        if(hand == Material.ENDER_EYE){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"Imposible acceder al End hasta que el evento del End haya sido completado - próximamente.");
        }
    }
    
    @EventHandler
    public void onPortalCreateEvent(PortalCreateEvent event){
        if(event.getReason() != CreateReason.FIRE) return;
        event.setCancelled(true);
        Entity entity = event.getEntity();
        if(entity.getType() != EntityType.PLAYER) return;
        ((Player)entity).sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"Imposible acceder al Nether hasta que el evento del Nether haya sido completado.");
    }
    
    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event){
        ItemStack is = event.getItem().getItemStack();
        ItemMeta meta = is.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String str = container.get(new NamespacedKey("cenikitopvp", "owner"), PersistentDataType.STRING);
        if(str == null) return;
        
        UUID uuidItem = UUID.fromString(str);
        Entity entity = event.getEntity();
        if(!entity.getUniqueId().equals(uuidItem))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void elytraPickupEvent(EntityPickupItemEvent event){
        if(event.getItem().getItemStack().getType() != Material.ELYTRA) return;
        ItemStack is = event.getItem().getItemStack();
        if(!isFragile(is)){
            setFragile(is);
        }
    }
    
    @EventHandler
    public void elytraHopperPickupEvent(InventoryPickupItemEvent event){
        ItemStack is= event.getItem().getItemStack();
        if(is.getType() != Material.ELYTRA) return;
        if(!isFragile(is)) setFragile(is);
    }
    
    @EventHandler
    public void preventFragileEnchant(PrepareItemEnchantEvent event){
        ItemStack is = event.getItem();
        if(!isFragile(is)) return;
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntityType() != EntityType.ITEM) return;
        if(event.getDamageSource().getDamageType() == DamageType.OUT_OF_WORLD) return;
        Item item = (Item)event.getEntity();
        ItemMeta im = item.getItemStack().getItemMeta();
        if (im == null) return;
        PersistentDataContainer container = im.getPersistentDataContainer();
        if(container.has(new NamespacedKey("cenikitopvp", "owner")))
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerEnderChestOpen(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block == null) return;
        if(block.getX() != -3 || block.getY() != 81 || block.getZ() != -2) return;
        
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"Imposible abrir este EnderChest sin haber crafteado uno antes.");
    }
    
    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event){
        if(!isPlayerAllowedToUse(event.getItemDrop().getItemStack(), event.getPlayer())) return;
        ItemMeta im = event.getItemDrop().getItemStack().getItemMeta();
        if(im == null) return;
        
        if(im.getPersistentDataContainer().has(new NamespacedKey("cenikitopvp", "owner"))){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"Ese objeto está ligado a ti. No puedes soltarlo.");
        }
    }
    
    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event){
        Player p = event.getPlayer();
        int slot = event.getNewSlot();
        ItemStack stack = event.getPlayer().getInventory().getItem(slot);
        if(stack == null) return;
        System.out.println("HeldItem new item: "+stack.getType().name());
        if(!isPlayerAllowedToUse(stack, event.getPlayer())){
            p.getInventory().remove(stack);
            p.getWorld().dropItem(p.getLocation(), stack);
            p.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"¡Ese objeto no es tuyo! Ha caído al suelo.");
        }
    }
    
    @EventHandler
    public void dropItemOnPlayerInteract(PlayerInteractEvent event){
        Player p = event.getPlayer();
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if(!isPlayerAllowedToUse(stack, p)){
            event.setCancelled(true);
            p.getInventory().remove(stack);
            p.getWorld().dropItem(p.getLocation(), stack);
            p.sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"¡Ese objeto no es tuyo! Ha caído al suelo.");
        }
    }
    
    public boolean isPlayerAllowedToUse(ItemStack stack, Player p){
        ItemMeta im = stack.getItemMeta();
        if (im == null) return true;
        PersistentDataContainer container = im.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey("cenikitopvp", "owner");
        if(container.has(key)){
            String uuid = container.get(key, PersistentDataType.STRING);
            return uuid.equals(p.getUniqueId().toString());
        }
        return true;
    }
    
    public static String timeToString(long tiempo, TimeUnit unidad){
        if(tiempo < 0) return "--:--:--";
        String str="";
        long restante=tiempo;
        switch(unidad){
            case NANOSECONDS:
            case MICROSECONDS:
            case MILLISECONDS:
                restante=restante/1000;
            case SECONDS:
                restante=restante/60;
            case MINUTES:
                long minutos=restante%60;
                if(minutos > 0)
                    str=minutos+"m "+str;
                restante=restante/60;
            case HOURS:
                long horas=restante%24;
                if(horas > 0)
                    str=horas+"h "+str;
                restante=restante/24;
            case DAYS:
                if(restante > 0)
                    str=restante+"d "+str;
        }
        return str;
    }
    
    public static String addLeading(String str, int number){
        String leadings = "";
        for(int i=0;i<=number;i++){
            leadings+=" ";
        }
        
        String ret = leadings+str+leadings;
        return ret;
    }
    
    private boolean isFragile(ItemStack is){
        is.removeEnchantments();
        ItemMeta meta = is.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey nk = new NamespacedKey("cenikitopvp", "fragil");
        return container.get(nk, PersistentDataType.BOOLEAN) != null;
    }

    private void setFragile(ItemStack is) {
        ItemMeta meta = is.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey nk = new NamespacedKey("cenikitopvp", "fragil");
        container.set(nk, PersistentDataType.BOOLEAN, true);
        meta.setLore(List.of(
                ""+ChatColor.RED+ChatColor.BOLD+"[Frágil]",
                ""+ChatColor.RED+ChatColor.ITALIC+"Demasiado delicado para ser reparado o encantado."
        ));
        ((Repairable)meta).setRepairCost(9999);
        is.setItemMeta(meta);
    }
    
    public static void sendAlert(String message){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendTitle(message, null, 10, 70, 20);
        }
    }
    
}

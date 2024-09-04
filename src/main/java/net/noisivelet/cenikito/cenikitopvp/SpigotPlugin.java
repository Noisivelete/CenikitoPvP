/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.noisivelet.cenikito.cenikitopvp;

import net.noisivelet.cenikito.cenikitopvp.Commands.SidebarEditCommands;
import net.noisivelet.cenikito.cenikitopvp.Commands.HeavenPick;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.noisivelet.cenikito.cenikitopvp.Commands.CheckClosestDistance;
import net.noisivelet.cenikito.cenikitopvp.Commands.GetPlayerHead;
import net.noisivelet.cenikito.cenikitopvp.Commands.MercyRuleSwitch;
import net.noisivelet.cenikito.cenikitopvp.Commands.SwitchHeartbeats;
import net.noisivelet.cenikito.cenikitopvp.Commands.VidasCommand;
import static net.noisivelet.cenikito.cenikitopvp.PvP.inCombat;
import net.noisivelet.cenikito.cenikitopvp.packets.WrapperPlayServerLogin;
import net.noisivelet.cenikito.cenikitopvp.utils.PluginConfig;
import net.noisivelet.cenikito.cenikitopvp.utils.SQLDatabase;
import net.noisivelet.cenikito.cenikitopvp.utils.UserDatabase;
import net.noisivelet.cenikito.cenikitopvp.utils.UserDatabase.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import static org.bukkit.event.block.Action.LEFT_CLICK_AIR;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.PHYSICAL;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author Francis
 */
public class SpigotPlugin extends JavaPlugin implements Listener{
    public static Scoreboard pvp;
    
    public static Plugin plugin;
    public static PluginConfig CONFIG = new PluginConfig();
    public static UserDatabase USERS = new UserDatabase();
    
    public static ConcurrentHashMap<UUID, BukkitTask> mercyRule = new ConcurrentHashMap<>();
    
    @Override
    public void onEnable() {
        plugin = this;
        try {
            SQLDatabase.start();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(SpigotPlugin.class.getName()).log(Level.SEVERE, null, ex);
            this.setEnabled(false);
            return;
        }
        Bukkit.getPluginManager().registerEvents(this,this);
        Bukkit.getPluginManager().registerEvents(new PvP(), this);
        Bukkit.getPluginManager().registerEvents(new WitherModifier(), this);
        Bukkit.getPluginManager().registerEvents(new PotionOfHeightenedSenses(), this);
        this.getCommand("heavenpick").setExecutor(new HeavenPick());
        this.getCommand("evento").setExecutor(new SidebarEditCommands());
        this.getCommand("vidas").setExecutor(new VidasCommand());
        this.getCommand("head").setExecutor(new GetPlayerHead());
        this.getCommand("distancia").setExecutor(new CheckClosestDistance());
        this.getCommand("latidos").setExecutor(new SwitchHeartbeats());
        this.getCommand("mercy").setExecutor(new MercyRuleSwitch());
        
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
        
        NamespacedKey nk = new NamespacedKey("cenikitopvp", "heightened_senses_potion");
        
        ShapedRecipe recipe = new ShapedRecipe(nk, PotionOfHeightenedSenses.getPotion());
        
        recipe.shape("H H"," P "," S ");
        
        recipe.setIngredient('H', Material.PLAYER_HEAD);
        recipe.setIngredient('P', Material.DRAGON_BREATH);
        recipe.setIngredient('S', Material.CALIBRATED_SCULK_SENSOR);
        Bukkit.addRecipe(recipe);
        
        Bukkit.getScheduler().runTaskTimer(this, ()->{
            Sidebar.updateSidebar();
        }, 0, 20*60);
        
        Bukkit.getScheduler().runTaskTimer(this, ()->{
            heavenlyPickaxeTask();
        }, 60*60*2*20, 60*60*2*20); //Cada 2 horas
        //}, 20*20, 60*20);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player p = event.getPlayer();
        /*event.getPlayer().setMaxHealth(2);
        event.getPlayer().sendMessage("[*] Setting HP to 2");*/
        p.setScoreboard(pvp);
        
        boolean pvpEnabled = true;
        try {
            pvpEnabled = CONFIG.get(PluginConfig.Key.IS_PVP_ENABLED).equals("1");
        } catch (SQLException ex) {
            Logger.getLogger(SpigotPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        PlayerData data;
        try {
            data = USERS.get(p.getUniqueId());
        } catch (SQLException ex) {
            Logger.getLogger(SpigotPlugin.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        int vidasJugador = data.getVidas();
        long mercyRulePlayer = data.getMercyRuleUntil();
        if(!mercyRule.containsKey(p.getUniqueId()) && mercyRulePlayer != 0){
            PvP.addToMercyRule(p, mercyRulePlayer, false);
        }
        
        if(pvpEnabled && !mercyRule.containsKey(p.getUniqueId())){
            addToPvPTeam(p);
        } else{
            addToSafeTeam(p);
        }
        pvp.getObjective("vidas").getScore(event.getPlayer()).setScore(vidasJugador * 2);
        if(vidasJugador == 0)
            p.setGameMode(GameMode.SPECTATOR);
        
        BukkitTask hbTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, ()->{
            heartbeatTask(p);
        }, 0, 10);
        hbTasks.put(p.getUniqueId(), hbTask);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        hbTasks.remove(uuid).cancel();
        hbTypes.remove(uuid);
        hbTicksTillHeartbeat.remove(uuid);
        
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
        if(hand != Material.ENDER_EYE) return;
        
        boolean endActive = true;
        try {
            endActive = CONFIG.get(PluginConfig.Key.IS_END_ENABLED).equals("1");
        } catch (SQLException ex) {
            Logger.getLogger(SpigotPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(endActive) return;
        
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.DARK_RED+"[*] "+ChatColor.RED+"Imposible acceder al End hasta que el evento del End haya sido completado - próximamente.");
    }
    
    @EventHandler
    public void onPortalCreateEvent(PortalCreateEvent event){
        if(event.getReason() != CreateReason.FIRE) return;
        boolean nether = true;
        try {
            nether = CONFIG.get(PluginConfig.Key.IS_NETHER_ENABLED).equals("1");
        } catch (SQLException ex) {
            Logger.getLogger(SpigotPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(nether) return;
        
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
    
    @EventHandler
    public void consumePlayerHeads(PlayerInteractEvent event){
        Player p = event.getPlayer();
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if(stack.getType() != Material.PLAYER_HEAD) return;
        if(p.isSneaking()) return;
        
        switch(event.getAction()){
            case LEFT_CLICK_BLOCK, LEFT_CLICK_AIR, PHYSICAL -> { 
                return;
            }
            case RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR -> event.setCancelled(true);
            default -> throw new AssertionError(event.getAction().name());
            
        }
        consumeHead(stack, p);
        
    }
    
    @EventHandler
    public void onTotemUse(EntityResurrectEvent event){
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        Player p = (Player)event.getEntity();
        
        PlayerData data;
        try {
            data = USERS.get(p.getUniqueId());
        } catch (SQLException ex) {
            Logger.getLogger(SpigotPlugin.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        long last = data.getLastTotemUsed();
        long now = System.currentTimeMillis();
        if(now - last < 60*15*1000){
            event.setCancelled(true);
            p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"Tu totem no ha podido utilizarse debido a que continúa en enfriamiento.");
            return;
        } else {
            data.setLastTotemUsed(now);
            p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"¡Has usado un totem! No podrás usar otro hasta dentro de "+ChatColor.RED+"15 minutos"+ChatColor.YELLOW+".");
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
        if(str.isEmpty()){
            str = "<1m";
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
    
    public static void addToSafeTeam(Player p){
        SpigotPlugin.pvp.getTeam("safe").addPlayer(p);
        p.setDisplayName(ChatColor.YELLOW+"🏳 "+p.getName());
    }
    
    public static void addToPvPTeam(Player p){
        SpigotPlugin.pvp.getTeam("pvp").addPlayer(p);
        p.setDisplayName(ChatColor.RED+"🗡 "+p.getName());
    }
    
    public static void addToCombatTeam(Player p){
        SpigotPlugin.pvp.getTeam("combat").addPlayer(p);
        p.setDisplayName(ChatColor.DARK_RED+"⚔ "+p.getName());
    }
    
    public static ItemStack getHeavenPick(UUID uuid){
        ItemStack pick = new ItemStack(Material.NETHERITE_PICKAXE);
            pick.addUnsafeEnchantment(Enchantment.FORTUNE, 15);
            pick.addUnsafeEnchantment(Enchantment.EFFICIENCY, 10);
            Repairable im = (Repairable)pick.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            im.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(NamespacedKey.fromString("generic.attack_damage"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
            im.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(NamespacedKey.fromString("generic.attack_speed"), 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));

            im.setDisplayName(""+ChatColor.DARK_PURPLE+ChatColor.BOLD+"Regalo de los cielos");
            im.setLore(
                    List.of(
                            ChatColor.GRAY+"Eficiencia X",
                            ChatColor.GRAY+"Fortuna XV",
                            ChatColor.LIGHT_PURPLE+"Una vez cada 2 horas, los dioses te brindan su ayuda,",
                            ChatColor.LIGHT_PURPLE+"para evitar que te quedes rezagado/a.",
                            ""+ChatColor.WHITE,
                            ""+ChatColor.DARK_AQUA+ChatColor.BOLD+"[Ligado]",
                            ""+ChatColor.AQUA+ChatColor.ITALIC+"Solo puede ser cogido del suelo y usado por ti.",
                            ""+ChatColor.AQUA+ChatColor.ITALIC+"Indestructible mientras esté en el suelo."
                            
                    )
            );
            im.setRarity(ItemRarity.EPIC);
            im.setRepairCost(999);
            PersistentDataContainer pdc = im.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey("cenikitopvp", "owner");
            pdc.set(key, PersistentDataType.STRING, uuid.toString());
            pick.setItemMeta(im);
            pick.setDurability((short)3000);
            return pick;
    }
    
    public void heavenlyPickaxeTask(){
        for(Player p : Bukkit.getOnlinePlayers()){
            ItemStack[] inventory = p.getInventory().getStorageContents();
            boolean hasSpace = false;
            for(int i=0;i<inventory.length;i++){
                if(inventory[i]==null){
                    hasSpace = true;
                    i=inventory.length;
                }
            }
            ItemStack pick = getHeavenPick(p.getUniqueId());
            p.playSound(p.getLocation(), Sound.ITEM_TRIDENT_THUNDER, SoundCategory.AMBIENT, 0.5f, 2);
            if(hasSpace){
                p.getInventory().addItem(pick);
                p.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"Los dioses te observan, entretenidos. Has recibido un regalo.");
            } else {
                p.getWorld().dropItem(p.getLocation(), pick);
                p.sendMessage(ChatColor.DARK_GREEN+"[*] "+ChatColor.GREEN+"Los dioses te observan, entretenidos. Has recibido un regalo - pero tu inventario está lleno. Ha caído al suelo.");
            }
        }
    }
    
    public static ItemStack getHead(Player p){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)head.getItemMeta();

        meta.setOwningPlayer(p);
        meta.setDisplayName(ChatColor.YELLOW+"Cabeza de "+ChatColor.AQUA+p.getName());
        meta.setLore(List.of(
                ChatColor.GRAY+"Vida Instantánea II "+ChatColor.RED+"(❤x5)",
                ChatColor.GRAY+"Velocidad III (0:15)"
        ));
        PersistentDataContainer data = meta.getPersistentDataContainer();

        NamespacedKey nk = new NamespacedKey("cenikitopvp", "playerhead");
        data.set(nk, PersistentDataType.BOOLEAN, true);
        head.setItemMeta(meta);
        return head;
    }
    
    public static void consumeHead(ItemStack stack, Player p){
        int amount = stack.getAmount();
        if(amount > 1)
            stack.setAmount(amount - 1);
        else
            p.getInventory().remove(stack);
        double newHealth = p.getHealth() + 10;
        double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if(newHealth > maxHealth){
            newHealth = maxHealth;
        }
        p.setHealth(newHealth);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 2));
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1, 1);
        p.sendMessage(ChatColor.GOLD+"[*] "+ChatColor.YELLOW+"¡Al comerte esa cabeza has restaurado "+ChatColor.GREEN+"5 "+ChatColor.YELLOW+"corazones de vida! Obtienes, además, 15 segundos de "+ChatColor.GREEN+"Velocidad III"+ChatColor.YELLOW+".");
    }
    public enum HeartbeatType{
        FAR,CLOSE,CLOSER, CLOSEST
    }
    public static ConcurrentHashMap<UUID, HeartbeatType> hbTypes = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, Integer> hbTicksTillHeartbeat = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, BukkitTask> hbTasks = new ConcurrentHashMap<>();
    public static void heartbeatTask(Player player){
        try {
            if(CONFIG.get(PluginConfig.Key.IS_PVP_ENABLED).equals("0")) return;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        if(inCombat.containsKey(player.getUniqueId())) return;
        
        Location playerLocation = player.getLocation();
        World playerWorld = player.getWorld();
        double smallestDistance = 500*500;
        for(Player itPlayer : Bukkit.getOnlinePlayers()){
            if(!player.equals(itPlayer)){
                World itPlayerWorld = itPlayer.getWorld();
                if(playerWorld.equals(itPlayerWorld)){
                    Location itPlayerLocation = itPlayer.getLocation();
                    double distance = playerLocation.distanceSquared(itPlayerLocation);
                    /*if(itPlayer.isSneaking()){
                        distance+=64*64;
                    }*/
                    if(distance < smallestDistance)
                        smallestDistance = distance;
                }
            }
            
        }
        UUID uuid = player.getUniqueId();
        boolean playSound = false;
        HeartbeatType type;
        int ticksTillBeat;
        ChatColor warningColor;
        String alerta;
        if(smallestDistance > 128*128){
            hbTypes.remove(uuid);
            hbTicksTillHeartbeat.remove(uuid);
            return;
        }
        if(smallestDistance <= 128*128 && smallestDistance >= 64*64){
            //FAR
            type = HeartbeatType.FAR;
            ticksTillBeat = 20;
            warningColor = ChatColor.GRAY;
            alerta = "Alguien lejos...";
            
        } else if(smallestDistance < 64*64 && smallestDistance >= 32*32){
            //CLOSE
            type = HeartbeatType.CLOSE;
            ticksTillBeat = 10;
            warningColor = ChatColor.YELLOW;
            alerta = "Alguien cerca...";
            
        } else if(smallestDistance < 32*32 && smallestDistance >= 16*16){
            //CLOSER
            type = HeartbeatType.CLOSER;
            ticksTillBeat = 4;
            warningColor = ChatColor.GOLD;
            alerta = "Alguien muy cerca...";
            
        } else {
            //CLOSEST
            type = HeartbeatType.CLOSEST;
            ticksTillBeat = 2;
            warningColor = ChatColor.RED;
            alerta = "Alguien aquí...";
        }
        
        if(!hbTypes.containsKey(uuid)){
            hbTypes.put(uuid, type);
            hbTicksTillHeartbeat.put(uuid, ticksTillBeat);
            playSound = true;
        }
        
        HeartbeatType actualhb = hbTypes.get(uuid);
        int remainingTicks = hbTicksTillHeartbeat.get(uuid);
        
        if(actualhb != type){
            hbTypes.put(uuid, type);
            //if(ticksTillBeat < remainingTicks)
            hbTicksTillHeartbeat.put(uuid, ticksTillBeat);
            playSound = true;
        } else {
            remainingTicks--;

            if(remainingTicks == 0){
                playSound = true;
                hbTicksTillHeartbeat.put(uuid, ticksTillBeat);
            } else
                hbTicksTillHeartbeat.put(uuid, remainingTicks);
        }
        PlayerData data;
        try {
            data = USERS.get(uuid);
        } catch (SQLException ex) {
            Logger.getLogger(SpigotPlugin.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        if(data.isHeightenedSenses() && data.isHearingHeartbeats())
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(warningColor+"⚠ "+alerta));
        
        if(playSound && data.isHearingHeartbeats()){
            player.playSound(playerLocation, Sound.ENTITY_WARDEN_HEARTBEAT, SoundCategory.AMBIENT, 1, 1);

            if(!data.isHeightenedSenses())
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""+ChatColor.YELLOW+ChatColor.ITALIC+"...❤..."));
        }
    }
    
}

package tk.airshipcraft.explosivechests;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class ExplosiveChests extends JavaPlugin implements Listener {

    String folderDir;
    File folder;
    File configFile;
    FileConfiguration config;
    List<String> worlds;
    int gunPowderPower;
    int tntPower;
    int range1;
    int range2;
    int range3;
    int range4;
    int range5;
    int range6;
    int range7;
    int range8;
    int range9;
    int range10;
    Float power1;
    Float power2;
    Float power3;
    Float power4;
    Float power5;
    Float power6;
    Float power7;
    Float power8;
    Float power9;
    Float power10;
    boolean debug;

    public ExplosiveChests() {
        this.folderDir = this.getDataFolder() + "";
        this.folder = new File(this.folderDir);
        this.configFile = new File(this.folderDir + File.separator + "config.yml");
    }

    public void onEnable() {
        if (!this.folder.exists()) {
            this.getLogger().info("First time run - Creating files");
            int i = 0;
            i = this.createFolders(this.folder);
            if (i == 1) {
                this.getLogger().info("ExplosivesChest Folder created");
            }
            this.getLogger().info("Plugin Loading");
        }
        else {
            this.getLogger().info("Plugin Loading");
        }
        if (!this.configFile.exists()) {
            this.saveDefaultConfig();
            this.getLogger().info("Creating new config file");
        }
        else {
            this.getLogger().info("Loading configuration file");
        }
        this.getLogger().info("Enabled");
        this.debug = this.getConfig().getBoolean("debug");
        this.gunPowderPower = this.getConfig().getInt("gunPowderPower");
        this.tntPower = this.getConfig().getInt("tntPower");
        this.range1 = this.getConfig().getInt("range1");
        this.range2 = this.getConfig().getInt("range2");
        this.range3 = this.getConfig().getInt("range3");
        this.range4 = this.getConfig().getInt("range4");
        this.range5 = this.getConfig().getInt("range5");
        this.range6 = this.getConfig().getInt("range6");
        this.range7 = this.getConfig().getInt("range7");
        this.range8 = this.getConfig().getInt("range8");
        this.range9 = this.getConfig().getInt("range9");
        this.range10 = this.getConfig().getInt("range10");
        this.power1 = (float)this.getConfig().getDouble("power1");
        this.power2 = (float)this.getConfig().getDouble("power2");
        this.power3 = (float)this.getConfig().getDouble("power3");
        this.power4 = (float)this.getConfig().getDouble("power4");
        this.power5 = (float)this.getConfig().getDouble("power5");
        this.power6 = (float)this.getConfig().getDouble("power6");
        this.power7 = (float)this.getConfig().getDouble("power7");
        this.power8 = (float)this.getConfig().getDouble("power8");
        this.power9 = (float)this.getConfig().getDouble("power9");
        this.power10 = (float)this.getConfig().getDouble("power10");
        this.worlds = this.getConfig().getStringList("worlds");
        for (final String world : this.worlds) {
            this.getLogger().info("Adding allowed world: " + world);
        }
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
    }

    int createFolders(final File folder) {
        if (!folder.exists()) {
            folder.mkdir();
            return 1;
        }
        return 2;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onChestBreak(final BlockBreakEvent event) {
        if (this.debug) {
            this.getLogger().info("block broken ");
        }
        final Block block = event.getBlock();
        final int power = 0;
        if (this.worlds.contains(block.getWorld().getName()) && !event.isCancelled()) {
            if (this.debug) {
                this.getLogger().info("in enabled world with block " + block.getType());
            }
            if (block.getType() == Material.CHEST || block.getType() == Material.DISPENSER) {
                if (this.debug) {
                    this.getLogger().info(" block is a chest or dispenser ");
                }
                final int foundPower = this.calculatePower(block);
                if (foundPower > 0) {
                    this.detonateContainer(block, foundPower);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onExplosion(final EntityExplodeEvent event) {
        if (this.worlds.contains(event.getLocation().getWorld().getName())) {
            for (final Block b : event.blockList()) {
                if (b.getType() == Material.CHEST || b.getType() == Material.DISPENSER || b.getType() == Material.BARREL) {
                    final int foundPower = this.calculatePower(b);
                    if (foundPower > 0) {
                        if (this.debug) {
                            this.getLogger().info("power greater than 0 exploding block ");
                        }
                        this.detonateContainer(b, foundPower);
                        break;
                    }
                }
            }
        }
    }

    // eventually will need to find a better way to do this instead of having to do a bunch of if statements

    /**
     * Calculates the power of the explosion.
     * @param block
     * @return
     */
    int calculatePower(final Block block) {
        int power = 0;
        if (this.debug) {
            this.getLogger().info("Calculating power ");
        }
        if (block.getType() == Material.CHEST) {
            if (this.debug) {
                this.getLogger().info("block is chest ");
            }
            final Chest chest = (Chest)block.getState();
            if (chest.getInventory().getContents() != null) {
                for (final ItemStack item : chest.getInventory().getContents()) {
                    if (item != null) {
                        if (item.getType() == Material.GUNPOWDER) {
                            if (this.debug) {
                                this.getLogger().info("found powder ");
                            }
                            power += item.getAmount() * this.gunPowderPower;
                        }
                        if (item.getType() == Material.TNT) {
                            if (this.debug) {
                                this.getLogger().info(" found tnt");
                            }
                            power += item.getAmount() * this.tntPower;
                        }
                    }
                }
            }
        }
        else if (block.getType() == Material.DISPENSER) {
            if (this.debug) {
                this.getLogger().info("Block is dispenser");
            }
            final Dispenser dispenser = (Dispenser)block.getState();
            if (dispenser.getInventory().getContents() != null) {
                for (final ItemStack item : dispenser.getInventory().getContents()) {
                    if (item != null) {
                        if (item.getType() == Material.GUNPOWDER) {
                            if (this.debug) {
                                this.getLogger().info("found powder ");
                            }
                            power += item.getAmount() * this.gunPowderPower;
                        }
                        if (item.getType() == Material.TNT) {
                            if (this.debug) {
                                this.getLogger().info("found tnt ");
                            }
                            power += item.getAmount() * this.tntPower;
                        }
                    }
                }
            }
        }
        else if (block.getType() == Material.BARREL) {
            if (this.debug) {
                this.getLogger().info("Block is a barrel");
            }
            final Barrel barrel = (Barrel)block.getState();
            if (barrel.getInventory().getContents() != null) {
                for (final ItemStack item : barrel.getInventory().getContents()) {
                    if (item != null) {
                        if (item.getType() == Material.GUNPOWDER) {
                            if (this.debug) {
                                this.getLogger().info("found powder ");
                            }
                            power += item.getAmount() * this.gunPowderPower;
                        }
                        if (item.getType() == Material.TNT) {
                            if (this.debug) {
                                this.getLogger().info("found tnt ");
                            }
                            power += item.getAmount() * this.tntPower;
                        }
                    }
                }
            }
        }
        return power;
    }

    void detonateContainer(final Block block, final int power) {
        this.getLogger().info("container explosion at " + block.getLocation() + " with power = " + power);
        final World world = block.getWorld();
        block.breakNaturally();
        if (power > this.range1) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power1);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.setType(Material.FIRE);
            final Location location = new Location(world, (double)block.getY(), (double)(block.getX() - 1), (double)block.getZ());
            location.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
        }
        if (power > this.range2) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power2);
            block.setType(Material.FIRE);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
            Location location = new Location(world, (double)block.getY(), (double)(block.getX() - 1), (double)block.getZ());
            location.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
            location = new Location(world, (double)block.getY(), (double)(block.getX() + 1), (double)block.getZ());
            location.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
        }
        if (power > this.range3) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power3);
            block.setType(Material.FIRE);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
        }
        if (power > this.range4) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power4);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
        }
        if (power > this.range5) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power5);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
        }
        if (power > this.range6) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power6);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
        }
        if (power > this.range7) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power7);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
        }
        if (power > this.range8) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power8);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
            block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 1);
        }
        if (power > this.range9) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power9);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
        }
        if (power > this.range10) {
            block.getWorld().createExplosion(block.getLocation(), (float)this.power10);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
            block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
            block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, block.getLocation(), 2);
            block.getWorld().spawnParticle(Particle.FLAME, block.getLocation(), 2);
        }
    }
}

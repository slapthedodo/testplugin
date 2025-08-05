package com.example.cybertptest;

import org.bukkit.Bukkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {

    private final CyberTPTest plugin;
    private final Player player;
    private int currentChunkX;
    private int currentChunkZ;

    private final int startChunkX = -718;
    private final int startChunkZ = -819;
    private final int endChunkX = 1157;
    private final int endChunkZ = 1056;

    private long lastMessageTime = 0;
    private final long totalChunks = 1876L * 1876L;
    private final BossBar bossBar;

    public TeleportTask(CyberTPTest plugin, Player player, int currentChunkX, int currentChunkZ) {
        this.plugin = plugin;
        this.player = player;
        this.currentChunkX = currentChunkX;
        this.currentChunkZ = currentChunkZ;
        this.bossBar = Bukkit.createBossBar("CyberTPTest", BarColor.BLUE, BarStyle.SOLID);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void run() {
        plugin.getLogger().info("Teleport task running...");

        if (!player.isOnline()) {
            plugin.getLogger().info("Player is not online. Cancelling task.");
            player.sendMessage("Teleportation task cancelled because you are not online.");
            this.cancel();
            return;
        }

        if (!player.getName().equalsIgnoreCase("slapthedodo")) {
            plugin.getLogger().info("Player is not slapthedodo. Cancelling task.");
            player.sendMessage("Teleportation task cancelled because you are not slapthedodo.");
            this.cancel();
            return;
        }

        plugin.getLogger().info("Player is online and is slapthedodo. Proceeding with teleport.");

        World world = Bukkit.getWorlds().get(0); // Assuming the first world
        world.getChunkAtAsync(currentChunkX, currentChunkZ).thenAccept(chunk -> {
            int x = currentChunkX * 16 + 8;
            int z = currentChunkZ * 16 + 8;
            int y = 235;

            Location location = new Location(world, x, y, z);

            boolean allowFlight = player.getAllowFlight();
            player.setAllowFlight(true);
            boolean flying = player.isFlying();
            player.setFlying(true);
            player.teleportAsync(location).thenAccept(success -> {
                player.setFlying(flying);
                player.setAllowFlight(allowFlight);
                if (success) {
                    plugin.getLogger().info("Teleported " + player.getName() + " to " + x + ", " + y + ", " + z);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    plugin.getLogger().warning("Teleportation failed for " + player.getName() + " to " + x + ", " + y + ", " + z);
                }
            });
        });


        // Update chunk coordinates
        currentChunkZ++;
        if (currentChunkZ > endChunkZ) {
            currentChunkZ = startChunkZ;
            currentChunkX++;
        }

        // Check for completion
        if (currentChunkX > endChunkX) {
            player.sendMessage("Teleportation process complete!");
            this.cancel();
            return;
        }

        // Save the current chunk to the config
        plugin.getConfig().set("lastChunk.x", currentChunkX);
        plugin.getConfig().set("lastChunk.z", currentChunkZ);
        plugin.saveConfig();

        // Update Boss Bar
        long chunksDone = (long)(currentChunkX - startChunkX) * (endChunkZ - startChunkZ + 1) + (currentChunkZ - startChunkZ);
        double progress = (double) chunksDone / totalChunks;
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));

        long chunksRemaining = totalChunks - chunksDone;
        double secondsRemaining = chunksRemaining * 0.4;
        bossBar.setTitle("Estimated time remaining: " + formatDuration(secondsRemaining));
    }

    @Override
    public void cancel() {
        super.cancel();
        bossBar.removePlayer(player);
        plugin.setTeleportTask(null);
    }

    private String formatDuration(double seconds) {
        if (seconds < 60) {
            return String.format("%.1f seconds", seconds);
        } else if (seconds < 3600) {
            return String.format("%.1f minutes", seconds / 60);
        } else if (seconds < 86400) {
            return String.format("%.1f hours", seconds / 3600);
        } else {
            return String.format("%.1f days", seconds / 86400);
        }
    }
}

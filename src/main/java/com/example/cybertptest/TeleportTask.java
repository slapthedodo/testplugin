package com.example.cybertptest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {

    private final CyberTPTest plugin;
    private final Player player;
    private int currentChunkX;
    private int currentChunkZ;

    private final int endChunkX = 1145;
    private final int endChunkZ = 1044;

    private long lastMessageTime = 0;
    private final long totalChunks = 1876L * 1876L;

    public TeleportTask(CyberTPTest plugin, Player player, int startChunkX, int startChunkZ) {
        this.plugin = plugin;
        this.player = player;
        this.currentChunkX = startChunkX;
        this.currentChunkZ = startChunkZ;
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
        int x = currentChunkX * 16 + 8;
        int z = currentChunkZ * 16 + 8;
        int y = 280;

        Location location = new Location(world, x, y, z);
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                plugin.getLogger().info("Teleported " + player.getName() + " to " + x + ", " + y + ", " + z);
            } else {
                plugin.getLogger().warning("Teleportation failed for " + player.getName() + " to " + x + ", " + y + ", " + z);
            }
        });

        // Update chunk coordinates
        currentChunkZ++;
        if (currentChunkZ > endChunkZ) {
            currentChunkZ = -831;
            currentChunkX++;
        }

        // Check for completion
        if (currentChunkX > endChunkX) {
            player.sendMessage("Teleportation process complete!");
            this.cancel();
            return;
        }

        // Send progress message every 20 seconds
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMessageTime > 20000) {
            lastMessageTime = currentTime;
            long chunksDone = (long)(currentChunkX + 730) * 1876 + (currentChunkZ + 831);
            long chunksRemaining = totalChunks - chunksDone;
            double secondsRemaining = chunksRemaining * 0.4;
            player.sendMessage("Estimated time remaining: " + formatDuration(secondsRemaining));
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        plugin.getConfig().set("lastChunk.x", currentChunkX);
        plugin.getConfig().set("lastChunk.z", currentChunkZ);
        plugin.saveConfig();
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

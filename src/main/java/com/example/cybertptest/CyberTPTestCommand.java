package com.example.cybertptest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CyberTPTestCommand implements CommandExecutor {

    private final CyberTPTest plugin;

    public CyberTPTestCommand(CyberTPTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.getName().equalsIgnoreCase("slapthedodo")) {
             sender.sendMessage("This command can only be used by slapthedodo.");
             return true;
        }

        if (plugin.getTeleportTask() != null && !plugin.getTeleportTask().isCancelled()) {
            sender.sendMessage("The teleport process is already running.");
            return true;
        }

        FileConfiguration config = plugin.getConfig();
        int startChunkX = config.getInt("lastChunk.x", -718);
        int startChunkZ = config.getInt("lastChunk.z", -819);

        sender.sendMessage("Starting teleportation process...");

        TeleportTask task = new TeleportTask(plugin, player, startChunkX, startChunkZ);
        plugin.setTeleportTask(task);
        task.runTaskTimer(plugin, 0L, 20L); // 1000ms delay (20 ticks)

        return true;
    }
}

package com.example.cybertptest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CyberTPTestStopCommand implements CommandExecutor {

    private final CyberTPTest plugin;

    public CyberTPTestStopCommand(CyberTPTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.getTeleportTask() == null || plugin.getTeleportTask().isCancelled()) {
            sender.sendMessage("The teleport process is not running.");
            return true;
        }

        plugin.getTeleportTask().cancel();
        plugin.setTeleportTask(null);

        sender.sendMessage("Teleport process stopped.");

        return true;
    }
}

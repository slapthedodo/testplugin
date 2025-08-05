package com.example.cybertptest;

import org.bukkit.plugin.java.JavaPlugin;

public class CyberTPTest extends JavaPlugin {

    private TeleportTask teleportTask;

    @Override
    public void onEnable() {
        // Save the default config if it doesn't exist
        saveDefaultConfig();

        // Register commands
        getCommand("cybertptest").setExecutor(new CyberTPTestCommand(this));
        getCommand("cybertpteststop").setExecutor(new CyberTPTestStopCommand(this));

        getLogger().info("CyberTPTest plugin enabled.");
    }

    @Override
    public void onDisable() {
        // Stop the task if it's running
        if (teleportTask != null && !teleportTask.isCancelled()) {
            teleportTask.cancel();
        }
        getLogger().info("CyberTPTest plugin disabled.");
    }

    public TeleportTask getTeleportTask() {
        return teleportTask;
    }

    public void setTeleportTask(TeleportTask teleportTask) {
        this.teleportTask = teleportTask;
    }
}

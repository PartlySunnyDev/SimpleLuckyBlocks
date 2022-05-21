package me.partlysunny.api;

import org.bukkit.plugin.java.JavaPlugin;

public interface IModule {

    void enable(JavaPlugin plugin);

    void disable(JavaPlugin plugin);

}

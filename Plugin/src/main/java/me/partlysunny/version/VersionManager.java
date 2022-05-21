package me.partlysunny.version;

import me.partlysunny.ConsoleLogger;
import me.partlysunny.api.IModule;
import me.partlysunny.util.classes.ServerVersion;
import me.partlysunny.util.reflection.JavaAccessor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class VersionManager {

    public static final String PACKAGE = "me.partlysunny";
    private final JavaPlugin p;
    private ServerVersion serverVersion;
    private IModule module;

    public VersionManager(JavaPlugin p) {
        this.p = p;
    }

    public void load() throws ReflectiveOperationException {
        if (serverVersion == null) {
            throw new ClassNotFoundException("Server version not found!");
        }
        module = loadModule("Module");
    }

    @SuppressWarnings("unchecked")
    private <T> T loadModule(String name) throws ReflectiveOperationException {
        return (T) JavaAccessor
                .instance(Class.forName(PACKAGE + "." + serverVersion + "." + name));
    }

    public ServerVersion serverVersion() {
        return serverVersion;
    }

    public IModule module() {
        return module;
    }

    public void enable() {
        module.enable(p);
    }

    public void disable() {
        module.disable(p);
    }

    public void checkServerVersion() {
        String versionString = Bukkit.getServer().getClass().getPackage().getName();
        String mcVersion;
        try {
            mcVersion = versionString.split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }

        try {
            serverVersion = ServerVersion.valueOf(mcVersion);
        } catch (IllegalArgumentException exc) {
            ConsoleLogger.console("This NMS version isn't supported. (" + mcVersion + ")!");
        }
    }

    public int getWorldMinHeight(World world) {
        try {
            return world.getMinHeight();
        } catch (NoSuchMethodError ex) {
            return 0;
        }
    }

    public int getWorldMaxHeight(World world) {
        try {
            return world.getMaxHeight();
        } catch (NoSuchMethodError ex) {
            return 255;
        }
    }
}

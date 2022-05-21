package me.partlysunny;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleLogger {

    private static final Logger log = JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getLogger();

    public static void console(String msg) {
        log.info(msg);
    }

    public static void console(String... msg) {
        for (String s : msg) {
            log.info(s);
        }
    }

    public static void error(String msg) {
        log.log(Level.SEVERE, msg);
    }

    public static void error(String... msg) {
        for (String s : msg) {
            log.log(Level.SEVERE, s);
        }
    }

    public static void warn(String msg) {
        log.warning(msg);
    }

    public static void warn(String... msg) {
        for (String s : msg) {
            log.warning(s);
        }
    }

}

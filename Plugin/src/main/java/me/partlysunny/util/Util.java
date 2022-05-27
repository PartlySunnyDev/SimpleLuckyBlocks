package me.partlysunny.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTChunk;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.reflection.JavaAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public final class Util {

    public static final Random RAND = new Random();

    /**
     * With this method you can get a player's head by nickname or a base64 head by base64 code
     *
     * @param type  Determines whether you want to get the head by name or by base64
     * @param value If you want a player's head, then the player's name. If you want base64, then base64 code.
     * @return Head itemStack
     */
    public static ItemStack convert(HeadType type, String value) {
        if (type.equals(HeadType.PLAYER_HEAD)) {
            return getSkullByTexture(getPlayerHeadTexture(value));
        } else {
            return getSkullByTexture(value);
        }
    }

    private static ItemStack getSkullByTexture(String url) {
        ItemStack head = getAllVersionStack("SKULL_ITEM", "PLAYER_HEAD");
        if (url.isEmpty() || url.equals("none")) return head;

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", url));
        try {
            JavaAccessor.setValue(meta, JavaAccessor.getField(meta.getClass(), "profile"), profile);
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
        return head;
    }

    private static String getPlayerHeadTexture(String username) {
        if (getPlayerId(username).equals("none")) return "none";
        String url = "https://api.minetools.eu/profile/" + getPlayerId(username);
        try {
            JSONParser jsonParser = new JSONParser();
            String userData = readUrl(url);
            Object parsedData = jsonParser.parse(userData);

            JSONObject jsonData = (JSONObject) parsedData;
            JSONObject decoded = (JSONObject) jsonData.get("raw");
            JSONArray textures = (JSONArray) decoded.get("properties");
            JSONObject data = (JSONObject) textures.get(0);

            return data.get("value").toString();
        } catch (Exception ex) {
            return "none";
        }
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) buffer.append(chars, 0, read);
            return buffer.toString();
        } finally {
            if (reader != null) reader.close();
        }
    }

    private static String getPlayerId(String playerName) {
        try {
            String url = "https://api.minetools.eu/uuid/" + playerName;
            JSONParser jsonParser = new JSONParser();
            String userData = readUrl(url);
            Object parsedData = jsonParser.parse(userData);

            JSONObject jsonData = (JSONObject) parsedData;

            if (jsonData.get("id") != null) return jsonData.get("id").toString();
            return "";
        } catch (Exception ex) {
            return "none";
        }
    }

    private static ItemStack getAllVersionStack(String oldName, String newName) {
        Material material = null;
        try {
            material = Material.valueOf(oldName);
        } catch (Exception exception) {
            material = Material.valueOf(newName);
        }
        return new ItemStack(material, 1);
    }

    public static int getRandomBetween(int a, int b) {
        if (a > b) {
            throw new IllegalArgumentException("a must be higher than b");
        }
        if (a == b) {
            return a;
        }
        if (a < 0 && b < 0) {
            return -getRandomBetween(-b, -a);
        }
        if (a < 0) {
            return getRandomBetween(0, -a + b) - a;
        }
        return RAND.nextInt(b - a) + a;
    }

    public static void setToLuckyBlock(Block b) {
        NBTBlock nbtb = new NBTBlock(b);
        nbtb.getData().setBoolean("lucky", true);
    }

    public static void setToLuckyBlockType(Block b, String t) {
        NBTBlock nbtb = new NBTBlock(b);
        nbtb.getData().setString("luckyType", t);
    }

    public static String processText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace('&', ChatColor.COLOR_CHAR);
    }

    public static void scheduleRepeatingCancelTask(Runnable r, long delay, long repeat, long stopAfter) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        JavaPlugin p = JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class);
        BukkitTask t = scheduler.runTaskTimer(p, r, delay, repeat);
        scheduler.runTaskLater(p, t::cancel, stopAfter);
    }

    public static List<String> processTexts(List<String> texts) {
        List<String> result = new ArrayList<>();
        texts.forEach(n -> {
            result.add(processText(n));
        });
        return result;
    }

    public static String getLuckyBlockType(Block b) {
        NBTBlock nbtb = new NBTBlock(b);
        return nbtb.getData().getString("luckyType");
    }

    public static boolean isLuckyBlock(Block b) {
        NBTChunk nbtc = new NBTChunk(b.getChunk());
        NBTCompound c = nbtc.getPersistentDataContainer().getOrCreateCompound("blocks");
        String key = b.getLocation().getX() + "_" + b.getLocation().getY() + "_" + b.getLocation().getZ();
        if (c.hasKey(key)) {
            return c.getCompound(key).hasKey("luckyType");
        }
        return false;
    }

    public static <T> T getOrDefault(ConfigurationSection y, String key, T def) {
        if (y.contains(key)) {
            return (T) y.get(key);
        }
        return def;
    }

    public static <T> T getOrError(ConfigurationSection y, String key) {
        if (y.contains(key)) {
            return (T) y.get(key);
        }
        throw new IllegalArgumentException("Key " + key + " inside " + y.getName() + " was not found!");
    }

    public static ItemStack produceLuckyBlock(LuckyBlockType type) {
        ItemStack itemStack = type.innerItem();
        ItemStack block;
        if (itemStack == null) {
            block = ItemBuilder.builder(type.blockType()).setName(processText(type.displayName())).build();
        } else {
            block = itemStack.clone();
            ItemMeta itemMeta = block.getItemMeta();
            itemMeta.setDisplayName(processText(type.displayName()));
            block.setItemMeta(itemMeta);
        }
        NBTItem nbti = new NBTItem(block);
        nbti.setString("luckyType", type.id());
        nbti.applyNBT(block);
        return block;
    }

    public static boolean isLuckyBlock(int x, int y, int z, Chunk r) {
        NBTChunk nbtc = new NBTChunk(r);
        NBTCompound c = nbtc.getPersistentDataContainer().getOrCreateCompound("blocks");
        String key = x + "_" + y + "_" + z;
        if (c.hasKey(key)) {
            return c.getCompound(key).hasKey("luckyType");
        }
        return false;
    }

    public static void copy(String source, File destination, boolean rezip) throws IOException {
        InputStream stream = SimpleLuckyBlocksCore.class.getClassLoader().getResourceAsStream(source);
        if (!destination.exists()) {
            Files.copy(stream, destination.toPath());
        }
    }

    /**
     * Generation head type enum
     */
    public enum HeadType {
        PLAYER_HEAD,
        BASE64
    }
}

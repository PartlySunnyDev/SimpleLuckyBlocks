package me.partlysunny.util;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTChunk;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.partlysunny.ConsoleLogger;
import me.partlysunny.SimpleLuckyBlocksCore;
import me.partlysunny.blocks.LuckyBlockType;
import me.partlysunny.blocks.loot.entry.IEntry;
import me.partlysunny.blocks.loot.entry.LootEntryManager;
import me.partlysunny.gui.GuiManager;
import me.partlysunny.gui.SelectGui;
import me.partlysunny.gui.SelectGuiManager;
import me.partlysunny.gui.guis.Renamable;
import me.partlysunny.gui.guis.loot.entry.creation.CreateGuiManager;
import me.partlysunny.gui.guis.loot.entry.creation.EntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.EntrySaveWrapper;
import me.partlysunny.gui.guis.loot.entry.creation.mob.MobEntryCreateGui;
import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.EquipmentWrapper;
import me.partlysunny.gui.guis.loot.entry.creation.mob.equipment.MobSlot;
import me.partlysunny.gui.textInput.ChatListener;
import me.partlysunny.util.classes.ItemBuilder;
import me.partlysunny.util.classes.Pair;
import me.partlysunny.util.reflection.JavaAccessor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

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
            StringBuilder buffer = new StringBuilder();
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
        texts.forEach(n -> result.add(processText(n)));
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

    public static boolean isInvalidFilePath(String path) {
        File f = new File(path);
        try {
            f.getCanonicalPath();
            return false;
        } catch (IOException e) {
            return true;
        }
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
        nbti.setString("luckyType", LuckyBlockType.getIdOfType(type));
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

    public static void setClickSoundTo(Sound s, Gui gui) {
        gui.setOnGlobalClick(event -> {
            if (event.getWhoClicked() instanceof Player a) {
                a.playSound(a.getLocation(), s, 1, 1);
            }
            event.setCancelled(true);
        });
    }

    public static List<String> splitLoreForLine(String input, String linePrefix, String lineSuffix, int width) {
        char[] array = input.toCharArray();
        List<String> out = new ArrayList<>();
        String currentColor = "";
        String cachedColor = "";
        boolean wasColorChar = false;
        StringBuilder currentLine = new StringBuilder(linePrefix);
        StringBuilder currentWord = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if (wasColorChar) {
                wasColorChar = false;
                cachedColor = currentColor;
                Pattern pattern = Pattern.compile("[0-9a-fkmolnr]");
                if (pattern.matcher(c + "").matches()) {
                    if (c == 'r') {
                        currentColor = ChatColor.COLOR_CHAR + "r";
                    } else {
                        currentColor += ChatColor.COLOR_CHAR + "" + c;
                    }
                }
                currentWord.append(ChatColor.COLOR_CHAR + "").append(c);
                continue;
            }
            if (c == '\n') {
                currentLine.append(currentWord);
                currentWord = new StringBuilder();
                out.add(currentLine + lineSuffix);
                currentLine = new StringBuilder(linePrefix + cachedColor + currentWord);
                cachedColor = currentColor;
                continue;
            }
            if (c == ' ') {
                if ((currentLine + currentWord.toString()).replaceAll("ยง[0-9a-fklmnor]", "").length() > width) {
                    out.add(currentLine + lineSuffix);
                    currentLine = new StringBuilder(linePrefix + cachedColor + currentWord + " ");
                } else {
                    currentLine.append(currentWord).append(" ");
                }
                cachedColor = currentColor;
                currentWord = new StringBuilder();
                continue;
            }
            if (c == ChatColor.COLOR_CHAR) {
                wasColorChar = true;
                continue;
            }
            currentWord.append(c);
        }
        currentLine.append(currentWord);
        out.add(currentLine + lineSuffix);
        return out;
    }

    public static String[] getAlphabetSorted(String[] values) {
        List<String> strings = new ArrayList<>(List.of(values));
        Collections.sort(strings);
        return strings.toArray(new String[0]);
    }

    public static List<String> splitLoreForLine(String input) {
        return splitLoreForLine(input, ChatColor.GRAY.toString(), "", 30);
    }

    public static double[] linspace(double min, double max, int points) {
        double[] d = new double[points];
        for (int i = 0; i < points; i++) {
            d[i] = min + i * (max - min) / (points - 1);
        }
        return d;
    }

    public static double[] fakeSpace(int points) {
        return switch (points) {
            case 0 -> new double[]{};
            case 1 -> new double[]{4};
            case 2 -> new double[]{3, 5};
            case 3 -> new double[]{2, 4, 6};
            case 4 -> new double[]{1, 3, 5, 7};
            case 5 -> new double[]{2, 3, 4, 5, 6};
            case 6 -> new double[]{1, 2, 3, 5, 6, 7};
            case 7 -> new double[]{1, 2, 3, 4, 5, 6, 7};
            case 8 -> new double[]{0, 1, 2, 3, 5, 6, 7, 8};
            default -> new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        };
    }

    @SafeVarargs
    public static ChestGui getGeneralSelectionMenu(String title, Player p, Pair<String, ItemStack>... items) {
        if (items.length > 9) {
            ConsoleLogger.error("Too many items! (Max supported 9)");
        }
        double[] linspace = fakeSpace(items.length);
        ChestGui ui = new ChestGui(3, title);
        StaticPane pane = new StaticPane(0, 0, 9, 3);
        pane.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).build());
        setClickSoundTo(Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, ui);
        int count = 0;
        for (double d : linspace) {
            int finalCount = count;
            pane.addItem(new GuiItem(
                    items[count].b(),
                    (item) -> {
                        GuiManager.openInventory(p, items[finalCount].a());
                    }
            ), (int) Math.round(d), 1);
            count++;
        }
        ui.addPane(pane);
        return ui;
    }

    public static void addListPages(PaginatedPane pane, Player p, SelectGui<?> from, int x, int y, int width, int height, String[] a, ChestGui gui) {
        pane.setOnClick(event -> {
            if (event.getWhoClicked() instanceof Player pp) {
                pp.playSound(pp.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, 1, 1);
            }
            event.setCancelled(true);
        });
        int displaySize = width * height;
        if (displaySize < 1) {
            return;
        }
        int numPages = (int) Math.ceil(a.length / (displaySize * 1f));
        if (numPages == 0) {
            numPages = 1;
        }
        int count = 0;
        for (int i = 0; i < numPages; i++) {
            StaticPane border = new StaticPane(0, 0, 9, 5, Pane.Priority.HIGH);
            StaticPane items = new StaticPane(x, y, width, height, Pane.Priority.HIGHEST);
            addPageNav(pane, numPages, i, border, gui);
            items.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
            for (int j = count; j < count + displaySize; j++) {
                if (j > a.length - 1) {
                    break;
                }
                String itemName = a[j];
                items.addItem(new GuiItem(ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + itemName).build(), item -> {
                    from.update(p.getUniqueId(), itemName);
                    from.returnTo(p);
                }), (j - count) % width, (j - count) / width);
            }
            count += displaySize;
            Util.addReturnButton(border, p, from.getReturnTo(p), 0, 4);
            pane.addPane(i, border);
            pane.addPane(i, items);
        }
    }

    public static void addPageNav(PaginatedPane pane, int numPages, int i, StaticPane border, ChestGui gui) {
        border.fillWith(ItemBuilder.builder(Material.BLACK_STAINED_GLASS_PANE).setName("").build());
        if (i != 0) {
            border.addItem(new GuiItem(ItemBuilder.builder(Material.ARROW).setName(ChatColor.GRAY + "Page Back").setLore(ChatColor.GREEN + "Right click for 5 pages", ChatColor.RED + "Shift Click for 15 pages").build(), item -> {
                if (item.isShiftClick()) Util.changePage(pane, -15);
                else if (item.isLeftClick()) Util.changePage(pane, -1);
                else if (item.isRightClick()) Util.changePage(pane, -5);
                gui.update();
            }), 0, 2);
        }
        if (i != numPages - 1) {
            border.addItem(new GuiItem(ItemBuilder.builder(Material.ARROW).setName(ChatColor.GRAY + "Page Forward").setLore(ChatColor.GREEN + "Right click for 5 pages", ChatColor.RED + "Shift Click for 15 pages").build(), item -> {
                if (item.isShiftClick()) Util.changePage(pane, 15);
                else if (item.isLeftClick()) Util.changePage(pane, 1);
                else if (item.isRightClick()) Util.changePage(pane, 5);
                gui.update();
            }), 8, 2);
        }
    }

    public static void invalid(String message, Player p) {
        p.sendMessage(ChatColor.RED + message);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    public static <T, U> void flushNulls(Pair<T, U> pair, T repT, U repU) {
        if (pair.a() == null) {
            pair.setA(repT);
        }
        if (pair.b() == null) {
            pair.setB(repU);
        }
    }

    public static void addLoreLine(ItemStack s, String... lines) {
        ItemMeta m = s.getItemMeta();
        List<String> lore = m.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.addAll(List.of(lines));
        m.setLore(lore);
        s.setItemMeta(m);
    }

    public static void addSelectionLink(StaticPane pane, Player p, String currentGui, String selectionLink, ItemStack toShow, int x, int y) {
        pane.addItem(new GuiItem(toShow, item -> {
            SelectGuiManager.getSelectGui(selectionLink.substring(0, selectionLink.length() - 6)).setReturnTo(p.getUniqueId(), currentGui);
            p.closeInventory();
            GuiManager.openInventory(p, selectionLink);
        }), x, y);
    }

    public static void addTextInputLink(StaticPane pane, Player p, String currentGui, String message, ItemStack toShow, int x, int y, Consumer<Player> toDo) {
        pane.addItem(new GuiItem(toShow, item -> {
            ChatListener.startChatListen(p, currentGui, message, toDo);
            p.closeInventory();
        }), x, y);
    }

    public static void addEquipmentSlot(StaticPane pane, Player p, String currentGui, MobSlot slot, ItemStack toShow, ItemStack currentItem, double dropChance, int x, int y) {
        if (currentItem.getType() != Material.AIR && toShow.getType() != Material.AIR) {
            pane.addItem(new GuiItem(toShow, item -> {
                MobEntryCreateGui.setSlot(p.getUniqueId(), slot);
                SelectGuiManager.getSelectGui("mobEquipment").setReturnTo(p.getUniqueId(), currentGui);
                p.closeInventory();
                ((SelectGui<EquipmentWrapper>) SelectGuiManager.getSelectGui("mobEquipment")).openWithValue(p, new EquipmentWrapper(slot, currentItem, dropChance), "mobEquipmentSelect");
            }), x, y);
        }
    }

    public static void addReturnButton(StaticPane pane, Player p, String returnTo, int x, int y) {
        pane.addItem(new GuiItem(ItemBuilder.builder(Material.ARROW).setName(ChatColor.GREEN + "Back").build(), item -> {
            GuiManager.openInventory(p, returnTo);
        }), x, y);
    }

    public static void setName(ItemStack i, String name) {
        ItemMeta m = i.getItemMeta();
        if (m == null) {
            return;
        }
        m.setDisplayName(name);
        i.setItemMeta(m);
    }

    public static Integer getTextInputAsInt(Player pl) {
        String input = ChatListener.getCurrentInput(pl);
        if (input.equals("cancel")) {
            return null;
        }
        int currentInput;
        try {
            currentInput = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            pl.sendMessage(ChatColor.RED + "Invalid number!");
            return null;
        }
        if (currentInput < 1) {
            pl.sendMessage("Must be greater than 1!");
            return null;
        }
        return currentInput;
    }

    public static Double getTextInputAsDouble(Player pl) {
        String input = ChatListener.getCurrentInput(pl);
        if (input.equals("cancel")) {
            return null;
        }
        double currentInput;
        try {
            currentInput = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            pl.sendMessage(ChatColor.RED + "Invalid number!");
            return null;
        }
        if (currentInput < 0) {
            pl.sendMessage("Must be greater than 0!");
            return null;
        }
        return currentInput;
    }

    public static void setLore(ItemStack i, List<String> lore) {
        ItemMeta m = i.getItemMeta();
        if (m == null) {
            return;
        }
        m.setLore(lore);
        i.setItemMeta(m);
    }

    public static PotionType asType(PotionEffectType t) {
        if (t == null) {
            return PotionType.WATER;
        }
        PotionType asType = PotionType.WATER;
        for (PotionType type : PotionType.values()) {
            if (t.equals(type.getEffectType())) asType = type;
        }
        return asType;
    }

    public static void deleteFile(File f) {
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
    }

    public static ChestGui getEntryManagement(Player p, String guiId, String title, String[] values, String createGui, String backButtonLink) {
        JavaPlugin plugin = JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class);
        ChestGui gui = new ChestGui(5, title);
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);
        int numPages = (int) Math.ceil(values.length / 21f);
        if (numPages == 0) {
            numPages = 1;
        }
        int count = 0;
        for (int i = 0; i < numPages; i++) {
            StaticPane border = new StaticPane(0, 0, 9, 5);
            StaticPane items = new StaticPane(1, 1, 7, 3);
            addPageNav(pane, numPages, i, border, gui);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN + "Add new").build(), item -> GuiManager.openInventory(p, createGui)), 1, 0);
            border.addItem(new GuiItem(ItemBuilder.builder(Material.YELLOW_CONCRETE).setName(ChatColor.GOLD + "Reload").build(), item -> GuiManager.openInventory(p, guiId)), 2, 0);
            items.fillWith(ItemBuilder.builder(Material.GRAY_STAINED_GLASS_PANE).setName("").build());
            for (int j = count; j < count + 27; j++) {
                if (j > values.length - 1) {
                    break;
                }
                String fileName = values[j];
                ItemStack build = ItemBuilder.builder(Material.PAPER).setName(ChatColor.GRAY + fileName).build();
                Util.addLoreLine(build, ChatColor.GREEN + "Click to open with this value!");
                Util.addLoreLine(build, ChatColor.RED + "Right click to delete!");
                items.addItem(new GuiItem(build, event -> {
                    if (event.isLeftClick()) {
                        IEntry e = LootEntryManager.getEntry(fileName);
                        switch (e.getEntryType()) {
                            case MOB -> openCreateUiWithValue(p, "mobEntry", fileName, e);
                            case ITEM -> openCreateUiWithValue(p, "itemEntry", fileName, e);
                            case POTION -> openCreateUiWithValue(p, "potionEntry", fileName, e);
                            case COMMAND -> openCreateUiWithValue(p, "commandEntry", fileName, e);
                            case STRUCTURE -> openCreateUiWithValue(p, "structureEntry", fileName, e);
                            case WAND -> openCreateUiWithValue(p, "wandEntry", fileName, e);
                        }
                    } else if (event.isRightClick()) {
                        deleteFile(new File(plugin.getDataFolder() + "/lootEntries", fileName + ".yml"));
                        GuiManager.openInventory(p, "entryManagement");
                    }
                }), (j - count) % 7, (j - count) / 7);
            }
            count += 27;
            Util.addReturnButton(border, p, backButtonLink, 0, 4);
            pane.addPane(i, border);
            pane.addPane(i, items);
        }
        gui.addPane(pane);
        return gui;
    }

    public static boolean saveInfo(Player player, boolean b, String name, YamlConfiguration save, String folder) {
        if (b) {
            Util.invalid("Invalid info!", player);
            return true;
        }
        if (name == null) {
            Util.invalid("Please specify a name!", player);
            return true;
        }
        YamlConfiguration config = save;
        try {
            config.save(new File(JavaPlugin.getPlugin(SimpleLuckyBlocksCore.class).getDataFolder() + "/" + folder, name + ".yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T extends IEntry> void openCreateUiWithValue(Player p, String createUi, String fileName, T value) {
        ((EntryCreateGui<T>) CreateGuiManager.getCreateGui(createUi)).setSave(p.getUniqueId(), new EntrySaveWrapper<>(fileName, value));
        GuiManager.openInventory(p, createUi + "Create");
    }


    public static ConfigurationSection getEnchantSection(Map<Enchantment, Integer> enchants) {
        ConfigurationSection returned = new YamlConfiguration();
        for (Enchantment e : enchants.keySet()) {
            String key = e.getKey().getKey();
            int lvl = enchants.get(e);
            ConfigurationSection subSection = new YamlConfiguration();
            subSection.set("id", key);
            subSection.set("lvl", lvl);
            returned.set(key, subSection);
        }
        return returned;
    }

    public static <T extends Renamable> void addRenameButton(StaticPane mainPane, Player player, Map<UUID, T> toChange, T def, String currentUi, int x, int y) {
        mainPane.addItem(new GuiItem(ItemBuilder.builder(Material.ACACIA_SIGN).setName(ChatColor.RED + "Rename").setLore(ChatColor.GRAY + "Current name: " + toChange.getOrDefault(player.getUniqueId(), def).name()).build(), event -> {
            ChatListener.startChatListen(player, currentUi, ChatColor.RED + "Enter new name!", pl -> {
                String input = ChatListener.getCurrentInput(pl);
                if (input.length() < 2 || input.length() > 30) {
                    Util.invalid("Characters must be at least 2 and at most 29!", pl);
                    return;
                }
                if (Util.isInvalidFilePath(input)) {
                    Util.invalid("Invalid File Name!", pl);
                    return;
                }
                if (!toChange.containsKey(pl.getUniqueId())) {
                    toChange.put(pl.getUniqueId(), def);
                }
                toChange.get(pl.getUniqueId()).setName(input);
            });
            player.closeInventory();
        }), x, y);
    }

    public static ItemStack addEditable(ItemStack i) {
        if (i.hasItemMeta()) {
            if (!(i.getItemMeta().getLore() == null)) {
                if (!i.getItemMeta().getLore().get(i.getItemMeta().getLore().size() - 1).equals(ChatColor.GREEN + "Click to edit!")) {
                    addLoreLine(i, ChatColor.GREEN + "Click to edit!");
                }
            } else {
                addLoreLine(i, ChatColor.GREEN + "Click to edit");
            }
        }
        return i;
    }

    public static <T, U> void handleSelectInput(String selectInput, Player player, Map<UUID, T> values, T def, Class<U> clazz, BiConsumer<T, U> handler) {
        UUID pId = player.getUniqueId();
        U b = (U) SelectGuiManager.getSelectGui(selectInput).getValue(pId);
        if (b != null) {
            if (!values.containsKey(pId)) {
                values.put(pId, def);
            }
            handler.accept(values.get(pId), b);
            SelectGuiManager.getSelectGui(selectInput).resetValue(pId);
        }
    }

    public static ItemStack getInfoItem(String title, String currentValue) {
        return ItemBuilder.builder(Material.PAPER).setName(ChatColor.BLUE + title).setLore(ChatColor.GRAY + "" + currentValue).build();
    }

    public static void changePage(PaginatedPane p, int amount) {
        int current = p.getPage();
        int newAmount = current + amount;
        if (newAmount < 0) p.setPage(0);
        else p.setPage(Math.min(newAmount, p.getPages() - 1));
    }

    /**
     * Generation head type enum
     */
    public enum HeadType {
        PLAYER_HEAD,
        BASE64
    }
}

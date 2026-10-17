package com.zenkaiorecycle;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Settings you can change without rebuilding: config/zenkaiorecycle.properties (created on first launch). */
final class Config {
    static String vaultCommand = "vault";
    static String craftCommand = "craft";
    static int spareOre = 45;
    static String mineGold = "#mine gold_ore deepslate_gold_ore";
    static String mineCoal = "#mine coal_ore deepslate_coal_ore";
    static String stopCommand = "#stop";

    private Config() {}

    static void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("zenkaiorecycle.properties");
        Properties p = new Properties();
        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                p.load(in);
            } catch (IOException ignored) {
                // use defaults
            }
        }
        vaultCommand = p.getProperty("vault_command", vaultCommand).trim();
        if (vaultCommand.startsWith("/")) vaultCommand = vaultCommand.substring(1);
        craftCommand = p.getProperty("craft_command", craftCommand).trim();
        if (craftCommand.startsWith("/")) craftCommand = craftCommand.substring(1);
        mineGold = p.getProperty("mine_gold_command", mineGold).trim();
        mineCoal = p.getProperty("mine_coal_command", mineCoal).trim();
        stopCommand = p.getProperty("stop_command", stopCommand).trim();
        try {
            spareOre = Integer.parseInt(p.getProperty("spare_ore", Integer.toString(spareOre)).trim());
        } catch (NumberFormatException ignored) {
            // keep default
        }

        p.setProperty("vault_command", vaultCommand);
        p.setProperty("craft_command", craftCommand);
        p.setProperty("spare_ore", Integer.toString(spareOre));
        p.setProperty("mine_gold_command", mineGold);
        p.setProperty("mine_coal_command", mineCoal);
        p.setProperty("stop_command", stopCommand);
        try (OutputStream out = Files.newOutputStream(path)) {
            p.store(out, "Zenkai Ore Cycle. spare_ore = ore kept back and stored with the blocks each cycle.");
        } catch (IOException ignored) {
            // not worth failing over
        }
    }
}

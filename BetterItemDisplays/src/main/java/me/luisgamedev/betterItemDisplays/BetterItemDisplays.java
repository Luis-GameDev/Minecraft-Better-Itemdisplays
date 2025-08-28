package me.luisgamedev.betterItemDisplays;

import me.luisgamedev.betterItemDisplays.commands.DisplayCommand;
import me.luisgamedev.betterItemDisplays.language.LanguageManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterItemDisplays extends JavaPlugin {
    private static BetterItemDisplays instance;
    private NamespacedKey ownerKey;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        instance = this;
        ownerKey = new NamespacedKey(this, "owner");
        languageManager = new LanguageManager(this);

        if (getCommand("display") != null) {
            getCommand("display").setExecutor(new DisplayCommand());
            getCommand("display").setTabCompleter(new DisplayCommand());
        }
    }

    public static BetterItemDisplays getInstance() {
        return instance;
    }

    public LanguageManager getLang() {
        return languageManager;
    }

    public NamespacedKey getOwnerKey() {
        return ownerKey;
    }
}

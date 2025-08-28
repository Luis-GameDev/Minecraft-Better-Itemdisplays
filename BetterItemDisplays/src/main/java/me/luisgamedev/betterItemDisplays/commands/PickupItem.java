package me.luisgamedev.betterItemDisplays.commands;

import me.luisgamedev.betterItemDisplays.BetterItemDisplays;
import me.luisgamedev.betterItemDisplays.language.LanguageManager;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.UUID;

public class PickupItem {
    LanguageManager lang;

    public boolean execute(Player p, String[] args) {
        ItemDisplay target = getTargetDisplay(p);
        if (target == null) {
            p.sendMessage(lang.get("no-display"));
            return true;
        }
        if (!canModify(p, target)) {
            p.sendMessage(lang.get("not-allowed-pickup"));
            return true;
        }
        ItemStack stack = target.getItemStack();
        if (stack == null || stack.getType().isAir()) {
            target.remove();
            p.sendMessage(lang.get("no-pickup"));
            return true;
        }
        HashMapUtil.giveOrDrop(p, stack.clone());
        target.remove();
        return true;
    }

    private ItemDisplay getTargetDisplay(Player p) {
        World w = p.getWorld();
        RayTraceResult r = w.rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), 6.0, 0.3, e -> e instanceof ItemDisplay);
        if (r == null || r.getHitEntity() == null) return null;
        if (r.getHitEntity() instanceof ItemDisplay id) return id;
        return null;
    }

    private boolean canModify(Player p, ItemDisplay d) {
        if (p.hasPermission("betteritemdisplays.admin")) return true;
        String s = d.getPersistentDataContainer().get(BetterItemDisplays.getInstance().getOwnerKey(), PersistentDataType.STRING);
        if (s == null) return false;
        try {
            return UUID.fromString(s).equals(p.getUniqueId());
        } catch (Exception e) {
            return false;
        }
    }

    private static class HashMapUtil {
        static void giveOrDrop(Player p, ItemStack stack) {
            var inv = p.getInventory();
            HashMap<Integer, ItemStack> rest = inv.addItem(stack);
            if (!rest.isEmpty()) p.getWorld().dropItemNaturally(p.getLocation(), stack);
        }
    }
}

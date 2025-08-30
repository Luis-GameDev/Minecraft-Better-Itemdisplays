package me.luisgamedev.betterItemDisplays.commands;

import me.luisgamedev.betterItemDisplays.BetterItemDisplays;
import me.luisgamedev.betterItemDisplays.language.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

public class PickupItem {

    public boolean execute(Player p, String[] args) {
        LanguageManager lang = BetterItemDisplays.getInstance().getLang();

        if (!p.hasPermission("betteritemdisplays.pickup")) { p.sendMessage(lang.get("no-permission")); return true; }

        ItemDisplay target = getTargetDisplay(p);
        if (target == null) {
            p.sendMessage(lang.get("must-target-display"));
            return true;
        }

        Block breakCheck = findRelevantBlockForBreakCheck(p, target);
        if (breakCheck == null || breakCheck.isEmpty()) {
            p.sendMessage(lang.get("not-allowed-pickup"));
            return true;
        }

        BlockBreakEvent breakEvent = new BlockBreakEvent(breakCheck, p);
        Bukkit.getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            p.sendMessage(lang.get("not-allowed-pickup"));
            return true;
        }

        ItemStack stack = target.getItemStack();
        if (stack == null || stack.getType().isAir()) {
            target.remove();
            p.sendMessage(lang.get("no-display"));
            return true;
        }

        ItemStack give = stack.clone();
        var leftovers = p.getInventory().addItem(give);
        if (!leftovers.isEmpty()) {
            leftovers.values().forEach(remaining ->
                    p.getWorld().dropItemNaturally(p.getLocation(), remaining));
        }

        target.remove();
        return true;
    }

    private ItemDisplay getTargetDisplay(Player p) {
        World w = p.getWorld();
        RayTraceResult r = w.rayTraceEntities(
                p.getEyeLocation(),
                p.getEyeLocation().getDirection(),
                6.0,
                0.3,
                e -> e instanceof ItemDisplay
        );
        if (r == null || r.getHitEntity() == null) return null;
        return (ItemDisplay) r.getHitEntity();
    }

    private Block findRelevantBlockForBreakCheck(Player p, ItemDisplay target) {
        Location eye = p.getEyeLocation();
        BoundingBox box = target.getBoundingBox();
        Location center = new Location(target.getWorld(),
                box.getCenterX(), box.getCenterY(), box.getCenterZ());

        var toVec = center.toVector().subtract(eye.toVector());
        double dist = toVec.length();
        if (dist <= 0.0) dist = 0.1;

        RayTraceResult rb = p.getWorld().rayTraceBlocks(
                eye,
                toVec.normalize(),
                dist,
                FluidCollisionMode.NEVER
        );

        if (rb != null && rb.getHitBlock() != null) {
            return rb.getHitBlock();
        }

        Block at = target.getLocation().getBlock();
        if (!at.isEmpty()) return at;

        Block below = at.getRelative(0, -1, 0);
        return below;
    }
}

package me.luisgamedev.betterItemDisplays.commands;

import me.luisgamedev.betterItemDisplays.BetterItemDisplays;
import me.luisgamedev.betterItemDisplays.language.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;

public class EditItem {
    LanguageManager lang;

    public boolean execute(Player p, String[] args) {
        lang = BetterItemDisplays.getInstance().getLang();

        if (args.length == 0) {
            p.sendMessage(lang.get("missing-parameters-rotation"));
            p.sendMessage(lang.get("missing-parameters-scale"));
            p.sendMessage(lang.get("missing-parameters-move"));
            return true;
        }

        ItemDisplay target = getTargetDisplay(p);
        if (target == null) { p.sendMessage(lang.get("must-target-display")); return true; }
        if (!canModify(p, target)) { p.sendMessage(lang.get("not-allowed-edit")); return true; }

        String type = args[0].toLowerCase();

        if (type.equals("rotation")) {
            if (args.length < 4) { p.sendMessage(lang.get("missing-parameters-rotation")); return true; }
            double dx, dy, dz;
            try { dx = Double.parseDouble(args[1]); dy = Double.parseDouble(args[2]); dz = Double.parseDouble(args[3]); }
            catch (Exception e) { p.sendMessage(lang.get("invalid-rotation")); return true; }

            Transformation t = target.getTransformation();
            Quaternionf current = new Quaternionf(t.getLeftRotation());

            Quaternionf deltaWorld = new Quaternionf()
                    .rotateX((float) Math.toRadians(dx))
                    .rotateY((float) Math.toRadians(dy))
                    .rotateZ((float) Math.toRadians(dz));

            Quaternionf newLeft = new Quaternionf(deltaWorld).mul(current);

            target.setTransformation(new Transformation(
                    new Vector3f(t.getTranslation()),
                    newLeft,
                    new Vector3f(t.getScale()),
                    new Quaternionf(t.getRightRotation())
            ));
            p.sendMessage(lang.get("edited"));
            return true;
        }

        if (type.equals("scale")) {
            if (args.length < 2) { p.sendMessage(lang.get("missing-parameters-scale")); return true; }
            double s;
            try { s = Double.parseDouble(args[1]); } catch (Exception e) { p.sendMessage(lang.get("invalid-scale")); return true; }
            double maxScale = BetterItemDisplays.getInstance().getConfig().getDouble("max-item-scale", 2.0);
            if (maxScale < 0.1) maxScale = 0.1;
            if (s < 0.1) s = 0.1;
            if (s > maxScale) s = maxScale;

            Transformation t = target.getTransformation();
            target.setTransformation(new Transformation(
                    new Vector3f(t.getTranslation()),
                    new Quaternionf(t.getLeftRotation()),
                    new Vector3f((float) s, (float) s, (float) s),
                    new Quaternionf(t.getRightRotation())
            ));
            p.sendMessage(lang.get("edited"));
            return true;
        }

        if (type.equals("move")) {
            if (args.length < 4) { p.sendMessage(lang.get("missing-parameters-move")); return true; }
            double dx, dy, dz;
            try { dx = Double.parseDouble(args[1]); dy = Double.parseDouble(args[2]); dz = Double.parseDouble(args[3]); }
            catch (Exception e) { p.sendMessage(lang.get("invalid-move")); return true; }

            double maxMove = BetterItemDisplays.getInstance().getConfig().getDouble("max-item-move", 1.0);
            if (Math.abs(dx) > maxMove || Math.abs(dy) > maxMove || Math.abs(dz) > maxMove) {
                p.sendMessage(lang.get("move-too-far").replace("{max}", String.valueOf(maxMove)));
                return true;
            }

            Location from = target.getLocation();
            Location to = from.clone().add(dx, dy, dz);

            ItemStack checkStack = target.getItemStack() == null ? new ItemStack(p.getInventory().getItemInMainHand()) : target.getItemStack().clone();
            Block newBlock = to.getBlock();
            Block against = newBlock.getRelative(BlockFace.DOWN);

            BlockPlaceEvent placeCheck = new BlockPlaceEvent(newBlock, newBlock.getState(), against, checkStack, p, true, EquipmentSlot.HAND);
            Bukkit.getPluginManager().callEvent(placeCheck);
            if (placeCheck.isCancelled()) {
                p.sendMessage(lang.get("not-allowed-place"));
                return true;
            }

            target.teleport(to);
            p.sendMessage(lang.get("edited"));
            return true;
        }

        p.sendMessage(lang.get("unknown-parameter"));
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
        try { return UUID.fromString(s).equals(p.getUniqueId()); }
        catch (Exception e) { return false; }
    }
}

package me.luisgamedev.betterItemDisplays.commands;

import me.luisgamedev.betterItemDisplays.BetterItemDisplays;
import me.luisgamedev.betterItemDisplays.language.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
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

public class PlaceItem {
    LanguageManager lang;

    public boolean execute(Player p, String[] args) {
        lang = BetterItemDisplays.getInstance().getLang();

        ItemStack hand = p.getInventory().getItem(EquipmentSlot.HAND);
        if (hand == null || hand.getType().isAir()) { p.sendMessage(lang.get("must-hold-item")); return true; }

        double scale = 1.0;
        boolean stick = false;
        if (args.length >= 1) try { scale = Double.parseDouble(args[0]); } catch (Exception ignored) {}
        if (args.length >= 2) stick = args[1].equalsIgnoreCase("true");

        double maxScale = BetterItemDisplays.getInstance().getConfig().getDouble("max-item-scale", 2.0);
        if (maxScale < 0.1) maxScale = 0.1;
        if (scale < 0.1) scale = 0.1;
        if (scale > maxScale) scale = maxScale;

        World w = p.getWorld();
        RayTraceResult rb = w.rayTraceBlocks(p.getEyeLocation(), p.getEyeLocation().getDirection(), 6.0, FluidCollisionMode.NEVER);
        if (rb == null || rb.getHitPosition() == null || rb.getHitBlockFace() == null || rb.getHitBlock() == null) {
            p.sendMessage(lang.get("must-target-block")); return true;
        }

        Block hitBlock = rb.getHitBlock();
        BlockFace face = rb.getHitBlockFace();

        Block targetForCheck = hitBlock.getRelative(face);
        BlockPlaceEvent event = new BlockPlaceEvent(targetForCheck, targetForCheck.getState(), hitBlock, hand.clone(), p, true, EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) { p.sendMessage(lang.get("not-allowed-place")); return true; }

        Location hitPos = new Location(w, rb.getHitPosition().getX(), rb.getHitPosition().getY(), rb.getHitPosition().getZ());

        Vector3f normal = new Vector3f(face.getModX(), face.getModY(), face.getModZ());
        Vector3f rayDir = new Vector3f(
                (float) p.getEyeLocation().getDirection().getX(),
                (float) p.getEyeLocation().getDirection().getY(),
                (float) p.getEyeLocation().getDirection().getZ()
        ).normalize();

        Location placePos;
        Quaternionf baseRot;

        if (stick) {
            double embed = 0.06;
            placePos = hitPos.clone().add(rayDir.x * embed, rayDir.y * embed, rayDir.z * embed);

            Quaternionf lookOut = new Quaternionf().lookAlong(new Vector3f(rayDir).negate(), new Vector3f(0, 1, 0));
            Quaternionf modelFix = new Quaternionf()
                    .rotateX((float) Math.toRadians(90))
                    .rotateY((float) Math.toRadians(180));
            baseRot = new Quaternionf(lookOut).mul(modelFix);
        } else {
            double eps = 0.001; 
            placePos = hitPos.clone().add(normal.x * eps, normal.y * eps, normal.z * eps);
            baseRot = flatRotationForFace(face);
        }

        Vector3f s = new Vector3f((float) scale, (float) scale, (float) scale);
        ItemStack single = hand.clone(); single.setAmount(1);

        ItemDisplay d = w.spawn(placePos, ItemDisplay.class);
        d.setItemStack(single);
        d.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        d.setTransformation(new Transformation(new Vector3f(0, 0, 0), baseRot, s, new Quaternionf()));
        d.getPersistentDataContainer().set(BetterItemDisplays.getInstance().getOwnerKey(), PersistentDataType.STRING, p.getUniqueId().toString());

        if (hand.getAmount() > 1) { hand.setAmount(hand.getAmount() - 1); p.getInventory().setItem(EquipmentSlot.HAND, hand); }
        else { p.getInventory().setItem(EquipmentSlot.HAND, null); }

        return true;
    }

    private Quaternionf flatRotationForFace(BlockFace face) {
        switch (face) {
            case UP:    return new Quaternionf().rotateX((float) Math.toRadians(90));
            case DOWN:  return new Quaternionf().rotateX((float) Math.toRadians(-90));
            case NORTH: return new Quaternionf().rotateY((float) Math.toRadians(180));
            case SOUTH: return new Quaternionf();
            case WEST:  return new Quaternionf().rotateY((float) Math.toRadians(90));
            case EAST:  return new Quaternionf().rotateY((float) Math.toRadians(-90));
            default:    return new Quaternionf();
        }
    }
}

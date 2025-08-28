package me.luisgamedev.betterItemDisplays.commands;

import me.luisgamedev.betterItemDisplays.BetterItemDisplays;
import me.luisgamedev.betterItemDisplays.language.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayCommand implements CommandExecutor, TabCompleter {
    LanguageManager lang;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (!p.hasPermission("betteritemdisplays.use")) return true;

        if (args.length == 0) {
            p.sendMessage("§e/display place <scale> <stickInBlock:true|false>");
            p.sendMessage("§e/display edit rotation <dX> <dY> <dZ>");
            p.sendMessage("§e/display edit scale <value>");
            p.sendMessage("§e/display edit move <dx> <dy> <dz>");
            p.sendMessage("§e/display pickup");
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("place")) return new PlaceItem().execute(p, Arrays.copyOfRange(args, 1, args.length));
        if (sub.equals("edit")) return new EditItem().execute(p, Arrays.copyOfRange(args, 1, args.length));
        if (sub.equals("pickup")) return new PickupItem().execute(p, Arrays.copyOfRange(args, 1, args.length));

        p.sendMessage(lang.get("unknown-subcommand"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player)) return out;

        double maxScale = BetterItemDisplays.getInstance().getConfig().getDouble("max-item-scale", 2.0);

        if (args.length == 1) {
            out.add("place");
            out.add("edit");
            out.add("pickup");
            return filter(out, args[0]);
        }

        if (args[0].equalsIgnoreCase("place")) {
            if (args.length == 2) {
                out.add("1.0");
                out.add(String.format("%.1f", Math.min(0.5, maxScale)));
                out.add(String.format("%.1f", Math.min(1.5, maxScale)));
                out.add(String.format("%.1f", Math.min(2.0, maxScale)));
                return filter(out, args[1]);
            }
            if (args.length == 3) {
                out.add("true");
                out.add("false");
                return filter(out, args[2]);
            }
            return out;
        }

        if (args[0].equalsIgnoreCase("edit")) {
            if (args.length == 2) {
                out.add("rotation");
                out.add("scale");
                out.add("move");
                return filter(out, args[1]);
            }
            if (args.length >= 3) {
                String sub = args[1].toLowerCase();
                if (sub.equals("rotation")) {
                    if (args.length == 3) { out.addAll(Arrays.asList("0", "15", "45", "90", "180")); return filter(out, args[2]); }
                    if (args.length == 4) { out.addAll(Arrays.asList("0", "15", "45", "90", "180")); return filter(out, args[3]); }
                    if (args.length == 5) { out.addAll(Arrays.asList("0", "15", "45", "90", "180")); return filter(out, args[4]); }
                    return out;
                }
                if (sub.equals("scale")) {
                    if (args.length == 3) {
                        out.add("0.5");
                        out.add("1.0");
                        out.add(String.format("%.1f", Math.min(1.5, maxScale)));
                        out.add(String.format("%.1f", Math.min(2.0, maxScale)));
                        return filter(out, args[2]);
                    }
                    return out;
                }
                if (sub.equals("move")) {
                    List<String> steps = Arrays.asList("-1.0", "-0.5", "0", "0.5", "1.0");
                    if (args.length == 3) return filter(new ArrayList<>(steps), args[2]);
                    if (args.length == 4) return filter(new ArrayList<>(steps), args[3]);
                    if (args.length == 5) return filter(new ArrayList<>(steps), args[4]);
                    return out;
                }
            }
        }

        if (args[0].equalsIgnoreCase("pickup")) {
            return out;
        }

        return out;
    }

    private List<String> filter(List<String> list, String token) {
        List<String> r = new ArrayList<>();
        String low = token.toLowerCase();
        for (String s : list) if (s.toLowerCase().startsWith(low)) r.add(s);
        return r;
    }
}

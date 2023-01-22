package mc.obliviate.seniorregions.commands;

import mc.obliviate.seniorregions.Region;
import mc.obliviate.seniorregions.SeniorRegions;
import mc.obliviate.seniorregions.gui.RegionGui;
import mc.obliviate.seniorregions.gui.RegionsGui;
import mc.obliviate.seniorregions.util.PositionSelection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

public class RegionCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arguments) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "/This command is in-game only.");
            return false;
        }

        final List<String> args = List.of(arguments);
        if (args.size() == 0) return handleGuiOfRegions(player, args);

        if (args.get(0).equalsIgnoreCase("create")) return handleCreate(player, args);
        else if (args.get(0).equalsIgnoreCase("wand")) return handleWand(player, args);
        else if (args.get(0).equalsIgnoreCase("add")) return handleAdd(player, args);
        else if (args.get(0).equalsIgnoreCase("remove")) return handleRemove(player, args);
        else if (args.get(0).equalsIgnoreCase("whitelist")) return handleWhitelist(player, args);
        else return handleGuiOfRegion(player, args);
    }

    private boolean handleCreate(Player player, List<String> args) {
        if (!player.hasPermission("region.create")) {
            player.sendMessage(ChatColor.RED + "You don't have enough permission.");
            return false;
        }

        if (args.size() == 1) {
            player.sendMessage(ChatColor.RED + "/region create <region name>");
            return false;
        }
        PositionSelection selection = PositionSelection.get(player.getUniqueId());
        if (!selection.isConvertableToCuboid()) {
            player.sendMessage(ChatColor.RED + "You have to select 2 position using wand.");
            return false;
        }

        Region region = new Region(selection.toCuboid(), args.get(1));
        SeniorRegions.getRegionDatabase().async(db -> db.save(region));
        player.sendMessage(ChatColor.GREEN + "You created new region successfully.");
        return true;
    }

    private boolean handleAdd(Player player, List<String> args) {
        if (!player.hasPermission("region.add")) {
            player.sendMessage(ChatColor.RED + "You don't have enough permission.");
            return false;
        }

        if (args.size() == 1) {
            player.sendMessage(ChatColor.RED + "/region add <region> <player>");
            return false;
        }
        if (args.size() == 2) {
            player.sendMessage(ChatColor.RED + "/region add <region> <player>");
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args.get(2));

        Optional<Region> region = Region.findByName(args.get(1));
        if (region.isPresent()) {
            region.get().getPlayers().add(target.getUniqueId());
            player.sendMessage(ChatColor.GREEN + target.getName() + " added to whitelist.");
            SeniorRegions.getRegionDatabase().async(db -> db.save(region.get()));
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "Region could not find.");
            return false;
        }
    }

    private boolean handleRemove(Player player, List<String> args) {
        if (!player.hasPermission("region.remove")) {
            player.sendMessage(ChatColor.RED + "You don't have enough permission.");
            return false;
        }
        if (args.size() == 1) {
            player.sendMessage(ChatColor.RED + "/region remove <region> <player>");
            return false;
        }
        if (args.size() == 2) {
            player.sendMessage(ChatColor.RED + "/region remove <region> <player>");
            return false;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args.get(2));

        Optional<Region> region = Region.findByName(args.get(1));
        if (region.isPresent()) {
            region.get().getPlayers().remove(target.getUniqueId());
            player.sendMessage(ChatColor.GREEN + target.getName() + " removed from whitelist.");
            SeniorRegions.getRegionDatabase().async(db -> db.save(region.get()));
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "Region could not find.");
            return false;
        }
    }

    private boolean handleWhitelist(Player player, List<String> args) {
        if (!player.hasPermission("region.whitelist")) {
            player.sendMessage(ChatColor.RED + "You don't have enough permission.");
            return false;
        }

        if (args.size() == 1) {
            player.sendMessage(ChatColor.RED + "/region whitelist <region>");
            return false;
        }
        Optional<Region> region = Region.findByName(args.get(1));
        if (region.isPresent()) {
            player.sendMessage(ChatColor.GOLD + "Whitelist:");
            region.get().getPlayers().forEach(p -> player.sendMessage(ChatColor.GRAY + " - " + Bukkit.getOfflinePlayer(p).getName()));
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "Region could not find.");
            return false;
        }
    }

    private boolean handleWand(Player player, List<String> args) {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Region Wand");
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        return true;
    }

    private boolean handleGuiOfRegion(Player player, List<String> args) {
        if (!player.hasPermission("region.menu")) {
            player.sendMessage(ChatColor.RED + "You don't have enough permission.");
            return false;
        }
        String regionName = args.get(0);
        Optional<Region> region = Region.findByName(regionName);
        if (region.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Could not found any region by name: " + regionName);
            return false;
        }

        new RegionGui(player, region.get()).open();
        return true;
    }

    private boolean handleGuiOfRegions(Player player, List<String> args) {
        if (!player.hasPermission("region.menu")) {
            player.sendMessage(ChatColor.RED + "You don't have enough permission.");
            return false;
        }
        new RegionsGui(player).open();
        return true;
    }

}

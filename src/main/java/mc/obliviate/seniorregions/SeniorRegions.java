package mc.obliviate.seniorregions;

import mc.obliviate.inventory.InventoryAPI;
import mc.obliviate.seniorregions.commands.RegionCmd;
import mc.obliviate.seniorregions.database.IRegionDatabase;
import mc.obliviate.seniorregions.database.RegionDatabase;
import mc.obliviate.seniorregions.util.PositionSelection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public class SeniorRegions extends JavaPlugin {

    private static final IRegionDatabase regionDatabase = new RegionDatabase();

    public static SeniorRegions getInstance() {
        return JavaPlugin.getPlugin(SeniorRegions.class);
    }

    @Override
    public void onEnable() {
        regionDatabase.connect();
        regionDatabase.loadAll();

        new InventoryAPI(this).init();
        Objects.requireNonNull(getCommand("region")).setExecutor(new RegionCmd());

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onWandSelect(PlayerInteractEvent e) {
                if (e.getClickedBlock() == null) return;
                if (e.getItem() == null) return;
                if (!e.getItem().hasItemMeta()) return;
                if (!e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Region Wand"))
                    return;
                final PositionSelection selection = PositionSelection.get(e.getPlayer().getUniqueId());
                if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    selection.setPos1(e.getClickedBlock().getLocation());
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "Pos 1 selected");
                } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    selection.setPos2(e.getClickedBlock().getLocation());
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "Pos 2 selected");
                }
                e.setCancelled(true);
            }

            @EventHandler
            public void onInteract(PlayerInteractEvent e) {
                if (e.getClickedBlock() == null) return;
                List<Region> regions = Region.findByLoc(e.getClickedBlock().getLocation());
                if (regions.stream().allMatch(region -> region.getPlayers().contains(e.getPlayer().getUniqueId()))) return;
                if (e.getPlayer().hasPermission("region.bypass")) return;

                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "You cannot interact with this area.");
            }
        }, this);
    }

    @Override
    public void onDisable() {
        regionDatabase.disconnect();
    }

    public static IRegionDatabase getRegionDatabase() {
        return regionDatabase;
    }
}

package mc.obliviate.seniorregions.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.seniorregions.Region;
import mc.obliviate.seniorregions.util.ChatEntry;
import mc.obliviate.seniorregions.util.PositionSelection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import javax.annotation.Nonnull;

public class RegionGui extends Gui {

    private final Region region;

    public RegionGui(@Nonnull Player player, Region region) {
        super(player, "region-gui", "Region: " + region.getName(), 3);
        this.region = region;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        addItem(10, new Icon(Material.NAME_TAG).setName(ChatColor.GOLD + "Current name: " + region.getName()).setLore(ChatColor.YELLOW + "Click to change").onClick(e -> {
            new ChatEntry(event.getPlayer().getUniqueId()).onResponse(chatEvent -> {
                region.setName(chatEvent.getMessage());
                setTitle("Region: " + region.getName());
                open();
                player.sendMessage(ChatColor.YELLOW + "Updated.");
                player.sendMessage(ChatColor.YELLOW + "Type a name to rename the region. Type 'cancel' to cancel the process.");

            });
            player.closeInventory();
        }));
        addItem(12, new Icon(Material.GLASS).setName(ChatColor.GOLD + "Change cuboid").setLore(ChatColor.YELLOW + "Click to change").onClick(e -> {
            PositionSelection selection = PositionSelection.get(e.getWhoClicked().getUniqueId());
            region.getCuboid().setPositions(selection.getPos1(), selection.getPos2());
            open();
            player.sendMessage(ChatColor.YELLOW + "Updated.");
        }));

        addItem(14, new Icon(Material.GREEN_WOOL).setName(ChatColor.GOLD + "Whitelist Add").setLore(ChatColor.YELLOW + "Click to add").onClick(e -> {
            new ChatEntry(event.getPlayer().getUniqueId()).onResponse(chatEvent -> {
                player.performCommand("region add " + region.getName() + " " + chatEvent.getMessage());
                open();
            });
            player.closeInventory();
            player.sendMessage(ChatColor.YELLOW + "Type a player name to add from whitelist. Type 'cancel' to cancel the process.");

        }));

        addItem(15, new Icon(Material.RED_WOOL).setName(ChatColor.GOLD + "Whitelist Remove").setLore(ChatColor.YELLOW + "Click to remove").onClick(e -> {
            new ChatEntry(event.getPlayer().getUniqueId()).onResponse(chatEvent -> {
                player.performCommand("region remove " + region.getName() + " " + chatEvent.getMessage());
                open();
            });
            player.sendMessage(ChatColor.YELLOW + "Type a player name to remove from whitelist. Type 'cancel' to cancel the process.");
            player.closeInventory();
        }));
    }
}

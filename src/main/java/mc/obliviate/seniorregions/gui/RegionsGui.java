package mc.obliviate.seniorregions.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.seniorregions.Region;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import javax.annotation.Nonnull;

public class RegionsGui extends Gui {

    public RegionsGui(@Nonnull Player player) {
        super(player, "regions-gui", "All Regions", 6);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillRow(new Icon(Material.BLACK_STAINED_GLASS_PANE), 0);
        int slot = 9;
        for (Region region : Region.getCache().values()) {
            addItem(slot++, new Icon(Material.GLASS).setName(region.getName()).onClick(e -> new RegionGui(player, region).open()));
        }
    }
}

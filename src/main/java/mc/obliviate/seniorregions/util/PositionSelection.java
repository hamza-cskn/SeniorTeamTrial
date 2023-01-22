package mc.obliviate.seniorregions.util;

import mc.obliviate.seniorregions.Cuboid;
import org.bukkit.Location;

import java.util.*;

public class PositionSelection {

    private static final Map<UUID, PositionSelection> POSITION_SELECTION_MAP = new HashMap<>();

    private Location pos1;
    private Location pos2;

    private PositionSelection(UUID uuid) {
        POSITION_SELECTION_MAP.put(uuid, this);
    }

    @SuppressWarnings("ReplaceNullCheck")
    public static PositionSelection get(UUID uuid) {
        var selection = POSITION_SELECTION_MAP.get(uuid);
        if (selection != null) return selection;
        return new PositionSelection(uuid); //i know Map#getOrDefault method.
    }

    public static Map<UUID, PositionSelection> getPositionSelectionMap() {
        return Collections.unmodifiableMap(POSITION_SELECTION_MAP);
    }

    public boolean isConvertableToCuboid() {
        return pos1 != null && pos2 != null && Objects.equals(pos1.getWorld(), pos2.getWorld());
    }

    public Cuboid toCuboid() {
        return new Cuboid(pos1, pos2);
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

}

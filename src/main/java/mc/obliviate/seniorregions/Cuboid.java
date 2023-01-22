package mc.obliviate.seniorregions;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Cuboid {

    private Location pos1;
    private Location pos2;
    private World world;

    public Cuboid(final Location pos1, final Location pos2) {
        setPositions(pos1, pos2);
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPositions(final Location pos1, final Location pos2) {
        Preconditions.checkNotNull(pos1.getWorld(), "worlds of positions cannot be null(1)");
        Preconditions.checkNotNull(pos2.getWorld(), "worlds of positions cannot be null(2)");
        Preconditions.checkArgument(pos1.getWorld() != null && Objects.equals(pos1.getWorld(), pos2.getWorld()), "worlds of arguments cannot be different");
        this.pos2 = pos2;
        this.pos1 = pos1;
        this.world = pos1.getWorld();
    }

    public boolean isIn(final Location loc) {
        return loc.getWorld() == this.world &&
                loc.getBlockX() >= this.getxMin() &&
                loc.getBlockX() <= this.getxMax() &&
                loc.getBlockY() >= this.getyMin() &&
                loc.getBlockY() <= this.getyMax() &&
                loc.getBlockZ() >= this.getzMin() &&
                loc.getBlockZ() <= this.getzMax();
    }

    public int getxMin() {
        return Math.min(pos1.getBlockX(), pos2.getBlockX());
    }

    public int getxMax() {
        return Math.max(pos1.getBlockX(), pos2.getBlockX());
    }

    public int getyMin() {
        return Math.min(pos1.getBlockY(), pos2.getBlockY());
    }

    public int getyMax() {
        return Math.max(pos1.getBlockY(), pos2.getBlockY());
    }

    public int getzMin() {
        return Math.min(pos1.getBlockZ(), pos2.getBlockZ());
    }

    public int getzMax() {
        return Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    }

    public World getWorld() {
        return world;
    }

    public static Cuboid deserialize(String serializedString) {
        String[] serializedPoses = serializedString.split(";");
        return new Cuboid(deserializeLocation(serializedPoses[0]), deserializeLocation(serializedPoses[1]));
    }

    private static Vector deserializeOffset(String string) {
        final String[] values = string.replace(" ", "").split(",");
        Preconditions.checkArgument(values.length == 3, "Invalid offset value given: " + string);

        final double x, y, z;
        try {
            x = Double.parseDouble(values[0]);
            y = Double.parseDouble(values[1]);
            z = Double.parseDouble(values[2]);
            return new Vector(x, y, z);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("invalid double value given: " + exception.getMessage());
        }
    }

    private static Location deserializeLocation(String string) {
        Preconditions.checkState(string.split(",").length == 4, "invalid location value given: " + string);
        String trimmed = string.substring(0, string.lastIndexOf(','));
        Vector vector = deserializeOffset(trimmed);
        String worldName = string.substring(string.lastIndexOf(',') + 1);
        World world = Bukkit.getWorld(worldName);
        Preconditions.checkNotNull(world, "no world found with: " + worldName);
        return vector.toLocation(world);
    }


    public String serialize() {
        return String.join(";", serialize(pos1), serialize(pos2));
    }

    private String serialize(Location location) {
        String res = String.join(",",
                String.valueOf(location.getX()),
                String.valueOf(location.getY()),
                String.valueOf(location.getZ()),
                Objects.requireNonNull(location.getWorld()).getName());
        System.out.println(location + " -> " + res);
        return res;
    }

}

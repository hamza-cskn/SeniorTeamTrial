package mc.obliviate.seniorregions;

import org.bukkit.Location;

import java.util.*;

public class Region {

    private static final Map<String, Region> CACHE = new HashMap<>();
    private final Cuboid cuboid;
    private String name;
    private final List<UUID> players;

    public Region(Cuboid cuboid, String name, List<UUID> players) {
        this.cuboid = cuboid;
        this.name = name;
        this.players = players;
        CACHE.put(name, this);
    }

    public Region(Cuboid cuboid, String name) {
        this(cuboid, name, new ArrayList<>());
    }

    public static Optional<Region> findByName(String name) {
        return Optional.ofNullable(CACHE.get(name));
    }

    public static List<Region> findByLoc(Location loc) {
        return CACHE.values().stream().filter(region -> region.getCuboid().isIn(loc)).toList();
    }

    public static Map<String, Region> getCache() {
        return CACHE;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UUID> getPlayers() {
        return players;
    }
}

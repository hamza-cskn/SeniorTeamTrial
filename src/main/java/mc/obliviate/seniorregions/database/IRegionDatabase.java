package mc.obliviate.seniorregions.database;

import mc.obliviate.seniorregions.Region;
import mc.obliviate.seniorregions.SeniorRegions;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.function.Consumer;

public interface IRegionDatabase {

    void connect();

    void disconnect();

    default void async(Consumer<IRegionDatabase> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(SeniorRegions.getInstance(), () -> consumer.accept(this));
    }

    default void sync(Consumer<IRegionDatabase> consumer) {
        Bukkit.getScheduler().runTask(SeniorRegions.getInstance(), () -> consumer.accept(this));
    }

    default void save(Iterable<Region> objects) {
        objects.forEach(this::save);
    }

    void save(Region object);

    default void save(Region object, Consumer<Region> then) {
        async(db -> {
            save(object);
            then.accept(object);
        });
    }

    Region load(Object id);

    default void load(Object id, Consumer<Region> then) {
        async(db -> then.accept(load(id)));
    }

    List<Region> loadAll();

    default void loadAll(Consumer<List<Region>> then) {
        async(db -> then.accept(loadAll()));
    }

}

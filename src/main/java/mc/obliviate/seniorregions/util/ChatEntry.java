package mc.obliviate.seniorregions.util;

import mc.obliviate.seniorregions.SeniorRegions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class ChatEntry implements Listener {

    private static final Map<UUID, ChatEntry> entryMap = new HashMap<>();
    public Consumer<AsyncPlayerChatEvent> action;

    public ChatEntry(UUID uuid) {
        entryMap.put(uuid, this);
        Bukkit.getPluginManager().registerEvents(this, SeniorRegions.getInstance());
    }

    public ChatEntry(Player player) {
        this(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void handleChatEvent(AsyncPlayerChatEvent e) {
        final Player sender = e.getPlayer();
        final ChatEntry chatEntry = entryMap.get(sender.getUniqueId());
        if (chatEntry == null || chatEntry.getAction() == null) return;
        e.setCancelled(true);
        if (!e.getMessage().equalsIgnoreCase("cancel"))
            Bukkit.getScheduler().runTask(SeniorRegions.getInstance(), () -> chatEntry.getAction().accept(e));
        unregisterEntryTask(sender.getUniqueId());
    }

    public static void unregisterEntryTask(UUID senderUniqueId) {
        HandlerList.unregisterAll(entryMap.get(senderUniqueId));
        entryMap.remove(senderUniqueId);
    }

    public void onResponse(Consumer<AsyncPlayerChatEvent> e) {
        this.action = e;
    }

    public Consumer<AsyncPlayerChatEvent> getAction() {
        return action;
    }

}

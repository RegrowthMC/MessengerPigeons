package org.lushplugins.messengerpigeons.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.lushplugins.followers.api.events.PlayerInteractAtFollowerEvent;
import org.lushplugins.messengerpigeons.MessengerPigeons;
import org.lushplugins.messengerpigeons.pigeon.Pigeon;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PigeonListener implements Listener {
    private final Set<UUID> rateLimit = new HashSet<>();

    @EventHandler
    public void onPlayerInteractAtFollower(PlayerInteractAtFollowerEvent event) {
        if (!(event.getFollower() instanceof Pigeon pigeon)) {
            return;
        }

        if (pigeon.isMailEmpty()) {
            return;
        }

        UUID uuid = event.getPlayer().getUniqueId();
        if (this.rateLimit.contains(uuid)) {
            return;
        }
        this.rateLimit.add(uuid);
        Bukkit.getScheduler().runTaskLater(MessengerPigeons.getInstance(), () -> this.rateLimit.remove(uuid), 20);

        pigeon.popMail().open(event.getPlayer());

        if (pigeon.isMailEmpty()) {
            pigeon.setDeparting(true);
        }
    }
}

package org.lushplugins.messengerpigeons.listener;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.lushplugins.followers.Followers;
import org.lushplugins.followers.api.events.FollowersReloadEvent;
import org.lushplugins.followers.config.FollowerHandler;
import org.lushplugins.followers.utils.entity.EntityConfiguration;
import org.lushplugins.followers.utils.entity.LivingEntityConfiguration;
import org.lushplugins.messengerpigeons.MessengerPigeons;
import org.lushplugins.messengerpigeons.pigeon.Pigeon;

public class ReloadListener implements Listener {

    public ReloadListener() {
        registerFollowerType();

        // TODO: Adjust Followers so this doesn't need to exist
        Bukkit.getScheduler().runTaskLater(MessengerPigeons.getInstance(), this::registerFollowerType, 100);
    }

    @EventHandler
    public void onFollowersReload(FollowersReloadEvent event) {
        registerFollowerType();
    }

    private void registerFollowerType() {
        FollowerHandler.Builder handler = FollowerHandler.builder()
            .entityType(EntityTypes.PARROT);

        EntityConfiguration entityConfig = handler.entityConfig();
        if (entityConfig instanceof LivingEntityConfiguration livingEntityConfig) {
            livingEntityConfig.setScale(1.0);
        }

        Followers.getInstance().getFollowerManager().loadFollower(Pigeon.TYPE, handler.build());
    }
}

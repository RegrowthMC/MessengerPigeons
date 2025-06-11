package org.lushplugins.messengerpigeons;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3f;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.lushplugins.followers.api.FollowersAPI;
import org.lushplugins.followers.api.events.FollowerTickEvent;
import org.lushplugins.followers.entity.tasks.TaskId;
import org.lushplugins.followers.libraries.entitylib.meta.display.TextDisplayMeta;
import org.lushplugins.followers.libraries.entitylib.wrapper.WrapperEntity;
import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.lushmail.mail.Mail;
import org.lushplugins.messengerpigeons.listener.PacketListener;
import org.lushplugins.messengerpigeons.listener.PigeonListener;
import org.lushplugins.messengerpigeons.listener.MailListener;
import org.lushplugins.messengerpigeons.listener.ReloadListener;
import org.lushplugins.messengerpigeons.pigeon.Pigeon;
import org.lushplugins.messengerpigeons.pigeon.PigeonTask;

import java.util.*;

public final class MessengerPigeons extends SpigotPlugin {
    private static MessengerPigeons plugin;

    private Map<UUID, Pigeon> trackedPigeons;
    private List<Pigeon> departingPigeons;
    private BukkitTask heartbeat;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        this.trackedPigeons = new HashMap<>();
        this.departingPigeons = new ArrayList<>();

        FollowersAPI.getTaskRegistry().register(new PigeonTask(20, 50));

        registerListener(new MailListener());
        registerListener(new ReloadListener());
        registerListener(new PigeonListener());

        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener());

        this.heartbeat = Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Pigeon pigeon : this.trackedPigeons.values()) {
                this.callEvent(new FollowerTickEvent(pigeon));
            }

            for (Pigeon pigeon : new ArrayList<>(this.departingPigeons)) {
                this.callEvent(new FollowerTickEvent(pigeon));
            }
        }, 1, 1);
    }

    @Override
    public void onDisable() {
        if (this.heartbeat != null) {
            this.heartbeat.cancel();
            this.heartbeat = null;
        }
    }

    public Collection<Pigeon> getTrackedPigeons() {
        return this.trackedPigeons.values();
    }

    private Pigeon preparePigeon(Player receiver) {
        Pigeon pigeon = new Pigeon(receiver);
        pigeon.setDisplayName("Parcel Parrot");
        pigeon.spawn(receiver.getWorld(), SpigotConversionUtil.fromBukkitLocation(receiver.getEyeLocation().clone()
            .add(30, 20, 15)));
        pigeon.removeTask(TaskId.MOVE_NEAR);
        pigeon.addTask(PigeonTask.ID);

        WrapperEntity nameTagEntity = pigeon.getNameTagEntity();
        if (nameTagEntity != null && nameTagEntity.getEntityMeta() instanceof TextDisplayMeta nameTagMeta) {
            nameTagMeta.setTranslation(new Vector3f(0, 0.65f, 0));
        }

        return pigeon;
    }

    public void trackPigeonForMail(Player receiver, Mail mail) {
        Pigeon pigeon = this.trackedPigeons.computeIfAbsent(receiver.getUniqueId(), (key) -> preparePigeon(receiver));
        pigeon.addMail(mail);
    }

    public void markPigeonAsDeparting(UUID receiver) {
        Pigeon pigeon = this.trackedPigeons.remove(receiver);
        if (pigeon != null) {
            this.departingPigeons.add(pigeon);
        }
    }

    public void stopTrackingPigeon(Pigeon pigeon) {
        this.departingPigeons.remove(pigeon);
    }

    public static MessengerPigeons getInstance() {
        return plugin;
    }
}

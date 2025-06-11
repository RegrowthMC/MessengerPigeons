package org.lushplugins.messengerpigeons.pigeon;

import com.github.retrooper.packetevents.util.Vector3d;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.LivingEntity;
import org.lushplugins.followers.entity.Follower;
import org.lushplugins.followers.entity.tasks.MoveNearTask;
import org.lushplugins.followers.libraries.entitylib.wrapper.WrapperEntity;
import org.lushplugins.messengerpigeons.MessengerPigeons;

public class PigeonTask extends MoveNearTask {
    public static String ID = "lushmail:pigeon_task";

    /**
     * The duration that the pigeon will hover near the player for before flying away (in ticks)
     */
    private final int maximumHoverDuration;
    /**
     * The maximum lifetime of the pigeon (in ticks)
     */
    private final int maximumLifetime;

    public PigeonTask(int maximumHoverDuration, int maximumLifetime) {
        super(ID);
        this.maximumHoverDuration = maximumHoverDuration * 20;
        this.maximumLifetime = maximumLifetime * 20;
    }

    @Override
    public void tick(Follower follower) {
        if (!(follower instanceof Pigeon pigeon)) {
            follower.removeTask(this.getId());
            return;
        }

        int lifetime = pigeon.incrementAndGetLifetime();
        if (lifetime > this.maximumLifetime) {
            MessengerPigeons.getInstance().stopTrackingPigeon(pigeon);
            follower.removeTask(this.getId());
            follower.despawn();
            return;
        }

        int hoverDuration = pigeon.isHovering() ? pigeon.incrementAndGetHoverDuration() : pigeon.getHoverDuration();
        if (hoverDuration <= this.maximumHoverDuration && !pigeon.isDeparting()) {
            LivingEntity targetEntity = pigeon.getTargetEntity();
            follower.setTarget(targetEntity.getWorld(), SpigotConversionUtil.fromBukkitLocation(targetEntity.getEyeLocation()).getPosition());
        } else {
            if (!pigeon.isDeparting()) {
                pigeon.setDeparting(true);
            }
        }

        super.tick(follower);
    }

    @Override
    public Vector3d calculatePosition(Follower follower) {
        WrapperEntity entity = follower.getEntity();
        double speed = 0.2;

        // Calculates new location of entity based off of the distance to the player
        Vector3d position = entity.getLocation().getPosition();
        Vector3d target = follower.getTarget();
        if (target != null) {
            Vector3d difference = getDifference(position, follower.getTarget());
            Vector3d normalizedDifference = difference.normalize();

            double newY = normalizedDifference.getY() * speed;
            if (new Vector3d(difference.getX(), 0 , difference.getZ()).lengthSquared() < 6.25) {
                position = position.add(new Vector3d(0, newY, 0));

                if (follower instanceof Pigeon pigeon && !pigeon.isHovering()) {
                    pigeon.incrementAndGetHoverDuration();
                }
            } else {
                position = position.add(new Vector3d(
                    normalizedDifference.getX() * speed,
                    newY,
                    normalizedDifference.getZ() * speed
                ));
            }
        }

        position = position.add(0, calculateYOffset(entity), 0); // Adds y offset of entity (Bobbing animation)

        return position;
    }
}

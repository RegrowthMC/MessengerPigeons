package org.lushplugins.messengerpigeons.pigeon;

import com.github.retrooper.packetevents.util.Vector3d;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.LivingEntity;
import org.lushplugins.followers.entity.Follower;
import org.lushplugins.lushmail.mail.Mail;
import org.lushplugins.messengerpigeons.MessengerPigeons;

import java.util.ArrayDeque;
import java.util.Deque;

public class Pigeon extends Follower {
    public static String TYPE = "lushmail:pigeon";

    private final LivingEntity targetEntity;
    private final Deque<Mail> mails = new ArrayDeque<>();
    private int lifetime = 0;
    private int hoverDuration = 0;
    private boolean departing = false;

    public Pigeon(LivingEntity targetEntity) {
        super(TYPE);
        this.targetEntity = targetEntity;
        this.setTarget(targetEntity.getWorld(), SpigotConversionUtil.fromBukkitLocation(targetEntity.getEyeLocation()).getPosition());
    }

    public LivingEntity getTargetEntity() {
        return this.targetEntity;
    }

    public boolean isMailEmpty() {
        return this.mails.isEmpty();
    }

    public void addMail(Mail mail) {
        this.mails.add(mail);
    }

    public Mail popMail() {
        return this.mails.pop();
    }

    public int getLifetime() {
        return lifetime;
    }

    public int incrementAndGetLifetime() {
        return ++lifetime;
    }

    public boolean isHovering() {
        return hoverDuration > 0;
    }

    public int getHoverDuration() {
        return hoverDuration;
    }

    public int incrementAndGetHoverDuration() {
        return ++hoverDuration;
    }

    public boolean isDeparting() {
        return departing;
    }

    public void setDeparting(boolean departing) {
        this.departing = departing;

        LivingEntity targetEntity = this.getTargetEntity();
        Vector3d target = SpigotConversionUtil.fromBukkitLocation(targetEntity.getEyeLocation()).getPosition()
            .add(50, 30, 20);
        this.setTarget(targetEntity.getWorld(), target);

        MessengerPigeons.getInstance().markPigeonAsDeparting(targetEntity.getUniqueId());
    }
}

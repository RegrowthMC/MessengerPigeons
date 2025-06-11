package org.lushplugins.messengerpigeons.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.lushplugins.lushmail.api.event.MailSendEvent;
import org.lushplugins.messengerpigeons.MessengerPigeons;

public class MailListener implements Listener {

    @EventHandler
    public void onMailSend(MailSendEvent event) {
        Player receiver = event.getReceiver();
        if (receiver == null) {
            return;
        }

        MessengerPigeons.getInstance().trackPigeonForMail(receiver, event.getMail());
    }
}

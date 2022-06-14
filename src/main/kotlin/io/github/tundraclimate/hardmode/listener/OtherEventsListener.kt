package io.github.tundraclimate.hardmode.listener

import io.github.tundraclimate.finelib.addon.server.RegisterEvent
import org.bukkit.entity.Enderman
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityTeleportEvent

object OtherEventsListener: RegisterEvent {
    @EventHandler
    private fun onTeleportEntity(e: EntityTeleportEvent) {
        //テレポートを削除
        if (e.entity is Enderman) e.isCancelled = true
    }
}
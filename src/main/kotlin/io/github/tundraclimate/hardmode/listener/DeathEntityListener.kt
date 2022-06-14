package io.github.tundraclimate.hardmode.listener

import io.github.tundraclimate.finelib.addon.server.RegisterEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent

object DeathEntityListener: RegisterEvent {
    @EventHandler
    private fun onDeadEntity(e: EntityDeathEvent) {}
}
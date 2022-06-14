package io.github.tundraclimate.hardmode.listener

import io.github.tundraclimate.finelib.addon.server.RegisterEvent
import org.bukkit.entity.Enderman
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

object DamageEntityListener : RegisterEvent {
    @EventHandler
    private fun onDamageByEntity(e: EntityDamageByEntityEvent) {
        val target = e.entity
        val damaged = e.damager

        //プレイヤー被ダメージ時発火
        if (target is Player) {
            //PVPはダメージを増やす
            if (damaged is Player)
                e.damage += 1
            if (damaged.hasMetadata("ghost"))
                target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 40, 1))
        }

        //プレイヤー与ダメージ時発火
        if (damaged is Player) {
            if (target.hasMetadata("zombie_leader")) {
                val tloc = target.location
                target.world.spawnEntity(
                    tloc.add((-2..2).random().toDouble(), 0.5, (-2..2).random().toDouble()),
                    EntityType.ZOMBIE
                )
            }
        }
    }

    @EventHandler
    private fun onDamageByBlock(e: EntityDamageByBlockEvent) {
        //溶岩には溺れるように
        if (e.cause == EntityDamageEvent.DamageCause.LAVA && e.entity is Player) {
            e.entity.velocity = Vector(0.0, -0.4, 0.0)
        }
    }

    @EventHandler
    private fun onDamage(e: EntityDamageEvent) {
        //エンダーマンに水耐性を
        if (e.entity is Enderman) {
            if (e.cause == EntityDamageEvent.DamageCause.DROWNING) e.isCancelled = true
        }
    }
}
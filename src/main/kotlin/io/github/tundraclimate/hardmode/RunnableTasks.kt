package io.github.tundraclimate.hardmode

import io.github.tundraclimate.finelib.FineLib
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.Particle
import org.bukkit.entity.CaveSpider
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.Vector

object RunnableTasks {
    fun runAlwaysTask() {
        Bukkit.getScheduler().runTaskTimer(FineLib.getPlugin(), Runnable {
            //ここには軽い処理を入れよう！
            Bukkit.getWorlds().forEach { world ->
                if (world.difficulty != Difficulty.HARD) world.difficulty = Difficulty.HARD
                world.livingEntities.forEach { entity ->
                    if (entity.hasMetadata("assassin_creeper")) {
                        entity.location.let {
                            world.spawnParticle(Particle.SMOKE_NORMAL, it, 5, 0.0, 0.0, 0.0, 0.01)
                        }
                    }
                    if (entity.hasMetadata("jumper")) {
                        if (entity.isOnGround) entity.setMetadata(
                            "jumper.enable",
                            FixedMetadataValue(FineLib.getPlugin(), true)
                        )
                        else {
                            if (entity.hasMetadata("jumper.enable")) {
                                entity.removeMetadata("jumper.enable", FineLib.getPlugin())
                                entity.velocity = entity.location.direction.multiply(1F).add(Vector(0, 1, 0))
                            }
                        }
                    }
                    if (entity.hasMetadata("seeker")) {
                        if (entity is CaveSpider) {
                            entity.isInvisible = entity.target != null
                        }
                    }
                }
            }
        }, 0L, 0L)
    }
}
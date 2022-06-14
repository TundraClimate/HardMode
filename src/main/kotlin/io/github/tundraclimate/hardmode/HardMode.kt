package io.github.tundraclimate.hardmode

import io.github.tundraclimate.finelib.FineLib
import io.github.tundraclimate.hardmode.listener.CreatureSpawnListener
import io.github.tundraclimate.hardmode.listener.DamageEntityListener
import io.github.tundraclimate.hardmode.listener.DeathEntityListener
import io.github.tundraclimate.hardmode.listener.OtherEventsListener
import org.bukkit.plugin.java.JavaPlugin

class HardMode : JavaPlugin() {
    override fun onEnable() {
        FineLib.setPlugin(this)

        CreatureSpawnListener.register()
        DamageEntityListener.register()
        DeathEntityListener.register()
        OtherEventsListener.register()

        RunnableTasks.runAlwaysTask()
    }
}
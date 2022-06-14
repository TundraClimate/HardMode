package io.github.tundraclimate.hardmode.listener

import io.github.tundraclimate.finelib.FineLib
import io.github.tundraclimate.finelib.addon.server.PreItemStack
import io.github.tundraclimate.finelib.addon.server.RegisterEvent
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object CreatureSpawnListener : RegisterEvent {
    @EventHandler
    private fun onSpawnEntity(e: CreatureSpawnEvent) {
        val entity = e.entity


        //HPを2倍 攻撃力を1.2倍 探索範囲を+20
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.let { it.baseValue *= 2; entity.health = it.baseValue }
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.let { it.baseValue *= 2 }
        entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)?.let { it.baseValue += 20 }

        executeEntitySetting(entity)

        //抽選
        when ((1..100).random()) {
            in 1..5 -> {
                when (entity.type) {
                    EntityType.ZOMBIE -> entity.setUniqueEntityType(UniqueEntityType.HIGH_ZOMBIE)
                    EntityType.SKELETON -> entity.setUniqueEntityType(UniqueEntityType.FLOAT_SKELETON)
                    EntityType.CREEPER -> entity.setUniqueEntityType(UniqueEntityType.ASSASSIN_CREEPER)
                    EntityType.CAVE_SPIDER -> entity.setUniqueEntityType(UniqueEntityType.SEEKER)
                    else -> {}
                }
            }
            in 6..10 -> {
                when (entity.type) {
                    EntityType.ZOMBIE -> entity.setUniqueEntityType(UniqueEntityType.LITTLE_ZOMBIE)
                    EntityType.SKELETON -> entity.setUniqueEntityType(UniqueEntityType.GHOST)
                    else -> {}
                }
            }
            in 11..30 -> {
                when (entity.type) {
                    EntityType.SKELETON -> entity.setUniqueEntityType(UniqueEntityType.SKELETON_SOLDIER)
                    EntityType.ENDERMAN -> entity.setUniqueEntityType(UniqueEntityType.SWIMMER)
                    EntityType.SPIDER -> entity.setUniqueEntityType(UniqueEntityType.JUMPER)
                    else -> {}
                }
            }
            in 97..99 -> {
                when (entity.type) {
                    EntityType.ZOMBIE -> entity.setUniqueEntityType(UniqueEntityType.BOMBIE)
                    else -> {}
                }
            }
            100 -> {
                when (entity.type) {
                    EntityType.ZOMBIE -> entity.setUniqueEntityType(UniqueEntityType.ZOMBIE_LEADER)
                    else -> {}
                }
            }
        }
    }

    private fun executeEntitySetting(entity: LivingEntity) {
        val equip = entity.equipment
        //エンティティごとの設定
        when (entity) {
            is Zombie, is AbstractSkeleton -> {
                //移動速度1.1倍
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.let { it.baseValue *= 1.1 }
                //炎上対策
                equip?.helmet.let {
                    if (it?.type == Material.AIR) {
                        equip?.helmet = ItemStack(Material.IRON_HELMET)
                        equip?.helmetDropChance = 0.0F
                    }
                }
                //ここはウィザスケ
                if (entity is WitherSkeleton && (equip?.itemInMainHand == ItemStack(Material.STONE_SWORD))) {
                    equip.setItemInMainHand(ItemStack(Material.IRON_AXE))
                    equip.setWeaponDropChance()
                }
                //ここはPigZombie 常時敵対ができないので単純強化
                if (entity is PigZombie && (equip?.itemInMainHand == ItemStack(Material.GOLDEN_SWORD))) {
                    equip.setItemInMainHand(
                        PreItemStack(Material.DIAMOND_AXE).addEnchantment(
                            Enchantment.DAMAGE_ALL,
                            5
                        )
                    )
                    equip.setWeaponDropChance()
                }
            }
            is Creeper -> {
                entity.let {
                    //魔改造
                    it.isSilent = true
                    it.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.let { it.baseValue *= 3 }
                    it.maxFuseTicks /= 3
                    it.fuseTicks /= 3
                    it.explosionRadius += 1
                    if (it.world.isThundering) it.isPowered = true
                }
            }
            else -> {}
        }
    }

    private enum class UniqueEntityType(val cast: (LivingEntity) -> Unit) {
        HIGH_ZOMBIE({
            val eq = it.equipment
            if (it is Zombie) {
                it.setMetadata("high_zombie", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "High Zombie"
                it.isCustomNameVisible = false
                it.setAdult()
                eq?.run {
                    chestplate = ItemStack(Material.IRON_CHESTPLATE)
                    leggings = ItemStack(Material.IRON_LEGGINGS)
                    boots = ItemStack(Material.IRON_BOOTS)
                    setArmorsDropChance()
                }
            }
        }),
        LITTLE_ZOMBIE({
            val eq = it.equipment
            if (it is Zombie) {
                it.setMetadata("little_zombie", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "Little Zombie"
                it.isCustomNameVisible = false
                it.setBaby()
                it.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.let { it.baseValue *= 2 }
                eq?.run {
                    setItemInMainHand(PreItemStack(Material.IRON_SWORD).addEnchantment(Enchantment.DAMAGE_ALL, 1))
                    setWeaponDropChance()
                }
            }
        }),
        FLOAT_SKELETON({
            val eq = it.equipment
            if (it is Skeleton) {
                it.setMetadata("float_skeleton", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "Float Skeleton"
                it.isCustomNameVisible = false
                it.setGravity(false)
                it.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 5, (6..10).random()))
                eq?.run {
                    setItemInOffHand(ItemStack(Material.SHIELD))
                    setSubDropChance()
                }
            }
        }),
        SKELETON_SOLDIER({
            val eq = it.equipment
            if (it is Skeleton) {
                it.setMetadata("skeleton_soldier", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "Skeleton Soldier"
                it.isCustomNameVisible = false
                eq?.run {
                    setItemInMainHand(ItemStack(Material.IRON_SWORD).also { s ->
                        val meta = s.itemMeta as org.bukkit.inventory.meta.Damageable
                        meta.setDisplayName("§rSoldier Blade")
                        meta.damage = (230..249).random()
                        s.itemMeta = meta
                    })
                    itemInMainHandDropChance = 50.0F
                }
            }
        }),
        ZOMBIE_LEADER({
            val eq = it.equipment
            if (it is Zombie) {
                it.setMetadata("zombie_leader", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "Zombie Leader"
                it.isCustomNameVisible = false
                it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 60.0
                it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 200.0
                it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 10000.0
                it.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)?.baseValue = 222.0
                it.health = 60.0
                it.setAdult()
                eq?.run {
                    setItemInMainHand(ItemStack(Material.DIAMOND_AXE))
                    helmet = ItemStack(Material.DIAMOND_HELMET)
                    chestplate = ItemStack(Material.DIAMOND_CHESTPLATE)
                    leggings = ItemStack(Material.DIAMOND_LEGGINGS)
                    boots = ItemStack(Material.DIAMOND_BOOTS)
                    setArmorsDropChance()
                    setWeaponDropChance()
                }
            }
        }),
        ASSASSIN_CREEPER({
            if (it is Creeper) {
                it.setMetadata("assassin_creeper", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "Assassin Creeper"
                it.isCustomNameVisible = false
                it.isSilent = false
                it.isInvisible = true
            }
        }),
        BOMBIE({
            if (it is Zombie) {
                it.setMetadata("bombie", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "Bombie"
                it.isCustomNameVisible = false
                it.setAdult()
                it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 15.0
                it.health = 15.0
                val tnt = it.world.spawnEntity(it.location, EntityType.MINECART_TNT)
                tnt.customName = "Bombie Bomb"
                tnt.isCustomNameVisible = false
                tnt.isInvulnerable = true
                it.addPassenger(tnt)
            }
        }),
        SWIMMER({
            val eq = it.equipment
            if (it is Enderman) {
                it.setMetadata("swimmer", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "Swimmer"
                it.isCustomNameVisible = false
                eq?.run {
                    boots = PreItemStack(Material.IRON_BOOTS).addEnchantment(Enchantment.DEPTH_STRIDER, 5)
                    bootsDropChance = 0.0F
                }
            }
        }),
        GHOST({
            val eq = it.equipment
            if (it is Skeleton) {
                it.setMetadata("ghost", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "GHOST"
                it.isCustomNameVisible = false
                it.isInvisible = true
                eq?.run {
                    helmet = ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
                    chestplate = ItemStack(Material.IRON_CHESTPLATE)
                    leggings = ItemStack(Material.IRON_LEGGINGS)
                    setItemInMainHand(ItemStack(Material.AIR))
                    setArmorsDropChance()
                }
            }
        }),
        JUMPER({
            if (it is Spider) {
                it.setMetadata("jumper", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "Jumper"
                it.isCustomNameVisible = false
                it.equipment?.run {
                    boots = PreItemStack(Material.IRON_BOOTS).addEnchantment(Enchantment.PROTECTION_FALL, 5)
                    bootsDropChance = 0.0F
                }
            }
        }),
        SEEKER({
            if (it is CaveSpider) {
                it.setMetadata("seeker", FixedMetadataValue(FineLib.getPlugin(), true))
                it.customName = "Seeker"
                it.isCustomNameVisible = false
                it.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.let { it.baseValue *= 1.3 }
                it.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)?.let { it.baseValue *= 200 }
            }
        });

        //cast用
        val a = { it: LivingEntity ->

        }
    }

    private fun EntityEquipment.setArmorsDropChance() {
        //泥率を0に
        this.helmetDropChance = 0.0F
        this.chestplateDropChance = 0.0F
        this.leggingsDropChance = 0.0F
        this.bootsDropChance = 0.0F
    }

    private fun EntityEquipment.setWeaponDropChance() {
        this.itemInMainHandDropChance = 0.0F
    }

    private fun EntityEquipment.setSubDropChance() {
        this.itemInOffHandDropChance = 0.0F
    }

    private fun LivingEntity.setUniqueEntityType(unique: UniqueEntityType) {
        unique.cast(this)
    }
}
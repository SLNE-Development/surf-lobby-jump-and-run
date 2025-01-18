package dev.slne.surf.lobby.jar.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

/**
 * The type Item builder.
 */
@Suppress("unused")
class ItemBuilder {
    private val itemStack: ItemStack

    /**
     * Create a new ItemBuilder over an existing itemstack.
     *
     * @param itemStack The itemstack to create the ItemBuilder over.
     */
    constructor(itemStack: ItemStack) {
        this.itemStack = itemStack
    }

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param material The material of the item.
     * @param amount   The amount of the item.
     */
    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param material The material to create the ItemBuilder with.
     */
    @JvmOverloads
    constructor(material: Material, amount: Int = 1) {
        itemStack = ItemStack(material, amount)
    }

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param material   The material of the item.
     * @param amount     The amount of the item.
     * @param durability The durability of the item.
     */
    constructor(material: Material, amount: Int, durability: Byte) {
        itemStack = ItemStack(material, amount)

        itemStack.editMeta(
            Damageable::class.java
        ) { meta: Damageable ->
            meta.damage =
                durability.toInt()
        }
    }

    /**
     * Clone the ItemBuilder into a new one.
     *
     * @return The cloned instance.
     */
    override fun clone(): ItemBuilder {
        return ItemBuilder(itemStack)
    }

    /**
     * Change the durability of the item.
     *
     * @param durability The durability to set it to.
     * @return the durability
     */
    fun setDurability(durability: Short): ItemBuilder {
        itemStack.editMeta(
            Damageable::class.java
        ) { meta: Damageable ->
            meta.damage =
                durability.toInt()
        }

        return this
    }

    /**
     * Sets amount.
     *
     * @param amount the amount
     * @return the amount
     */
    fun setAmount(amount: Int): ItemBuilder {
        itemStack.amount = amount

        return this
    }

    /**
     * Sets custom model data.
     *
     * @param amount the amount
     * @return the custom model data
     */
    fun setCustomModelData(amount: Int): ItemBuilder {
        itemStack.editMeta { meta: ItemMeta ->
            meta.setCustomModelData(
                amount
            )
        }

        return this
    }

    /**
     * Add effect item builder.
     *
     * @param type      the type
     * @param duration  the duration
     * @param amplifier the amplifier
     * @return the item builder
     */
    fun addEffect(type: PotionEffectType, duration: Int, amplifier: Int): ItemBuilder {
        itemStack.editMeta(
            PotionMeta::class.java
        ) { meta: PotionMeta ->
            meta.addCustomEffect(
                PotionEffect(
                    type,
                    duration,
                    amplifier
                ), true
            )
        }

        return this
    }

    /**
     * Add data to persistence container item builder.
     *
     * @param <T>   the type parameter
     * @param <V>   the type parameter
     * @param key   the key
     * @param type  the type
     * @param value the value
     * @return the item builder
    </V></T> */
    fun <T, V> addDataToPersistenceContainer(
        key: NamespacedKey, type: PersistentDataType<T, V>,
        value: V
    ): ItemBuilder {
        itemStack.editMeta { meta: ItemMeta ->
            meta.persistentDataContainer.set(
                key,
                type,
                value
            )
        }

        return this
    }

    /**
     * Set the displayname of the item.
     *
     * @param name The name to change it to.
     * @return the name
     */
    fun setName(name: Component): ItemBuilder {
        itemStack.editMeta { meta: ItemMeta ->
            meta.displayName(
                name.decoration(
                    TextDecoration.ITALIC,
                    false
                )
            )
        }

        return this
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    fun setName(name: String): ItemBuilder {
        itemStack.editMeta { meta: ItemMeta ->
            meta.displayName(
                Component.text(
                    name
                ).decoration(TextDecoration.ITALIC, false)
            )
        }

        return this
    }

    /**
     * Add an unsafe enchantment.
     *
     * @param ench  The enchantment to add.
     * @param level The level to put the enchant on.
     * @return the item builder
     */
    fun addUnsafeEnchantment(ench: Enchantment, level: Int): ItemBuilder {
        itemStack.addUnsafeEnchantment(ench, level)

        return this
    }

    /**
     * Remove a certain enchant from the item.
     *
     * @param ench The enchantment to remove
     * @return the item builder
     */
    fun removeEnchantment(ench: Enchantment): ItemBuilder {
        itemStack.removeEnchantment(ench)

        return this
    }

    /**
     * Set the skull owner for the item. Works on skulls only.
     *
     * @param owner The name of the skull's owner.
     * @return the skull owner
     */
    fun setSkullOwner(owner: String?): ItemBuilder {
        itemStack.editMeta(
            SkullMeta::class.java
        ) { meta: SkullMeta -> meta.setOwner(owner) }

        return this
    }

    /**
     * Add an enchant to the item.
     *
     * @param enchantment The enchant to add
     * @param level       The level
     * @return the item builder
     */
    fun addEnchantment(enchantment: Enchantment, level: Int): ItemBuilder {
        itemStack.editMeta { meta: ItemMeta ->
            meta.addEnchant(
                enchantment,
                level,
                true
            )
        }

        return this
    }

    /**
     * Sets unbreakable.
     *
     * @param b the b
     * @return the unbreakable
     */
    fun setUnbreakable(b: Boolean): ItemBuilder {
        itemStack.editMeta { meta: ItemMeta -> meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE) }

        return this
    }

    /**
     * Add item flag item builder.
     *
     * @param flag the flag
     * @return the item builder
     */
    fun addItemFlag(flag: ItemFlag?): ItemBuilder {
        itemStack.editMeta { meta: ItemMeta ->
            meta.addItemFlags(
                flag!!
            )
        }

        return this
    }

    /**
     * Add multiple enchants at once.
     *
     * @param enchantments The enchants to add.
     * @return the item builder
     */
    fun addEnchantments(enchantments: Map<Enchantment?, Int?>): ItemBuilder {
        itemStack.addEnchantments(enchantments)

        return this
    }

    /**
     * Sets infinity durability on the item by setting the durability to Short.MAX_VALUE.
     *
     * @return the infinity durability
     */
    fun setInfinityDurability(): ItemBuilder {
        itemStack.editMeta(
            Damageable::class.java
        ) { meta: Damageable ->
            meta.damage =
                Short.MAX_VALUE.toInt()
        }

        return this
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     * @return the lore
     */
    fun setLore(vararg lore: Component): ItemBuilder {
        itemStack.editMeta { meta: ItemMeta ->
            meta.lore(
                Arrays.asList<Component>(
                    *Arrays.stream<Component>(
                        lore
                    )
                        .map<Component> { component: Component ->
                            component.decoration(
                                TextDecoration.ITALIC,
                                false
                            )
                        }.toArray<Component> { _Dummy_.__Array__() })
            )
        }

        return this
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     * @return the lore
     */
    fun setLore(lore: List<Component?>?): ItemBuilder {
        itemStack.editMeta { meta: ItemMeta -> meta.lore(lore) }

        return this
    }

    /**
     * Remove a lore line.
     *
     * @param line The line to remove.
     * @return the item builder
     */
    fun removeLoreLine(line: Component): ItemBuilder {
        val im = itemStack.itemMeta
        val lore: MutableList<Component> = ArrayList(im.lore())
        if (!lore.contains(line)) {
            return this
        }
        lore.remove(line)
        im.lore(lore)
        itemStack.setItemMeta(im)

        return this
    }

    /**
     * Remove a lore line.
     *
     * @param index The index of the lore line to remove.
     * @return the item builder
     */
    fun removeLoreLine(index: Int): ItemBuilder {
        val im = itemStack.itemMeta
        val lore: MutableList<Component> = ArrayList(im.lore())
        if (index < 0 || index > lore.size) {
            return this
        }
        lore.removeAt(index)
        im.lore(lore)
        itemStack.setItemMeta(im)
        return this
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     * @return the item builder
     */
    fun addLoreLine(line: Component?): ItemBuilder {
        val im = itemStack.itemMeta
        var lore: MutableList<Component?> = ArrayList()
        if (im.hasLore()) {
            lore = ArrayList(im.lore())
        }
        lore.add(line)
        im.lore(lore)
        itemStack.setItemMeta(im)
        return this
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     * @param pos  The index of where to put it.
     * @return the item builder
     */
    fun addLoreLine(line: Component, pos: Int): ItemBuilder {
        val im = itemStack.itemMeta
        val lore: MutableList<Component> = ArrayList(im.lore())
        lore[pos] = line
        im.lore(lore)
        itemStack.setItemMeta(im)
        return this
    }

    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
     *
     * @param color The color to set it to.
     * @return the leather armor color
     */
    fun setLeatherArmorColor(color: Color?): ItemBuilder {
        itemStack.editMeta(
            LeatherArmorMeta::class.java
        ) { meta: LeatherArmorMeta ->
            meta.setColor(color)
        }

        return this
    }

    /**
     * Retrieves the itemstack from the ItemBuilder.
     *
     * @return The itemstack created/modified by the ItemBuilder instance.
     */
    fun build(): ItemStack {
        return itemStack
    }
}
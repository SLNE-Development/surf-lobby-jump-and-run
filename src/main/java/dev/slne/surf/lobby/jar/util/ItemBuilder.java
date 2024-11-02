package dev.slne.surf.lobby.jar.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * The type Item builder.
 */
public class ItemBuilder {

  private final ItemStack itemStack;

  /**
   * Create a new ItemBuilder from scratch.
   *
   * @param material The material to create the ItemBuilder with.
   */
  public ItemBuilder(Material material) {
    this(material, 1);
  }

  /**
   * Create a new ItemBuilder over an existing itemstack.
   *
   * @param itemStack The itemstack to create the ItemBuilder over.
   */
  public ItemBuilder(ItemStack itemStack) {
    this.itemStack = itemStack;
  }

  /**
   * Create a new ItemBuilder from scratch.
   *
   * @param material The material of the item.
   * @param amount   The amount of the item.
   */
  public ItemBuilder(Material material, int amount) {
    itemStack = new ItemStack(material, amount);
  }

  /**
   * Create a new ItemBuilder from scratch.
   *
   * @param material   The material of the item.
   * @param amount     The amount of the item.
   * @param durability The durability of the item.
   */
  public ItemBuilder(Material material, int amount, byte durability) {
    itemStack = new ItemStack(material, amount);

    itemStack.editMeta(Damageable.class, meta -> meta.setDamage(durability));
  }

  /**
   * Clone the ItemBuilder into a new one.
   *
   * @return The cloned instance.
   */
  public ItemBuilder clone() {
    return new ItemBuilder(itemStack);
  }

  /**
   * Change the durability of the item.
   *
   * @param durability The durability to set it to.
   * @return the durability
   */
  public ItemBuilder setDurability(short durability) {
    itemStack.editMeta(Damageable.class, meta -> meta.setDamage(durability));

    return this;
  }

  /**
   * Sets amount.
   *
   * @param amount the amount
   * @return the amount
   */
  public ItemBuilder setAmount(int amount) {
    itemStack.setAmount(amount);

    return this;
  }

  /**
   * Sets custom model data.
   *
   * @param amount the amount
   * @return the custom model data
   */
  public ItemBuilder setCustomModelData(int amount) {
    itemStack.editMeta(meta -> {
      meta.setCustomModelData(amount);
    });

    return this;
  }

  /**
   * Add effect item builder.
   *
   * @param type      the type
   * @param duration  the duration
   * @param amplifier the amplifier
   * @return the item builder
   */
  public ItemBuilder addEffect(PotionEffectType type, int duration, int amplifier) {
    itemStack.editMeta(PotionMeta.class, meta -> {
      meta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
    });

    return this;
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
   */
  public <T, V> ItemBuilder addDataToPersistenceContainer(
      NamespacedKey key, PersistentDataType<T, V> type,
      V value
  ) {
    itemStack.editMeta(meta -> {
      meta.getPersistentDataContainer().set(key, type, value);
    });

    return this;
  }

  /**
   * Set the displayname of the item.
   *
   * @param name The name to change it to.
   * @return the name
   */
  public ItemBuilder setName(Component name) {
    itemStack.editMeta(meta -> {
      meta.displayName(name.decoration(TextDecoration.ITALIC, false));
    });

    return this;
  }

  /**
   * Sets name.
   *
   * @param name the name
   * @return the name
   */
  public ItemBuilder setName(String name) {
    itemStack.editMeta(meta -> {
      meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
    });

    return this;
  }

  /**
   * Add an unsafe enchantment.
   *
   * @param ench  The enchantment to add.
   * @param level The level to put the enchant on.
   * @return the item builder
   */
  public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
    itemStack.addUnsafeEnchantment(ench, level);

    return this;
  }

  /**
   * Remove a certain enchant from the item.
   *
   * @param ench The enchantment to remove
   * @return the item builder
   */
  public ItemBuilder removeEnchantment(Enchantment ench) {
    itemStack.removeEnchantment(ench);

    return this;
  }

  /**
   * Set the skull owner for the item. Works on skulls only.
   *
   * @param owner The name of the skull's owner.
   * @return the skull owner
   */
  public ItemBuilder setSkullOwner(String owner) {
    itemStack.editMeta(SkullMeta.class, meta -> {
      meta.setOwner(owner);
    });

    return this;
  }

  /**
   * Add an enchant to the item.
   *
   * @param enchantment The enchant to add
   * @param level       The level
   * @return the item builder
   */
  public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
    itemStack.editMeta(meta -> {
      meta.addEnchant(enchantment, level, true);
    });

    return this;
  }

  /**
   * Sets unbreakable.
   *
   * @param b the b
   * @return the unbreakable
   */
  public ItemBuilder setUnbreakable(boolean b) {
    itemStack.editMeta(meta -> {
      meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    });

    return this;
  }

  /**
   * Add item flag item builder.
   *
   * @param flag the flag
   * @return the item builder
   */
  public ItemBuilder addItemFlag(ItemFlag flag) {
    itemStack.editMeta(meta -> {
      meta.addItemFlags(flag);
    });

    return this;
  }

  /**
   * Add multiple enchants at once.
   *
   * @param enchantments The enchants to add.
   * @return the item builder
   */
  public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
    itemStack.addEnchantments(enchantments);

    return this;
  }

  /**
   * Sets infinity durability on the item by setting the durability to Short.MAX_VALUE.
   *
   * @return the infinity durability
   */
  public ItemBuilder setInfinityDurability() {
    itemStack.editMeta(Damageable.class, meta -> {
      meta.setDamage(Short.MAX_VALUE);
    });

    return this;
  }

  /**
   * Re-sets the lore.
   *
   * @param lore The lore to set it to.
   * @return the lore
   */
  public ItemBuilder setLore(Component... lore) {
    itemStack.editMeta(meta -> {
      meta.lore(Arrays.asList(Arrays.stream(lore).map(component -> component.decoration(
          TextDecoration.ITALIC,
          false
      )).toArray(Component[]::new)));
    });

    return this;
  }

  /**
   * Re-sets the lore.
   *
   * @param lore The lore to set it to.
   * @return the lore
   */
  public ItemBuilder setLore(List<Component> lore) {
    itemStack.editMeta(meta -> {
      meta.lore(lore);
    });

    return this;
  }

  /**
   * Remove a lore line.
   *
   * @param line The line to remove.
   * @return the item builder
   */
  public ItemBuilder removeLoreLine(Component line) {
    ItemMeta im = itemStack.getItemMeta();
    List<Component> lore = new ArrayList<>(im.lore());
    if (!lore.contains(line)) {
      return this;
    }
    lore.remove(line);
    im.lore(lore);
    itemStack.setItemMeta(im);

    return this;
  }

  /**
   * Remove a lore line.
   *
   * @param index The index of the lore line to remove.
   * @return the item builder
   */
  public ItemBuilder removeLoreLine(int index) {
    ItemMeta im = itemStack.getItemMeta();
    List<Component> lore = new ArrayList<>(im.lore());
    if (index < 0 || index > lore.size()) {
      return this;
    }
    lore.remove(index);
    im.lore(lore);
    itemStack.setItemMeta(im);
    return this;
  }

  /**
   * Add a lore line.
   *
   * @param line The lore line to add.
   * @return the item builder
   */
  public ItemBuilder addLoreLine(Component line) {
    ItemMeta im = itemStack.getItemMeta();
    List<Component> lore = new ArrayList<>();
    if (im.hasLore()) {
      lore = new ArrayList<>(im.lore());
    }
    lore.add(line);
    im.lore(lore);
    itemStack.setItemMeta(im);
    return this;
  }

  /**
   * Add a lore line.
   *
   * @param line The lore line to add.
   * @param pos  The index of where to put it.
   * @return the item builder
   */
  public ItemBuilder addLoreLine(Component line, int pos) {
    ItemMeta im = itemStack.getItemMeta();
    List<Component> lore = new ArrayList<>(im.lore());
    lore.set(pos, line);
    im.lore(lore);
    itemStack.setItemMeta(im);
    return this;
  }

  /**
   * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
   *
   * @param color The color to set it to.
   * @return the leather armor color
   */
  public ItemBuilder setLeatherArmorColor(Color color) {
    itemStack.editMeta(LeatherArmorMeta.class, meta -> {
      meta.setColor(color);
    });

    return this;
  }

  /**
   * Retrieves the itemstack from the ItemBuilder.
   *
   * @return The itemstack created/modified by the ItemBuilder instance.
   */
  public ItemStack build() {
    return itemStack;
  }
}
package net.avoit.test;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Item - Simple ItemBuilder class to create a {@link ItemStack} with one line of Code
 * @author alberteistein
 * @version 1.8
 */
public class Item {

	private static Class<?> craftMetaSkullClass;

	private ItemStack item;
	private final org.bukkit.inventory.meta.ItemMeta meta;
	private final ItemMeta itemMeta;

	static {
		try {
			craftMetaSkullClass = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".inventory.CraftMetaSkull");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes a new custom head Item
	 * @param skinID URL of the skin, example (only the blue part): http://textures.minecraft.net/<a style="color: #6666BB">7e1f5c0350100d55f95d173ae9b84882a5026c095d88cca5f9b8e893562a06cf</p>
	 */
	public Item(String skinID) {
		item = getSkull(skinID);
		meta = item.getItemMeta();
		itemMeta = ItemMeta.ItemMeta;
	}

	/**
	 * Initializes a new Item
	 * @param material Material of the item
	 */
	public Item(Material material) {
		itemMeta = ItemMeta.ItemMeta;
		item = new ItemStack(material);
		meta = item.getItemMeta();
	}

	/**
	 * Initializes a new Item
	 * @param material Material of the item
	 * @param subId SubId of the item
	 */
	public Item(Material material, short subId) {
		itemMeta = ItemMeta.ItemMeta;
		item = new ItemStack(material, 1, subId);
		meta = item.getItemMeta();
	}

	/**
	 * Initializes a new Item
	 * @param material Material/type of the item
	 * @param subId SubId of the item
	 * @param itemMeta This is necessary to use methods that are specialized for specific types of items
	 */
	public Item(Material material, short subId, ItemMeta itemMeta) {
		this.itemMeta = itemMeta;
		item = new ItemStack(material, 1, subId);
		meta = item.getItemMeta();
	}

	/**
	 * Set the display name of the item
	 * @param displayname Displayname of the item
	 * <p>| TIP: Use color codes with <code>§</code> or {@link ChatColor}<p>
	 */
	public Item setDisplayname(String displayname) {
		meta.setDisplayName(displayname);
		return this;
	}

	public Item setAmount(int amount) {
		item.setAmount(amount);
		return this;
	}

	@SuppressWarnings("deprecation")
	public Item setMaterialById(int typeId) {
		item.setTypeId(typeId);
		return this;
	}

	public Item setSubId(short subId) {
		item = new ItemStack(item.getType(), item.getAmount(), subId);
		return this;
	}

	/**
	 * Set the item unbreakable so item can no longer lose durability</small>
	 */
	public Item setUnbreakable() {
		return setUnbreakable(true);
	}

	/**
	 * Set the item unbreakable so item can no longer lose durability</small>
	 */
	private Item setUnbreakable(boolean unbreakable) {
		meta.spigot().setUnbreakable(unbreakable);
		return this;
	}

	/**
	 * Hides all ItemFlags
	 * <p><small>Use this to hide the enchantment tag in the lore for example</small>
	 */
	public Item hideFlags() {
		for(ItemFlag flags : ItemFlag.values())
			meta.addItemFlags(flags);
		return this;
	}

	/**
	 * Hides selected ItemFlags
	 * <p><small>Use this to hide the enchantment tag in the lore for example</small>
	 */
	public Item hideFlags(ItemFlag... itemFlag) {
		meta.removeItemFlags(itemFlag);
		return this;
	}

	/**
	 * Show selected ItemFlags
	 */
	public Item showFlags(ItemFlag... itemFlag) {
		meta.addItemFlags(itemFlag);
		return this;
	}

	/**
	 * Add an Enchantment to the item
	 * @param enchantment Enchantment, for example {@link Enchantment#DURABILITY}
	 */
	public Item addEnchantment(Enchantment enchantment) {
		return addEnchantment(Enchantment.DURABILITY, 1);
	}

	/**
	 * Add an Enchantment to the item
	 * @param enchantment Enchantment, for example {@link Enchantment#DURABILITY}
	 * @param level Level of the enchantment, for example <code>I</code>/<code>III</code>/<code>VI</code>
	 */
	public Item addEnchantment(Enchantment enchantment, int level) {
		meta.addEnchant(enchantment, level, true); // ignoreLevelRestriction = true
		return this;
	}

	/**
	 * Set the durability of the item
	 * @param durability Durability/damage <small>(0 = full, 255 = rip)</small>
	 */
	public Item setDurability(short durability) {
		item.setDurability(durability);
		return this;
	}

	public Item setMaterial(Material material) {
		item.setType(material);
		return this;
	}

	/**
	 * Set the lore/description of the item
	 * @param lore Lore of the item
	 * <p>| TIP: Use color codes with <code>§</code> or {@link ChatColor}<p>
	 */
	public Item setLore(String... lore) {
		meta.setLore(Arrays.asList(lore));
		return this;
	}

	/**
	 * Set the lore/description of the item
	 * @param lore Lore of the item
	 * <p>| TIP: Use color codes with <code>§</code> or {@link ChatColor}<p>
	 */
	public Item setLore(List<String> lore) {
		meta.setLore(lore);
		return this;
	}

	/**
	 * Applies the skin of a player to the skull
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#SkullMeta}!
	 * @param owner Player name of the skull owner
	 */
	public Item setOwner(String owner) {
		if(itemMeta == ItemMeta.SkullMeta && meta instanceof SkullMeta)
			((SkullMeta) meta).setOwner(owner);
		return this;
	}

	/**
	 * Set the banner base/default color
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BannerMeta}!
	 */
	public Item setBaseColor(DyeColor dyeColor) {
		if (itemMeta == ItemMeta.BannerMeta && meta instanceof BannerMeta)
			((BannerMeta) meta).setBaseColor(dyeColor);
		return this;
	}

	/**
	 * Set the banner patterns
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BannerMeta}!
	 */
	public Item setPatterns(Pattern... patternType) {
		if (itemMeta == ItemMeta.BannerMeta && meta instanceof BannerMeta)
			((BannerMeta) meta).setPatterns(Arrays.asList(patternType));
		return this;
	}

	/**
	 * Set the banner patterns
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BannerMeta}!
	 */
	public Item setPatterns(List<Pattern> patterns) {
		if (itemMeta == ItemMeta.BannerMeta && meta instanceof BannerMeta)
			((BannerMeta) meta).setPatterns(patterns);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#FireworkEffectMeta}!
	 */
	public Item setFireworkEffect(FireworkEffect fireworkEffect) {
		if (itemMeta == ItemMeta.FireworkEffectMeta && meta instanceof FireworkEffectMeta)
			((FireworkEffectMeta) meta).setEffect(fireworkEffect);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#FireworkMeta}!
	 */
	public Item setPower(int power) {
		if (itemMeta == ItemMeta.FireworkMeta && meta instanceof FireworkMeta)
			((FireworkMeta) meta).setPower(power);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#FireworkMeta}!
	 */
	public Item addEffect(FireworkEffect fireworkEffect) {
		if (itemMeta == ItemMeta.FireworkMeta && meta instanceof FireworkMeta)
			((FireworkMeta) meta).addEffect(fireworkEffect);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#FireworkMeta}!
	 */
	public Item removeEffect(int fireworkEffect) {
		if (itemMeta == ItemMeta.FireworkMeta && meta instanceof FireworkMeta)
			((FireworkMeta) meta).removeEffect(fireworkEffect);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#FireworkMeta}!
	 */
	public Item clearEffects() {
		if (itemMeta == ItemMeta.FireworkMeta && meta instanceof FireworkMeta)
			((FireworkMeta) meta).clearEffects();
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#LeatherArmorMeta}!
	 */
	public Item setColor(Color color) {
		if (itemMeta == ItemMeta.LeatherArmorMeta && meta instanceof LeatherArmorMeta)
			((LeatherArmorMeta) meta).setColor(color);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BlockStateMeta}!
	 */
	public Item setBlockState(BlockState blockState) {
		if (itemMeta == ItemMeta.BlockStateMeta && meta instanceof BlockStateMeta)
			((BlockStateMeta) meta).setBlockState(blockState);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BookMeta}!
	 */
	public Item setAuthor(String author) {
		if (itemMeta == ItemMeta.BookMeta && meta instanceof BookMeta)
			((BookMeta) meta).setAuthor(author);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BookMeta}!
	 */
	public Item setTitle(String title) {
		if (itemMeta == ItemMeta.BookMeta && meta instanceof BookMeta)
			((BookMeta) meta).setTitle(title);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BookMeta}!
	 */
	public Item addPage(String... Pages) {
		if (itemMeta == ItemMeta.BookMeta && meta instanceof BookMeta)
			((BookMeta) meta).addPage(Pages);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BookMeta}!
	 */
	public Item setPage(int pages, String page) {
		if (itemMeta == ItemMeta.BookMeta && meta instanceof BookMeta)
			((BookMeta) meta).setPage(pages, page);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BookMeta}!
	 */
	public Item setPages(String... page) {
		if (itemMeta == ItemMeta.BookMeta && meta instanceof BookMeta)
			((BookMeta) meta).setPages(page);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BookMeta}!
	 */
	public Item setPages(List<String> page) {
		if (itemMeta == ItemMeta.BookMeta && meta instanceof BookMeta)
			((BookMeta) meta).setPages(page);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#EnchantmentStorageMeta}!
	 */
	public Item addStoredEnchant(Enchantment ench, int level) {
		if (itemMeta == ItemMeta.EnchantmentStorageMeta && meta instanceof EnchantmentStorageMeta)
			((EnchantmentStorageMeta) meta).addStoredEnchant(ench, level, true);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#EnchantmentStorageMeta}!
	 */
	public Item removeStoredEnchant(Enchantment ench) {
		if (itemMeta == ItemMeta.EnchantmentStorageMeta && meta instanceof EnchantmentStorageMeta)
			((EnchantmentStorageMeta) meta).removeStoredEnchant(ench);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#MapMeta}!
	 */
	public Item setScaling(boolean scaling) {
		if (itemMeta == ItemMeta.MapMeta && meta instanceof MapMeta)
			((MapMeta) meta).setScaling(scaling);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#PotionMeta}!
	 */
	public Item setMainEffect(PotionEffectType potionEffectType) {
		if (itemMeta == ItemMeta.PotionMeta && meta instanceof PotionMeta)
			((PotionMeta) meta).setMainEffect(potionEffectType);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#PotionMeta}!
	 */
	public Item addCustomEffect(PotionEffect potionEffect, boolean overwrite) {
		if (itemMeta == ItemMeta.PotionMeta && meta instanceof PotionMeta)
			((PotionMeta) meta).addCustomEffect(potionEffect, overwrite);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#PotionMeta}!
	 */
	public Item removeCustomEffect(PotionEffectType potionEffectType) {
		if (itemMeta == ItemMeta.PotionMeta && meta instanceof PotionMeta)
			((PotionMeta) meta).removeCustomEffect(potionEffectType);
		return this;
	}

	/**
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#PotionMeta}!
	 */
	public Item clearCustomEffect() {
		if (itemMeta == ItemMeta.PotionMeta && meta instanceof PotionMeta)
			((PotionMeta) meta).clearCustomEffects();
		return this;
	}

	/**
	 * @return The built ItemStack
	 */
	public ItemStack build() {
		item.setItemMeta(meta);
		return item;
	}

	public enum ItemMeta {
		ItemMeta, SkullMeta, LeatherArmorMeta, BannerMeta, FireworkMeta, BlockStateMeta, BookMeta, EnchantmentStorageMeta, FireworkEffectMeta, MapMeta, PotionMeta
	}

	private ItemStack getSkull(String skinId) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		try {
			Field profileField = craftMetaSkullClass.getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(meta, getProfile(skinId));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		skull.setItemMeta(meta);
		return skull;
	}

	private GameProfile getProfile(String skinId) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		String base64encoded = Base64.getEncoder().encodeToString(("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/" + skinId + "\"}}}").getBytes());
		Property property = new Property("textures", base64encoded);
		profile.getProperties().put("textures", property);
		return profile;
	}

}

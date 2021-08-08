
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * A simple API class to create complicated {@link ItemStack}s with only one line of code.
 * @author alberteistein
 * @version 1.8
 */
public class Item {

	private static Class<?> craftMetaSkullClass;
	private final static int version;

	private ItemStack item;
	private final org.bukkit.inventory.meta.ItemMeta meta;
	private final ItemMeta itemMeta;

	static {
		String versionPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		version = Integer.parseInt(versionPackage.replaceAll("[^0-9]", ""));
		try {
			craftMetaSkullClass = Class.forName("org.bukkit.craftbukkit." + versionPackage + ".inventory.CraftMetaSkull");
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
	 */
	public Item(Material material, ItemMeta itemMeta) {
		this.itemMeta = itemMeta;
		item = new ItemStack(material);
		meta = item.getItemMeta();
	}

	/**
	 * Initializes a new Item
	 * @param material Material of the item
	 * @param subId SubId of the item (only for version 1.12.2 and below)
	 */
	public Item(Material material, short subId) {
		itemMeta = ItemMeta.ItemMeta;
		try {
			Constructor<?> itemStackConstructor = Class.forName("org.bukkit.inventory.ItemStack").getConstructor(Material.class, int.class, short.class);
			item = (ItemStack) itemStackConstructor.newInstance(material, 1, subId);
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
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
		try {
			Constructor<?> itemStackConstructor = Class.forName("org.bukkit.inventory.ItemStack").getConstructor(Material.class, int.class, short.class);
			item = (ItemStack) itemStackConstructor.newInstance(material, 1, subId);
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		meta = item.getItemMeta();
	}

	/**
	 * Set the display name of the item
	 * @param displayname Displayname of the item
	 * <p>| TIP: Use color codes with <code>§</code> or {@link org.bukkit.ChatColor}<p>
	 */
	public Item setDisplayname(String displayname) {
		meta.setDisplayName(displayname);
		return this;
	}

	public Item setAmount(int amount) {
		item.setAmount(amount);
		return this;
	}

	/**
	 * <code>Version: <= 1.12.2</code>
	 */
	public Item setMaterialById(int typeId) {
		if(version < 1130)
			try {
				Method setTypeIdMethode = item.getClass().getMethod("setTypeId", int.class);
				setTypeIdMethode.setAccessible(true);
				setTypeIdMethode.invoke(item, typeId);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		return this;
	}

	public Item setSubId(short subId) {
		try {
			Constructor<?> itemStackConstructor = Class.forName("org.bukkit.inventory.ItemStack").getConstructor(Material.class, int.class, short.class);
			item = (ItemStack) itemStackConstructor.newInstance(item.getType(), item.getAmount(), subId);
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
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
		try {
			if(version < 1110) {
				try {
					Class.forName("org.spigotmc.SpigotConfig");
				} catch (ClassNotFoundException ignored) {
					Bukkit.getConsoleSender().sendMessage("[SimpleItemBuilder] Unbreakable items only work for Spigot or Bukkit 1.11 and above!");
				}
				Method spigotMethode = meta.getClass().getMethod("spigot");
				spigotMethode.setAccessible(true);
				Object spigot = spigotMethode.invoke(meta);

				Method unbreakableMethode = spigot.getClass().getMethod("setUnbreakable", boolean.class);
				unbreakableMethode.setAccessible(true);
				unbreakableMethode.invoke(spigot, unbreakable);
			} else {
				Method unbreakableMethode = meta.getClass().getMethod("setUnbreakable", boolean.class);
				unbreakableMethode.setAccessible(true);
				unbreakableMethode.invoke(meta, unbreakable);
			}

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
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
	 * @param durability Durability/damage
	 */
	public Item setDurability(int durability) {
		try {
			if(version < 1130) {
				Method setOwnerMethode = item.getClass().getMethod("setDurability", short.class);
				setOwnerMethode.setAccessible(true);
				setOwnerMethode.invoke(item, (short) durability);
			} else {
				Method setOwningPlayerMethode = ((Damageable) meta).getClass().getMethod("setDamage", int.class);
				setOwningPlayerMethode.setAccessible(true);
				setOwningPlayerMethode.invoke(meta, durability);
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return this;
	}

	public Item setMaterial(Material material) {
		item.setType(material);
		return this;
	}

	/**
	 * Set the lore/description of the item
	 * @param lore Lore of the item
	 * <p>| TIP: Use color codes with <code>§</code> or {@link org.bukkit.ChatColor}<p>
	 */
	public Item setLore(String... lore) {
		meta.setLore(Arrays.asList(lore));
		return this;
	}

	/**
	 * Set the lore/description of the item
	 * @param lore Lore of the item
	 * <p>| TIP: Use color codes with <code>§</code> or {@link org.bukkit.ChatColor}<p>
	 */
	public Item setLore(List<String> lore) {
		meta.setLore(lore);
		return this;
	}

	/**
	 * Applies the skin of a player to the skull
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#SkullMeta}!
	 */
	public Item setOwner(UUID uuid) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		return setOwner(offlinePlayer);
	}
	/**
	 * Applies the skin of a player to the skull
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#SkullMeta}!
	 */
	public Item setOwner(OfflinePlayer offlinePlayer) {
		if(itemMeta == ItemMeta.SkullMeta && meta instanceof SkullMeta)
			if(offlinePlayer != null)
				try {
					if(version < 1120) {
						Method setOwnerMethode = ((SkullMeta) meta).getClass().getMethod("setOwner", String.class);
						setOwnerMethode.setAccessible(true);
						setOwnerMethode.invoke(meta, offlinePlayer.getName());
					} else {
						Method setOwningPlayerMethode = ((SkullMeta) meta).getClass().getMethod("setOwningPlayer", OfflinePlayer.class);
						setOwningPlayerMethode.setAccessible(true);
						setOwningPlayerMethode.invoke(meta, offlinePlayer);
					}
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
		return this;
	}

	/**
	 * Set the banner base/default color
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#BannerMeta}!
	 */
	public Item setBaseColor(DyeColor dyeColor) {
		if (itemMeta == ItemMeta.BannerMeta && meta instanceof BannerMeta)
			try {
				Method setBaseColorMethode = ((BannerMeta) meta).getClass().getMethod("setBaseColor", DyeColor.class);
				setBaseColorMethode.setAccessible(true);
				setBaseColorMethode.invoke(meta, dyeColor);
			} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				e.printStackTrace();
			}
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
	 * <code>Version: <= 1.8.9</code>
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#PotionMeta}!
	 */
	public Item setMainEffect(PotionEffectType potionEffectType) {
		if(version < 190)
			if (itemMeta == ItemMeta.PotionMeta && meta instanceof PotionMeta)
				try {
					Method setMainEffectMethode = ((PotionMeta) meta).getClass().getMethod("setMainEffect", PotionEffectType.class);
					setMainEffectMethode.setAccessible(true);
					setMainEffectMethode.invoke(meta, potionEffectType);
				} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
					e.printStackTrace();
				}
		return this;
	}

	/**
	 * <code>Version: >= 1.9.0</code>
	 * <p style="color: red;">The {@link ItemMeta} must have been set to {@link ItemMeta#PotionMeta}!
	 */
	public Item setBasePotionData(Object potionData) {
		if(version >= 190)
			if (itemMeta == ItemMeta.PotionMeta && meta instanceof PotionMeta)
				try {
					Method setBasePotionDataMethode = ((PotionMeta) meta).getClass().getMethod("setBasePotionData", Object.class);
					setBasePotionDataMethode.setAccessible(true);
					setBasePotionDataMethode.invoke(meta, potionData);
				} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
					e.printStackTrace();
				}
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
		ItemStack skull = null;
		if(version < 1130)
			try {
				Constructor<?> itemStackConstructor = Class.forName("org.bukkit.inventory.ItemStack").getConstructor(Material.class, int.class, short.class);
				skull = (ItemStack) itemStackConstructor.newInstance(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
			} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		else
			skull = new ItemStack(Material.valueOf("PLAYER_HEAD"), 1);
		if (skull != null) {
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			try {
				Field profileField = craftMetaSkullClass.getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(meta, getProfile(skinId));
			} catch (NoSuchFieldException | IllegalAccessException ex) {
				ex.printStackTrace();
			}
			skull.setItemMeta(meta);
		}
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

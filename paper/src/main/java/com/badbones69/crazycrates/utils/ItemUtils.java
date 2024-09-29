package com.badbones69.crazycrates.utils;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.common.utils.StringUtil;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.ryderbelserion.vital.paper.util.DyeUtil;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.ryderbelserion.vital.paper.util.ItemUtil.getEnchantment;

public class ItemUtils {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final CrateManager crateManager = plugin.getCrateManager();

    /**
     * Removes an {@link ItemStack} from a {@link Player}'s inventory.
     *
     * @param item the {@link ItemStack}
     * @param player the {@link Player}
     */
    public static void removeItem(@NotNull final ItemStack item, @NotNull final Player player) {
        try {
            if (item.getAmount() <= 1) {
                player.getInventory().removeItem(item);
            } else {
                item.setAmount(item.getAmount() - 1);
            }
        } catch (Exception ignored) {}
    }

    /**
     * Converts {@link org.bukkit.enchantments.Enchantment} to mojang mapped ids.
     *
     * @param enchant the {@link org.bukkit.enchantments.Enchantment} to convert
     * @return the mojang mapped id
     */
    public static String getEnchant(String enchant) {
        if (enchant.isEmpty()) return "";

        switch (enchant) {
            case "PROTECTION_ENVIRONMENTAL" -> {
                return "protection";
            }

            case "PROTECTION_FIRE" -> {
                return "fire_protection";
            }

            case "PROTECTION_FALL" -> {
                return "feather_falling";
            }

            case "PROTECTION_EXPLOSIONS" -> {
                return "blast_protection";
            }

            case "PROTECTION_PROJECTILE" -> {
                return "projectile_protection";
            }

            case "OXYGEN" -> {
                return "respiration";
            }

            case "WATER_WORKER" -> {
                return "aqua_affinity";
            }

            case "DAMAGE_ALL" -> {
                return "sharpness";
            }

            case "DAMAGE_UNDEAD" -> {
                return "smite";
            }

            case "DAMAGE_ARTHROPODS" -> {
                return "bane_of_arthropods";
            }

            case "LOOT_BONUS_MOBS" -> {
                return "looting";
            }

            case "SWEEPING_EDGE" -> {
                return "sweeping";
            }

            case "DIG_SPEED" -> {
                return "efficiency";
            }

            case "DURABILITY" -> {
                return "unbreaking";
            }

            case "LOOT_BONUS_BLOCKS" -> {
                return "fortune";
            }

            case "ARROW_DAMAGE" -> {
                return "power";
            }

            case "ARROW_KNOCKBACK" -> {
                return "punch";
            }

            case "ARROW_FIRE" -> {
                return "flame";
            }

            case "ARROW_INFINITE" -> {
                return "infinity";
            }

            case "LUCK" -> {
                return "luck_of_the_sea";
            }

            default -> {
                return enchant.toLowerCase();
            }
        }
    }

    /**
     * Converts {@link org.bukkit.potion.PotionEffectType} to mojang mapped ids.
     *
     * @param potion the {@link org.bukkit.potion.PotionEffectType} to convert
     * @return the mojang mapped id
     */
    public static String getPotion(String potion) {
        return potion.isEmpty() ? "" : potion.toLowerCase();
    }

    /**
     * Checks if the {@link ItemStack} is a {@link Crate}.
     *
     * @param itemStack the {@link ItemStack}
     * @param crate the {@link Crate}
     * @return true or false
     */
    public static boolean isSimilar(@NotNull final ItemStack itemStack, @NotNull final Crate crate) {
        return crateManager.isKeyFromCrate(itemStack, crate);
    }

    /**
     * @param container the {@link PersistentDataContainer}
     * @return the {@link String}
     */
    public static String getKey(@NotNull final PersistentDataContainerView container) {
        return container.get(Keys.crate_key.getNamespacedKey(), PersistentDataType.STRING);
    }

    /**
     * Updates the {@link ItemBuilder} from a {@link ConfigurationSection} with a {@link Player} attached.
     *
     * @param section the section in the {@link org.bukkit.configuration.file.YamlConfiguration}
     * @param builder the {@link ItemBuilder}
     * @param player the {@link Player}
     * @return the {@link ItemBuilder}
     */
    public static @NotNull ItemBuilder getItem(@NotNull final ConfigurationSection section, @NotNull final ItemBuilder builder, @NotNull final Player player) {
        return getItem(section, builder.setPlayer(player));
    }

    /**
     * Updates the {@link ItemBuilder} from a {@link ConfigurationSection}.
     *
     * @param section the section in the {@link org.bukkit.configuration.file.YamlConfiguration}
     * @param builder the {@link ItemBuilder}
     * @return the {@link ItemBuilder}
     */
    public static @NotNull ItemBuilder getItem(@NotNull final ConfigurationSection section, @NotNull final ItemBuilder builder) {
        builder.setGlowing(section.contains("Glowing") ? section.getBoolean("Glowing") : null);
        
        builder.setDamage(section.getInt("DisplayDamage", 0));
        
        builder.setDisplayLore(section.getStringList("Lore"));

        builder.addPatterns(section.getStringList("Patterns"));

        builder.setItemFlags(section.getStringList("Flags"));

        builder.setHidingItemFlags(section.getBoolean("HideItemFlags", false));

        builder.setUnbreakable(section.getBoolean("Unbreakable", false));
        
        if (section.contains("Skull") && plugin.getApi() != null) {
            builder.setSkull(section.getString("Skull", ""), plugin.getApi());
        }
        
        if (section.contains("Player") && builder.isPlayerHead()) {
            builder.setPlayer(section.getString("Player", ""));
        }

        builder.setCustomModelData(section.getInt("Custom-Model-Data", -1));
        
        if (section.contains("DisplayTrim.Pattern") && builder.isArmor()) {
            builder.applyTrimPattern(section.getString("DisplayTrim.Pattern", "sentry"));
        }
        
        if (section.contains("DisplayTrim.Material") && builder.isArmor()) {
            builder.applyTrimMaterial(section.getString("DisplayTrim.Material", "quartz"));
        }
        
        if (section.contains("DisplayEnchantments")) {
            for (String ench : section.getStringList("DisplayEnchantments")) {
                String[] value = ench.split(":");

                builder.addEnchantment(value[0], Integer.parseInt(value[1]), true);
            }
        }
        
        return builder;
    }

    /**
     * Converts an {@link ItemStack} to an {@link ItemBuilder}.
     *
     * @param player {@link Player}
     * @param itemStack the {@link ItemStack}
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertItemStack(Player player, ItemStack itemStack) {
        ItemBuilder itemBuilder = new ItemBuilder(itemStack.getType(), itemStack.getAmount());

        if (player != null) {
            itemBuilder.setPlayer(player);
        }

        return itemBuilder;
    }

    /**
     * Converts an {@link ItemStack} without a {@link Player}.
     *
     * @param itemStack the {@link ItemStack}
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertItemStack(ItemStack itemStack) {
        return convertItemStack(null, itemStack);
    }

    /**
     * Converts a {@link List<String>} to a list of {@link ItemBuilder}.
     *
     * @param itemStrings the {@link List<String>}
     * @return list of {@link ItemBuilder}
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings) {
        return convertStringList(itemStrings, null);
    }

    /**
     * Converts a {@link List<String>} to a list of {@link ItemBuilder}.
     *
     * @param itemStrings the {@link List<String>}
     * @param section the section in the {@link org.bukkit.configuration.file.YamlConfiguration}
     * @return list of {@link ItemBuilder}
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings, String section) {
        return itemStrings.stream().map(itemString -> convertString(itemString, section)).collect(Collectors.toList());
    }

    /**
     * Converts a {@link String} to an {@link ItemBuilder}.
     *
     * @param itemString the {@link String} you wish to convert
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertString(String itemString) {
        return convertString(itemString, null);
    }

    /**
     * Converts a {@link List<String>} to a list of {@link ItemBuilder}.
     *
     * @param itemString the {@link String} you wish to convert
     * @param section the section in the {@link org.bukkit.configuration.file.YamlConfiguration}
     * @return the {@link ItemBuilder}
     */
    public static ItemBuilder convertString(String itemString, String section) {
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder.withType(value.toLowerCase());
                    case "data" -> itemBuilder = itemBuilder.fromBase64(value);
                    case "name" -> itemBuilder.setDisplayName(value);
                    case "mob" -> {
                        final EntityType type = ItemUtil.getEntity(value);

                        if (type != null) {
                            itemBuilder.setEntityType(type);
                        }
                    }
                    case "glowing" -> itemBuilder.setGlowing(Boolean.valueOf(value));
                    case "amount" -> {
                        final Optional<Number> amount = StringUtil.tryParseInt(value);
                        itemBuilder.setAmount(amount.map(Number::intValue).orElse(1));
                    }
                    case "damage" -> {
                        final Optional<Number> amount = StringUtil.tryParseInt(value);
                        itemBuilder.setDamage(amount.map(Number::intValue).orElse(1));
                    }
                    case "lore" -> itemBuilder.setDisplayLore(List.of(value.split(",")));
                    case "player" -> itemBuilder.setPlayer(value);
                    case "skull" -> itemBuilder.setSkull(value, plugin.getApi());
                    case "custom-model-data" -> itemBuilder.setCustomModelData(StringUtil.tryParseInt(value).orElse(-1).intValue());
                    case "unbreakable-item" -> itemBuilder.setUnbreakable(value.isEmpty() || value.equalsIgnoreCase("true"));
                    case "trim-pattern" -> itemBuilder.applyTrimPattern(value);
                    case "trim-material" -> itemBuilder.applyTrimMaterial(value);
                    default -> {
                        if (getEnchantment(option.toLowerCase()) != null) {
                            final Optional<Number> amount = StringUtil.tryParseInt(value);

                            itemBuilder.addEnchantment(option.toLowerCase(), amount.map(Number::intValue).orElse(1), true);

                            break;
                        }

                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemFlag.name().equalsIgnoreCase(option)) {
                                itemBuilder.addItemFlag(itemFlag);

                                break;
                            }
                        }

                        try {
                            DyeColor color = DyeUtil.getDyeColor(value);

                            PatternType patternType = ItemUtil.getPatternType(option.toLowerCase());

                            if (patternType != null) {
                                itemBuilder.addPattern(patternType, color);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception exception) {
            itemBuilder.withType(Material.RED_TERRACOTTA).setDisplayName("<red>Error found!, Prize Name: " + section);

            if (MiscUtils.isLogging()) plugin.getComponentLogger().warn("An error has occurred with the item builder: ", exception);
        }

        return itemBuilder;
    }
}
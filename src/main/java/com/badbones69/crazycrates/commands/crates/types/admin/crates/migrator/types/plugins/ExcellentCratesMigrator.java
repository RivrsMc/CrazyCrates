package com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.plugins;

import com.badbones69.crazycrates.api.enums.Files;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.ICrateMigrator;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.ryderbelserion.vital.paper.files.config.CustomFile;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import su.nightexpress.excellentcrates.CratesAPI;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.key.CrateKey;
import su.nightexpress.nightcore.config.FileConfig;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExcellentCratesMigrator extends ICrateMigrator {

    public ExcellentCratesMigrator(final CommandSender sender) {
        super(sender, MigrationType.EXCELLENT_CRATES);
    }

    @Override
    public void run() {
        final File directory = getCratesDirectory();

        if (!directory.exists()) {
            directory.mkdirs();
        }

        final File crateDirectory = CratesAPI.PLUGIN.getDataFolder();

        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        FileConfiguration locationData = Files.locations.getConfiguration();

        for (final Crate crate : CratesAPI.getCrateManager().getCrates()) {
            final String crateName = crate.getFile().getName();

            final File crateFile = new File(directory, crateName);

            if (crateFile.exists()) {
                this.plugin.getComponentLogger().warn("Crate {} already exists in {}.", crateName, directory.getName());

                failed.add("<red>⤷ " + crateName);

                return;
            }

            try {
                crateFile.createNewFile();
            } catch (IOException exception) {
                this.plugin.getComponentLogger().warn("Failed to create crate file {} in {}.", crateName, directory.getName(), exception);

                failed.add("<red>⤷ " + crateName);
            }

            final CustomFile customFile = new CustomFile(directory).apply(crateName);

            final YamlConfiguration configuration = customFile.getConfiguration();

            set(configuration, "Crate.CrateType", "CSGO");

            final ConfigurationSection root = configuration.getConfigurationSection("Crate");

            if (root == null) return;

            final FileConfig crateConfig = crate.getConfig();

            final List<String> locations = crateConfig.getStringList("Block.Locations");

            if (!locations.isEmpty()) {
                crateConfig.getStringList("Block.Locations").forEach(location -> {
                    String id = "1"; // Location ID

                    for (int i = 1; locationData.contains("Locations." + i); i++) {
                        id = (i + 1) + "";
                    }

                    String[] splitter = location.split(",");

                    String arg5 = splitter[5];
                    String arg0 = splitter[0];
                    String arg1 = splitter[1];
                    String arg2 = splitter[2];

                    locationData.set("Locations." + id + ".Crate", crateName.replace(".yml", ""));
                    locationData.set("Locations." + id + ".World", arg5);
                    locationData.set("Locations." + id + ".X", (int) Double.parseDouble(arg0));
                    locationData.set("Locations." + id + ".Y", (int) Double.parseDouble(arg1));
                    locationData.set("Locations." + id + ".Z", (int) Double.parseDouble(arg2));

                    Files.locations.save();
                });
            }

            set(root, "Glowing", false);

            final String name = crate.getPreviewConfig();

            final File file = new File(new File(crateDirectory, "menu"), name == null ? "default.yml" : name + ".yml");

            if (file.exists()) {
                final YamlConfiguration menuFile = YamlConfiguration.loadConfiguration(file);

                final String previewName = menuFile.getString("Crate.Name", "<bold><#9af7ff>%crate%</bold>").replace("%crate_name%", "%crate%").replace("%crate%", crateName.replace(".yml", ""));

                final List<String> previewLore = new ArrayList<>();

                menuFile.getStringList("Crate.Lore").forEach(line -> previewLore.add(line.replaceAll("<l", "<").replaceAll("</l", "</")));

                set(root, "Name", previewName);
                set(root, "Lore", previewLore);

                final ConfigurationSection section = menuFile.getConfigurationSection("Crate.Slots");

                if (section != null) {
                    for (String key : section.getKeys(false)) {
                        final int slot = section.getInt(key);

                        if (key.equalsIgnoreCase(crateName.replace(".yml", ""))) {
                            set(root, "InGUI", slot > 0);
                            set(root, "Slot", slot);

                            break;
                        }
                    }
                }
            }

            set(root, "Preview.Toggle", true);
            set(root, "Preview.ChestLines", 6);
            set(root, "Preview.Glass.Toggle", true);
            set(root, "Preview.Glass.Name", " ");
            set(root, "Preview.Glass.Item", "gray_stained_glass_pane");
            set(root, "Preview.Glass.Custom-Model-Data", -1);

            set(root, "StartingKeys", 0);
            set(root, "RequiredKeys", 0);
            set(root, "Max-Mass-Open", 10);

            set(root, "OpeningBroadCast", false);
            set(root, "BroadCast", "%prefix%<bold><gold>%player%</bold><reset> <gray>is opening a <bold><green>%crate%.</bold>".replace("%crate%", crate.getName()));

            set(root, "opening-command.toggle", false);
            set(root, "opening-command.commands", List.of("put your command here."));

            set(root, "sound.cycle-sound.toggle", false);
            set(root, "sound.cycle-sound.value", "block.note_block_xylophone");
            set(root, "sound.cycle-sound.volume", 1.0);
            set(root, "sound.cycle-sound.pitch", 1.0);

            set(root, "sound.click-sound.toggle", false);
            set(root, "sound.click-sound.value", "block.note_block_xylophone");
            set(root, "sound.click-sound.volume", 1.0);
            set(root, "sound.click-sound.pitch", 1.0);

            set(root, "sound.stop-sound.toggle", false);
            set(root, "sound.stop-sound.value", "block.note_block_xylophone");
            set(root, "sound.stop-sound.volume", 1.0);
            set(root, "sound.stop-sound.pitch", 1.0);

            set(root, "Prize-Message", List.of("<gray>You have won <red>%reward% <gray>from <red>%crate%."));

            final ItemStack crateItem = crate.getItem();

            set(root, "Item", crateConfig.getString("Item.Material", "player_head").toLowerCase());

            final String itemName = crateConfig.getString("Item.Name", "%crate%").replace("%crate%", crateName);

            set(root, "Preview-Name", itemName + " Preview");

            if (crateItem.hasItemMeta()) {
                final ItemMeta itemMeta = crateItem.getItemMeta();

                set(root, "Custom-Model-Data", itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : -1);
            }

            set(root, "Settings.Knockback", crate.isPushbackEnabled());

            set(root, "Hologram.Toggle", crate.isHologramEnabled());
            set(root, "Hologram.Height", crate.getHologramYOffset());
            set(root, "Hologram.Range", 8);
            set(root, "Hologram.Update-Interval", -1);
            set(root, "Hologram.Color", "transparent");

            List<String> hologramText = new ArrayList<>();
            crate.getHologramText().forEach(line -> {
                final String filtered = line.replace(
                        "%excellentcrates_keys_" + crateName.replace(".yml", "") + "%",
                        "%crazycrates_" + crateName.replace(".yml", "") + "%"
                ).replace(
                        "%crate%",
                        crate.getName()
                );

                hologramText.add(filtered);
            });

            set(root, "Hologram.Message", hologramText);

            final Optional<CrateKey> value = crate.getKeys().stream().findFirst();

            if (value.isPresent()) {
                final CrateKey key = value.get();

                final FileConfig config = key.getConfig();

                final ItemStack itemStack = key.getItem();

                set(root, "PhysicalKey.Data", ItemUtil.toBase64(itemStack));

                set(root, "PhysicalKey.Name", key.getName());
                set(root, "PhysicalKey.Item", itemStack.getType().getKey().getKey());
                set(root, "PhysicalKey.Lore", List.of());

                set(root, "PhysicalKey.Glowing", config.contains("Item.Enchants"));
            }

            crate.getRewards().forEach(reward -> {
                // Get the id i.e. '1':
                final String id = reward.getId();

                set(root, "Prizes." + id + ".DisplayData", ItemUtil.toBase64(reward.getPreview()));

                set(root, "Prizes." + id + ".Commands", reward.getCommands());

                set(root, "Prizes." + id + ".Settings.Broadcast.Toggle", reward.isBroadcast());
                set(root, "Prizes." + id + ".Settings.Broadcast.Messages", List.of());
                set(root, "Prizes." + id + ".Settings.Broadcast.Permission", "your_permission");

                set(root, "Prizes." + id + ".BlackListed-Permissions", reward.getIgnoredForPermissions());

                set(root, "Prizes." + id + ".Chance", (int) reward.getWeight());

                final ConfigurationSection section = root.getConfigurationSection("Prizes");

                if (section == null) return;

                final ConfigurationSection prizes = section.getConfigurationSection(id);

                if (prizes == null) return;

                reward.getItems().forEach(itemStack -> {
                    final String base64 = ItemUtil.toBase64(itemStack);

                    if (prizes.contains("Items")) {
                        final List<String> items = prizes.getStringList("Items");

                        items.add("Data: " + base64);

                        set(prizes, "Items", items);
                    } else {
                        set(prizes, "Items", new ArrayList<>() {{
                            add("Data: " + base64);
                        }});
                    }
                });
            });

            customFile.save();

            this.fileManager.addCustomFile(customFile);

            success.add("<green>⤷ " + crateName);
        }

        // reload crates
        this.crateManager.loadHolograms();
        this.crateManager.loadCrates();

        final int convertedCrates = success.size();
        final int failedCrates = failed.size();

        sendMessage(new ArrayList<>(failedCrates + convertedCrates) {{
            addAll(failed);
            addAll(success);
        }}, convertedCrates, failedCrates);
    }

    @Override
    public <T> void set(final ConfigurationSection section, final String path, final T value) {
        section.set(path, value);
    }

    @Override
    public final File getCratesDirectory() {
        return new File(this.plugin.getDataFolder(), "crates");
    }
}
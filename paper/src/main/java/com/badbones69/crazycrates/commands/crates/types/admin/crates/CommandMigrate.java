package com.badbones69.crazycrates.commands.crates.types.admin.crates;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.utils.MiscUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.enums.MigrationType;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.MojangMappedMigratorMultiple;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.MojangMappedMigratorSingle;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.deprecation.DeprecatedCrateMigrator;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.deprecation.LegacyColorMigrator;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.deprecation.WeightMigrator;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.types.plugins.ExcellentCratesMigrator;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandMigrate extends BaseCommand {

    @Command("migrate")
    @Permission(value = "crazycrates.migrate", def = PermissionDefault.OP)
    public void migrate(final CommandSender sender, @ArgName("migration_type") @Suggestion("migrators") final String name, @ArgName("crate") @Optional @Suggestion("crates") final String crateName) {
        final MigrationType type = MigrationType.fromName(name);

        if (type == null) {
            Messages.migration_not_available.sendMessage(sender);

            return;
        }

        switch (type) {
            case MOJANG_MAPPED_ALL -> new MojangMappedMigratorMultiple(sender, type).run();
            case MOJANG_MAPPED_SINGLE -> {
                if (crateName == null || crateName.isEmpty() || crateName.isBlank() || crateName.equalsIgnoreCase("Menu")) {
                    Messages.cannot_be_empty.sendMessage(sender, "{value}", "crate name");

                    return;
                }

                new MojangMappedMigratorSingle(sender, type, crateName).run();
            }

            case WEIGHT_MIGRATION -> new WeightMigrator(sender, type).run();

            case LEGACY_COLOR_ALL -> new LegacyColorMigrator(sender, type).run();

            case CRATES_DEPRECATED_ALL -> new DeprecatedCrateMigrator(sender, type).run();

            case SPECIALIZED_CRATES -> sender.sendRichMessage(Messages.migration_not_available.getMessage(sender));

            case EXCELLENT_CRATES -> {
                if (!MiscUtils.isExcellentCratesEnabled()) {
                    sender.sendRichMessage(Messages.migration_plugin_not_enabled.getMessage(sender, "{name}", type.getName()));

                    return;
                }

                new ExcellentCratesMigrator(sender).run();
            }
        }
    }
}
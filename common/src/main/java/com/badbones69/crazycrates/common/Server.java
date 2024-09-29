package com.badbones69.crazycrates.common;

import com.badbones69.crazycrates.common.impl.Settings;
import com.ryderbelserion.vital.common.utils.FileUtil;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.ISettings;
import us.crazycrew.crazycrates.platform.IServer;
import com.badbones69.crazycrates.common.config.ConfigManager;
import java.io.File;
import java.util.List;

public class Server implements IServer {

    private final File directory;
    private final File crates;

    public Server(@NotNull final File directory) {
        this.directory = directory;
        this.crates = new File(this.directory, "crates");
    }

    private UserManager userManager;
    private Settings settings;

    public void apply() {
        ConfigManager.load(this.directory);

        this.settings = new Settings();

        CratesProvider.register(this);
    }

    public void setUserManager(@NotNull final UserManager userManager) {
        if (this.userManager != null) return;

        this.userManager = userManager;
    }

    public void disable() {
        CratesProvider.unregister();
    }

    @Override
    public void reload() {
        ConfigManager.refresh();
    }

    @Override
    public @NotNull final File getCrateFolder() {
        return this.crates;
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.directory;
    }

    @Override
    public @NotNull final List<String> getCrateFiles() {
        return FileUtil.getFiles(getCrateFolder(), ".yml", false);
    }

    @Override
    public @NotNull final UserManager getUserManager() {
        return this.userManager;
    }

    @Override
    public @NotNull final ISettings getSettings() {
        return this.settings;
    }
}
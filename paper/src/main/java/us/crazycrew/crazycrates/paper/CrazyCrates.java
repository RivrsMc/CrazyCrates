package us.crazycrew.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.paper.cratetypes.QuickCrate;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import us.crazycrew.crazycrates.paper.listeners.MiscListener;
import us.crazycrew.crazycrates.paper.api.support.libraries.PluginSupport;
import us.crazycrew.crazycrates.paper.utils.MsgUtils;

import java.util.List;

public class CrazyCrates extends JavaPlugin {

    private final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

    private CrazyHandler crazyHandler;
    private CrazyManager crazyManager;

    @Override
    public void onEnable() {
        // Load version 2 of crazycrates
        this.crazyHandler = new CrazyHandler(getDataFolder());
        this.crazyHandler.load();

        // Load crates temporarily here, This is leftovers from version 1.
        this.crazyManager = new CrazyManager();

        // Load deprecated version 1 to not break plugins that might use old api's
        new com.badbones69.crazycrates.paper.CrazyCrates().enable();

        // Register listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new MiscListener(), this);

        // Print dependency garbage
        for (PluginSupport value : PluginSupport.values()) {
            if (value.isPluginEnabled()) {
                getServer().getConsoleSender().sendMessage(MsgUtils.color(this.crazyHandler.getConfigManager().getPluginConfig().getProperty(PluginConfig.command_prefix) + "&6&l" + value.name() + " &a&lFOUND"));
            } else {
                getServer().getConsoleSender().sendMessage(MsgUtils.color(this.crazyHandler.getConfigManager().getPluginConfig().getProperty(PluginConfig.command_prefix) + "&6&l" + value.name() + " &c&lNOT FOUND"));
            }
        }
    }

    @Override
    public void onDisable() {
        // End all crates.
        SessionManager.endCrates();

        // Remove quick crate rewards
        QuickCrate.removeAllRewards();

        // Purge holograms.
        if (this.crazyHandler.getCrateManager().getHolograms() != null) this.crazyHandler.getCrateManager().getHolograms().removeAllHolograms();

        // Unload the plugin.
        this.crazyHandler.unload();
    }

    @NotNull
    public CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    //TODO() Migrate this to crazy handler
    @NotNull
    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    @NotNull
    public ConfigManager getConfigManager() {
        return getCrazyHandler().getConfigManager();
    }

    @NotNull
    public FileManager getFileManager() {
        return getCrazyHandler().getFileManager();
    }

    @NotNull
    public CrateManager getCrateManager() {
        return getCrazyHandler().getCrateManager();
    }

    public boolean isLogging() {
        return getConfigManager().getPluginConfig().getProperty(PluginConfig.verbose_logging);
    }
}
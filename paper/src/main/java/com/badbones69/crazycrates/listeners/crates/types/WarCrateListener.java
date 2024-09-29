package com.badbones69.crazycrates.listeners.crates.types;

import com.badbones69.crazycrates.tasks.menus.CratePrizeMenu;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.utils.MiscUtils;

public class WarCrateListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof CratePrizeMenu holder)) return;

        final Player player = holder.getPlayer();

        event.setCancelled(true);

        if (this.crateManager.isPicker(player) && this.crateManager.isInOpeningList(player)) {
            final Crate crate = this.crateManager.getOpeningCrate(player);

            if (crate.getCrateType() == CrateType.war && this.crateManager.isPicker(player)) {
                final ItemStack item = event.getCurrentItem();

                if (item != null && item.getType().toString().contains(Material.GLASS_PANE.toString())) {
                    final int slot = event.getRawSlot();

                    final Prize prize = crate.pickPrize(player);

                    inventory.setItem(slot, prize.getDisplayItem(player, crate));

                    if (this.crateManager.hasCrateTask(player)) this.crateManager.endCrate(player);

                    this.crateManager.removePicker(player);
                    this.crateManager.addCloser(player, true);

                    PrizeManager.givePrize(player, prize, crate);

                    if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

                    this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, prize));
                    this.crateManager.removePlayerFromOpeningList(player);

                    crate.playSound(player, player.getLocation(), "cycle-sound", "block.anvil.land", Sound.Source.PLAYER);

                    this.crateManager.addCrateTask(player, new FoliaRunnable(player.getScheduler(), null) {
                        @Override
                        public void run() {
                            for (int i = 0; i < 9; i++) {
                                if (i != slot) inventory.setItem(i, crate.pickPrize(player).getDisplayItem(player, crate));
                            }

                            if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                            // Removing other items then the prize.
                            crateManager.addCrateTask(player, new FoliaRunnable(player.getScheduler(), null) {
                                @Override
                                public void run() {
                                    for (int i = 0; i < 9; i++) {
                                        if (i != slot) inventory.setItem(i, null);
                                    }

                                    if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                                    // Closing the inventory when finished.
                                    crateManager.addCrateTask(player, new FoliaRunnable(player.getScheduler(), null) {
                                        @Override
                                        public void run() {
                                            if (crateManager.hasCrateTask(player)) crateManager.endCrate(player);

                                            crateManager.removePlayerFromOpeningList(player);

                                            player.closeInventory();
                                        }
                                    }.runDelayed(plugin, 30));
                                }
                            }.runDelayed(plugin, 30));
                        }
                    }.runDelayed(this.plugin,30));
                }
            }
        }
    }
}
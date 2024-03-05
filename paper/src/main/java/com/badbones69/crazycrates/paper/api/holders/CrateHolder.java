package com.badbones69.crazycrates.paper.api.holders;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CrateHolder implements InventoryHolder {
    private Inventory inventory;
    private final Crate crate;

    public CrateHolder(Inventory inventory, Crate crate) {
        this.inventory = inventory;
        this.crate = crate;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Crate getCrate() {
        return crate;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}

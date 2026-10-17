package com.zenkaiorecycle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/** The two things the loop mines, what they craft into, and which vault they go in. */
enum OreKind {
    // Vault chest slots in the 3-row Personal Vaults menu: first two chests are slots 10 and 11.
    GOLD("gold", Items.RAW_GOLD, Items.RAW_GOLD_BLOCK, "raw_gold_block", 11, "vault #2"),
    COAL("coal", Items.COAL, Items.COAL_BLOCK, "coal_block", 10, "vault #1");

    final String label;
    final Item ore;
    final Item block;
    final ResourceLocation recipe;
    final int vaultSlot;
    final String vaultTitle;

    OreKind(String label, Item ore, Item block, String recipeId, int vaultSlot, String vaultTitle) {
        this.label = label;
        this.ore = ore;
        this.block = block;
        this.recipe = new ResourceLocation(recipeId);
        this.vaultSlot = vaultSlot;
        this.vaultTitle = vaultTitle;
    }

    String mineCommand() {
        return this == GOLD ? Config.mineGold : Config.mineCoal;
    }

    OreKind other() {
        return this == GOLD ? COAL : GOLD;
    }
}

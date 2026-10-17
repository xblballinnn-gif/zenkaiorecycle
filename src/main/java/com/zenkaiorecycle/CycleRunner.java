package com.zenkaiorecycle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;

/** Mines until inventory is full of the current ore, crafts blocks, deposits them, and switches ore. */
final class CycleRunner {
    private enum State { IDLE, MINING, STOPPING, CRAFTING, DEPOSITING }
    private static final int STALL_TICKS = 20 * 120;
    private static State state = State.IDLE;
    private static OreKind kind = OreKind.GOLD;
    private static int ticks, junkTimer, lastOre, noProgress;

    private CycleRunner() {}
    static boolean running() { return state != State.IDLE; }

    static void toggle(Minecraft mc) {
        if (running()) { stop(mc, "Cycle stopped."); return; }
        if (!Inv.baritoneLooksInstalled()) {
            Inv.tell(mc, "Baritone was not detected, so nothing was sent. Install Baritone first.");
            return;
        }
        kind = OreKind.GOLD;
        beginMining(mc);
    }

    static void stop(Minecraft mc, String why) {
        state = State.IDLE;
        if (mc.player != null && mc.screen instanceof AbstractContainerScreen<?>) mc.player.closeContainer();
        if (Inv.baritoneLooksInstalled()) Inv.chat(mc, Config.stopCommand);
        Inv.tell(mc, why);
    }

    private static void beginMining(Minecraft mc) {
        state = State.MINING;
        ticks = junkTimer = noProgress = 0;
        lastOre = mc.player.getInventory().countItem(kind.ore);
        Inv.chat(mc, kind.mineCommand());
        Inv.tell(mc, "Mining " + kind.label + ".");
    }

    static void tick(Minecraft mc) {
        if (state == State.IDLE) return;
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null) { state = State.IDLE; return; }
        if (p.isDeadOrDying()) { stop(mc, "Cycle stopped: you died."); return; }
        ticks++;
        switch (state) {
            case MINING -> mining(mc, p);
            case STOPPING -> stopping(mc);
            case CRAFTING -> crafting(mc);
            case DEPOSITING -> depositing(mc, p);
            default -> { }
        }
    }

    private static void mining(Minecraft mc, LocalPlayer p) {
        if (mc.screen != null) return;
        if (++junkTimer >= 100) { junkTimer = 0; Inv.dropJunk(mc); }
        int ore = p.getInventory().countItem(kind.ore);
        noProgress = ore > lastOre ? 0 : noProgress + 1;
        lastOre = ore;
        if (noProgress >= STALL_TICKS) {
            noProgress = 0;
            Inv.chat(mc, Config.stopCommand);
            Inv.chat(mc, kind.mineCommand());
            Inv.tell(mc, "No new " + kind.label + " for 2 minutes, restarting Baritone's mining.");
        }
        if (Inv.fullOfOre(p, kind.ore)) {
            Inv.chat(mc, Config.stopCommand);
            Inv.tell(mc, "Inventory is full of " + kind.label + ". Crafting blocks.");
            state = State.STOPPING;
            ticks = 0;
        }
    }

    private static void stopping(Minecraft mc) {
        if (ticks >= 25) { CraftStep.start(mc, kind); state = State.CRAFTING; }
    }

    private static void crafting(Minecraft mc) {
        Status s = CraftStep.tick(mc);
        if (s == Status.FAILED) stop(mc, "Cycle stopped: " + CraftStep.failure);
        else if (s == Status.DONE) { VaultStep.start(kind); state = State.DEPOSITING; }
    }

    private static void depositing(Minecraft mc, LocalPlayer p) {
        Status s = VaultStep.tick(mc);
        if (s == Status.FAILED) stop(mc, "Cycle stopped: " + VaultStep.failure);
        else if (s == Status.DONE) {
            if (Inv.emptySlots(p) < 5) {
                stop(mc, "Cycle stopped: your inventory is still nearly full after storing everything. Something other than ore is in it.");
                return;
            }
            Inv.tell(mc, "Stored " + kind.label + " in " + kind.vaultTitle + ".");
            kind = kind.other();
            beginMining(mc);
        }
    }
}

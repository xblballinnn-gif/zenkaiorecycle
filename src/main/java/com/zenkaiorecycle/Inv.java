package com.zenkaiorecycle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import java.util.Set;

/** Helpers shared by the loop. Hotbar slots 1-3 (indices 0-2) are protected. */
final class Inv {
    static final int FIRST_FREE_INDEX = 3;
    private static final Set<Item> JUNK = Set.of(
            Items.COBBLESTONE, Items.COBBLED_DEEPSLATE, Items.STONE, Items.DEEPSLATE, Items.DIRT, Items.GRAVEL,
            Items.ANDESITE, Items.DIORITE, Items.GRANITE, Items.TUFF, Items.NETHERRACK, Items.SAND, Items.RED_SAND,
            Items.ROTTEN_FLESH, Items.BONE, Items.STRING, Items.SPIDER_EYE, Items.GUNPOWDER, Items.ARROW, Items.FLINT);

    private Inv() {}

    static int emptySlots(LocalPlayer p) {
        int n = 0;
        for (int i = FIRST_FREE_INDEX; i <= 35; i++) if (p.getInventory().items.get(i).isEmpty()) n++;
        return n;
    }

    static boolean fullOfOre(LocalPlayer p, Item ore) {
        if (emptySlots(p) > 0) return false;
        for (int i = FIRST_FREE_INDEX; i <= 35; i++) {
            ItemStack s = p.getInventory().items.get(i);
            if (s.is(ore) && s.getCount() < s.getMaxStackSize()) return false;
        }
        return true;
    }

    static int menuSlot(int index) {
        return index < 9 ? 36 + index : index;
    }

    static void dropJunk(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null || mc.gameMode == null || mc.screen != null) return;
        for (int i = FIRST_FREE_INDEX; i <= 35; i++) {
            ItemStack s = p.getInventory().items.get(i);
            if (!s.isEmpty() && JUNK.contains(s.getItem())) {
                mc.gameMode.handleInventoryMouseClick(p.inventoryMenu.containerId, menuSlot(i), 1, ClickType.THROW, p);
            }
        }
    }

    static void click(Minecraft mc, AbstractContainerMenu menu, int slot, int button, ClickType type) {
        mc.gameMode.handleInventoryMouseClick(menu.containerId, slot, button, type, mc.player);
    }

    static void aimAt(LocalPlayer p, Vec3 point) {
        Vec3 eye = p.getEyePosition();
        double dx = point.x - eye.x, dy = point.y - eye.y, dz = point.z - eye.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float) (-(Mth.atan2(dy, horizontal) * 180.0 / Math.PI));
        p.setXRot(pitch);
        p.xRotO = pitch;
        if (horizontal > 0.01) {
            float yaw = (float) (Mth.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
            p.setYRot(yaw);
            p.yRotO = yaw;
        }
    }

    static void chat(Minecraft mc, String line) {
        if (mc.player != null) mc.player.connection.sendChat(line);
    }

    static void command(Minecraft mc, String name) {
        if (mc.player != null) mc.player.connection.sendCommand(name);
    }

    static void tell(Minecraft mc, String message) {
        if (mc.player != null) mc.player.displayClientMessage(Component.literal("[Zenkai Ore Cycle] " + message), false);
    }

    static boolean baritoneLooksInstalled() {
        if (ModList.get().isLoaded("baritone")) return true;
        try {
            Class.forName("baritone.api.BaritoneAPI", false, Inv.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }
}

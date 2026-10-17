package com.zenkaiorecycle;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

/**
 * Key handling. Z starts/stops the gold-coal loop (see {@link CycleRunner}), X stops it and sends Baritone's #stop,
 * C drops unwanted main-inventory stacks. Keys are real key bindings, so they do not fire while chat or a menu is open.
 */
public final class CycleController {
    static final KeyMapping START_KEY = new KeyMapping(
            "key.zenkaiorecycle.start", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.categories.zenkaiorecycle");
    static final KeyMapping STOP_KEY = new KeyMapping(
            "key.zenkaiorecycle.stop", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, "key.categories.zenkaiorecycle");
    static final KeyMapping CLEAN_KEY = new KeyMapping(
            "key.zenkaiorecycle.clean", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.zenkaiorecycle");

    static {
        Config.load();
    }

    private CycleController() {}

    @Mod.EventBusSubscriber(modid = ZenkaiOreCycle.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
            event.register(START_KEY);
            event.register(STOP_KEY);
            event.register(CLEAN_KEY);
        }
    }

    @Mod.EventBusSubscriber(modid = ZenkaiOreCycle.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) tick(Minecraft.getInstance());
        }
    }

    private static void tick(Minecraft mc) {
        CycleRunner.tick(mc);
        if (mc.player == null || mc.level == null) return;
        while (START_KEY.consumeClick()) CycleRunner.toggle(mc);
        while (STOP_KEY.consumeClick()) CycleRunner.stop(mc, "Stopped.");
        while (CLEAN_KEY.consumeClick()) cleanInventory(mc);
    }

    private static void cleanInventory(Minecraft mc) {
        if (mc.player == null || mc.gameMode == null || mc.screen != null) return;
        int dropped = 0;
        for (int slot = 9; slot <= 35; slot++) {
            ItemStack stack = mc.player.getInventory().items.get(slot);
            if (!stack.isEmpty() && !allowed(stack.getItem())) {
                mc.gameMode.handleInventoryMouseClick(mc.player.inventoryMenu.containerId, slot, 1, ClickType.THROW, mc.player);
                dropped++;
            }
        }
        Inv.tell(mc, "Cleanup: dropped " + dropped + " stack(s). Hotbar untouched.");
    }

    private static boolean allowed(Item item) {
        String id = item.getDescriptionId().toLowerCase(Locale.ROOT);
        return item == Items.COAL || item == Items.COAL_ORE || item == Items.DEEPSLATE_COAL_ORE
                || item == Items.GOLD_INGOT || item == Items.RAW_GOLD
                || item == Items.GOLD_ORE || item == Items.DEEPSLATE_GOLD_ORE
                || item == Items.GOLD_BLOCK || item == Items.RAW_GOLD_BLOCK || item == Items.COAL_BLOCK
                || item == Items.CRAFTING_TABLE
                || item == Items.NETHERITE_PICKAXE || item == Items.DIAMOND_PICKAXE || item == Items.DIAMOND_AXE
                || id.contains("senzu");
    }
}

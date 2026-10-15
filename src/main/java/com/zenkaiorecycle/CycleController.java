package com.zenkaiorecycle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.ClickType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import java.util.Locale;

public final class CycleController {
 private static final Minecraft MC = Minecraft.getInstance();
 private static final int START_KEY=GLFW.GLFW_KEY_K, STOP_KEY=GLFW.GLFW_KEY_J, CLEAN_KEY=GLFW.GLFW_KEY_H;
 private static boolean active=false, goldNext=false, startWasDown=false, stopWasDown=false, cleanWasDown=false;
 private static long lastActionTick=0;
 private static final long ROTATE_TICKS=20L*60L*10L;

 @SubscribeEvent public void onClientTick(TickEvent.ClientTickEvent e) {
  if(e.phase!=TickEvent.Phase.END || MC.player==null || MC.level==null) return;
  long tick=MC.level.getGameTime();
  boolean s=down(START_KEY), x=down(STOP_KEY), c=down(CLEAN_KEY);
  if(s&&!startWasDown) start();
  if(x&&!stopWasDown) stop();
  if(c&&!cleanWasDown) cleanInventory();
  startWasDown=s; stopWasDown=x; cleanWasDown=c;
  if(active && tick-lastActionTick>=ROTATE_TICKS) {
   goldNext=!goldNext; mine(goldNext); lastActionTick=tick;
   tell("Target rotated to "+(goldNext?"gold":"coal")+". This is timer-based; verify storage manually.");
  }
 }
 private static boolean down(int key) { return MC.getWindow()!=null && GLFW.glfwGetKey(MC.getWindow().getWindow(),key)==GLFW.GLFW_PRESS; }
 private static void start() { active=true; lastActionTick=MC.level.getGameTime(); mine(goldNext); tell("Started. K=start, J=stop, H=drop unwanted main-inventory items."); }
 private static void stop() { active=false; chat("#stop"); tell("Cycle stopped."); }
 private static void mine(boolean gold) { chat(gold ? "#mine gold_ore deepslate_gold_ore" : "#mine coal_ore deepslate_coal_ore"); }
 private static void chat(String command) { LocalPlayer p=MC.player; if(p!=null) p.connection.sendChat(command); }
 private static void cleanInventory() {
  if(MC.player==null||MC.gameMode==null) return;
  // Player inventory menu slot ids 9-35 are main inventory. Hotbar is intentionally untouched.
  for(int slot=9;slot<=35;slot++) {
   ItemStack st=MC.player.getInventory().items.get(slot);
   if(!st.isEmpty()&&!allowed(st.getItem()))
    MC.gameMode.handleInventoryMouseClick(MC.player.inventoryMenu.containerId,slot,1,ClickType.THROW,MC.player);
  }
  tell("Cleanup attempted. Main inventory scanned; hotbar untouched to protect held items.");
 }
 private static boolean allowed(Item i) {
  String id=i.getDescriptionId().toLowerCase(Locale.ROOT);
  return i==Items.COAL || i==Items.COAL_ORE || i==Items.DEEPSLATE_COAL_ORE ||
   i==Items.GOLD_INGOT || i==Items.RAW_GOLD || i==Items.GOLD_ORE || i==Items.DEEPSLATE_GOLD_ORE ||
   i==Items.GOLD_BLOCK || i==Items.COAL_BLOCK || i==Items.CRAFTING_TABLE ||
   i==Items.NETHERITE_PICKAXE || i==Items.DIAMOND_PICKAXE || id.contains("senzu");
 }
 private static void tell(String m) { if(MC.player!=null) MC.player.displayClientMessage(Component.literal("[Zenkai Ore Cycle] "+m),false); }
}

package com.zenkaiorecycle;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
@Mod(ZenkaiOreCycle.MOD_ID)
public class ZenkaiOreCycle {
 public static final String MOD_ID = "zenkaiorecycle";
 public ZenkaiOreCycle() { MinecraftForge.EVENT_BUS.register(new CycleController()); }
}

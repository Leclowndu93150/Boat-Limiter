package com.leclowndu93150.boat_limiter;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Boat_limiter.MODID)
public class Boat_limiter {

    public static final String MODID = "boat_limiter";
    public Boat_limiter() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}

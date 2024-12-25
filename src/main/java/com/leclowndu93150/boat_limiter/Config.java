package com.leclowndu93150.boat_limiter;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Boat_limiter.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue DEFAULT_SPEED;
    public static final ForgeConfigSpec.DoubleValue ICE_SPEED;
    public static final ForgeConfigSpec.DoubleValue WATER_SPEED;
    public static final ForgeConfigSpec.DoubleValue LAND_SPEED;

    static {
        BUILDER.push("Boat Speed Configuration");

        DEFAULT_SPEED = BUILDER
                .comment("Default boat speed in blocks per second")
                .defineInRange("defaultSpeed", 0.72, 0.0, 200.0);

        ICE_SPEED = BUILDER
                .comment("Boat speed on ice in blocks per second")
                .defineInRange("iceSpeed", 0.72, 0.0, 200.0);

        WATER_SPEED = BUILDER
                .comment("Boat speed in water in blocks per second")
                .defineInRange("waterSpeed", 0.72, 0.0, 200.0);

        LAND_SPEED = BUILDER
                .comment("Boat speed on land in blocks per second")
                .defineInRange("landSpeed", 0.48, 0.0, 200.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
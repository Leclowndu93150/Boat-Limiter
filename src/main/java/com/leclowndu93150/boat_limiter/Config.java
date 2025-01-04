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
    public static final ForgeConfigSpec.DoubleValue JUMP_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue CRAWL_SPEED;
    public static final ForgeConfigSpec.DoubleValue MAX_CRAWL_HEIGHT;
    public static final ForgeConfigSpec.DoubleValue FALL_SPEED_MULTIPLIER;

    static {
        BUILDER.push("Boat Speed Configuration");

        DEFAULT_SPEED = BUILDER
                .comment("Default boat speed multiplier")
                .defineInRange("defaultSpeed", 5, 0.0, 200.0);

        ICE_SPEED = BUILDER
                .comment("Boat speed on ice multiplier")
                .defineInRange("iceSpeed", 5, 0.0, 200.0);

        WATER_SPEED = BUILDER
                .comment("Boat speed in water multiplier")
                .defineInRange("waterSpeed", 5, 0.0, 200.0);

        LAND_SPEED = BUILDER
                .comment("Boat speed on land in multiplier")
                .defineInRange("landSpeed", 5, 0.0, 200.0);

        JUMP_MULTIPLIER = BUILDER
                .comment("Boat jump multiplier")
                .defineInRange("jumpMultiplier", 1.0, 0.0, 128);

        CRAWL_SPEED = BUILDER
                .comment("Boat speed multiplier while crawling in blocks")
                .defineInRange("crawlSpeed", 1, 0.0, 200.0);

        MAX_CRAWL_HEIGHT = BUILDER
                .comment("Maximum height a boat can crawl")
                .defineInRange("maxCrawlHeight", 1, 0.0, 200.0);

        FALL_SPEED_MULTIPLIER = BUILDER
                .comment("Fall speed multiplier (lower = slower falling, 0.05 recommended for slow falls)")
                .defineInRange("fallSpeedMultiplier", 0.05, 0.0, 1.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
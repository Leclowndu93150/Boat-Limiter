package com.leclowndu93150.boat_limiter.mixin;

import com.leclowndu93150.boat_limiter.Config;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Boat.class)
public class BoatMixin {
    private static final float TICKS_PER_SECOND = 20.0F;

    @ModifyVariable(
            method = "controlBoat",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private float modifyBoatSpeed(float originalSpeed) {
        Boat boat = (Boat)(Object)this;
        Boat.Status status = boat.getStatus();

        float speedMultiplier = switch (status) {
            case IN_WATER -> Config.WATER_SPEED.get().floatValue() / TICKS_PER_SECOND;
            case ON_LAND -> Config.LAND_SPEED.get().floatValue() / TICKS_PER_SECOND;
            default -> Config.DEFAULT_SPEED.get().floatValue() / TICKS_PER_SECOND;
        };

        return originalSpeed * speedMultiplier;
    }
}

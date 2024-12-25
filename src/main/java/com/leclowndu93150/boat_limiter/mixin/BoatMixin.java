package com.leclowndu93150.boat_limiter.mixin;

import com.leclowndu93150.boat_limiter.BoatJumpAccessor;
import com.leclowndu93150.boat_limiter.Config;
import com.leclowndu93150.boat_limiter.network.NetworkHandler;
import com.leclowndu93150.boat_limiter.network.SyncBoatJumpPacket;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public class BoatMixin implements BoatJumpAccessor {
    @Unique
    private float boat_limiter$jumpPower;
    @Unique
    private boolean boat_limiter$isJumping;
    @Unique
    private int boat_limiter$jumpRechargeTicks;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Boat boat = (Boat)(Object)this;

        if (boat_limiter$jumpRechargeTicks > 0) {
            boat_limiter$jumpRechargeTicks--;
        }

        if (boat_limiter$isJumping && boat_limiter$jumpRechargeTicks == 0) {
            boat_limiter$jumpPower = Math.min(boat_limiter$jumpPower + 0.1f, 1.0f);
        }

        if (!boat.level().isClientSide && boat.getControllingPassenger() instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendToAllTracking(
                    new SyncBoatJumpPacket(
                            boat.getId(),
                            boat_limiter$jumpPower,
                            boat_limiter$isJumping,
                            boat_limiter$jumpRechargeTicks
                    ),
                    serverPlayer
            );
        }

        Vec3 motion = boat.getDeltaMovement();
        if (motion.horizontalDistance() > 0.01) {
            checkAndClimb(boat, motion);
        }
    }

    @Unique
    private void checkAndClimb(Boat boat, Vec3 motion) {
        Vec3 forward = Vec3.directionFromRotation(0, boat.getYRot());
        Vec3 pos = boat.position();
        Vec3 checkPos = pos.add(forward.x * 1.0, 0, forward.z * 1.0);

        BlockPos blockPos = BlockPos.containing(checkPos.x, pos.y, checkPos.z);
        BlockState frontBlock = boat.level().getBlockState(blockPos);
        BlockState aboveBlock = boat.level().getBlockState(blockPos.above());

        if (!frontBlock.isAir() && aboveBlock.isAir() && !frontBlock.canBeReplaced()) {
            Vec3 currentMotion = boat.getDeltaMovement();
            boat.setDeltaMovement(
                    currentMotion.x * Config.CRAWL_SPEED.get(),
                    Math.max(0.2, currentMotion.y),
                    currentMotion.z * Config.CRAWL_SPEED.get()
            );
        }
    }

    @ModifyVariable(
            method = "controlBoat",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private float modifyBoatSpeed(float originalSpeed) {
        Boat boat = (Boat)(Object)this;
        Boat.Status status = boat.getStatus();

        float speedMultiplier = switch (status) {
            case IN_WATER -> Config.WATER_SPEED.get().floatValue();
            case ON_LAND -> Config.LAND_SPEED.get().floatValue();
            default -> Config.DEFAULT_SPEED.get().floatValue();
        };

        return originalSpeed * speedMultiplier;
    }

    @Unique
    private void performJump(Boat boat) {
        if (boat_limiter$jumpPower > 0.1f) {
            float baseJumpForce = boat_limiter$jumpPower * boat_limiter$jumpPower * 0.5f;
            double jumpHeight = baseJumpForce * Config.JUMP_MULTIPLIER.get();
            boat.setDeltaMovement(boat.getDeltaMovement().add(0, jumpHeight, 0));
        }
        boat_limiter$jumpPower = 0.0f;
    }

    @Override
    public float getJumpPower() {
        return boat_limiter$jumpPower;
    }

    @Override
    public void setJumping(boolean jumping) {
        if (!jumping && boat_limiter$isJumping) {
            Boat boat = (Boat)(Object)this;
            if (boat.level().isClientSide) {
                performJump(boat);
                boat_limiter$jumpPower = 0.0f;
            }
        }
        boat_limiter$isJumping = jumping;
    }

    @Override
    public boolean isJumping() {
        return boat_limiter$isJumping;
    }

    @Override
    public void setJumpPower(float power) {
        boat_limiter$jumpPower = power;
    }

    @Override
    public int getJumpRechargeTicks() {
        return boat_limiter$jumpRechargeTicks;
    }

    @Override
    public void setJumpRechargeTicks(int ticks) {
        boat_limiter$jumpRechargeTicks = ticks;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveJumpData(CompoundTag tag, CallbackInfo ci) {
        tag.putFloat("JumpPower", boat_limiter$jumpPower);
        tag.putBoolean("IsJumping", boat_limiter$isJumping);
        tag.putInt("JumpRecharge", boat_limiter$jumpRechargeTicks);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadJumpData(CompoundTag tag, CallbackInfo ci) {
        boat_limiter$jumpPower = tag.getFloat("JumpPower");
        boat_limiter$isJumping = tag.getBoolean("IsJumping");
        boat_limiter$jumpRechargeTicks = tag.getInt("JumpRecharge");
    }
}
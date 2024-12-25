package com.leclowndu93150.boat_limiter.mixin;

import com.leclowndu93150.boat_limiter.BoatJumpAccessor;
import com.leclowndu93150.boat_limiter.Config;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(Boat.class)
public class BoatMixin implements BoatJumpAccessor {
    @Unique
    private static final float TICKS_PER_SECOND = 20.0F;
    @Unique
    private float boat_limiter$jumpPower;
    @Unique
    private boolean boat_limiter$isJumping;
    @Unique
    private int boat_limiter$jumpRechargeTicks;

    @Override
    public float getJumpPower() {
        return boat_limiter$jumpPower;
    }

    @Override
    public void setJumpPower(float power) {
        this.boat_limiter$jumpPower = power;
    }

    @Override
    public boolean isJumping() {
        return boat_limiter$isJumping;
    }

    @Override
    public void setJumping(boolean jumping) {
        if (this.boat_limiter$isJumping != jumping) {
            this.boat_limiter$isJumping = jumping;
            if (!jumping) {
                performJump((Boat)(Object)this);
                this.boat_limiter$jumpRechargeTicks = 20;
            }
        }
    }

    @Override
    public int getJumpRechargeTicks() {
        return boat_limiter$jumpRechargeTicks;
    }

    @Override
    public void setJumpRechargeTicks(int ticks) {
        this.boat_limiter$jumpRechargeTicks = ticks;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (boat_limiter$jumpRechargeTicks > 0) {
            boat_limiter$jumpRechargeTicks--;
        }

        if (boat_limiter$isJumping && boat_limiter$jumpRechargeTicks == 0) {
            boat_limiter$jumpPower = Math.min(boat_limiter$jumpPower + 0.1f, 1.0f);
        }

        Boat boat = (Boat)(Object)this;
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
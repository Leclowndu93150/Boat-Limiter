package com.leclowndu93150.boat_limiter.network;

import com.leclowndu93150.boat_limiter.BoatJumpAccessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class SyncBoatJumpPacket {
    private final int entityId;
    private final float jumpPower;
    private final boolean isJumping;
    private final int jumpRechargeTicks;

    public SyncBoatJumpPacket(int entityId, float jumpPower, boolean isJumping, int jumpRechargeTicks) {
        this.entityId = entityId;
        this.jumpPower = jumpPower;
        this.isJumping = isJumping;
        this.jumpRechargeTicks = jumpRechargeTicks;
    }

    public SyncBoatJumpPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.jumpPower = buf.readFloat();
        this.isJumping = buf.readBoolean();
        this.jumpRechargeTicks = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(jumpPower);
        buf.writeBoolean(isJumping);
        buf.writeInt(jumpRechargeTicks);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (net.minecraft.client.Minecraft.getInstance().level != null) {
                Entity entity = net.minecraft.client.Minecraft.getInstance().level.getEntity(entityId);
                if (entity instanceof BoatJumpAccessor accessor) {
                    accessor.setJumpPower(jumpPower);
                    accessor.setJumping(isJumping);
                    accessor.setJumpRechargeTicks(jumpRechargeTicks);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
package com.leclowndu93150.boat_limiter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BoatJumpPacket {
    private final int entityId;
    private final boolean isJumping;

    public BoatJumpPacket(int entityId, boolean isJumping) {
        this.entityId = entityId;
        this.isJumping = isJumping;
    }

    public static void encode(BoatJumpPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeBoolean(packet.isJumping);
    }

    public static BoatJumpPacket decode(FriendlyByteBuf buf) {
        return new BoatJumpPacket(buf.readInt(), buf.readBoolean());
    }

    public static void handle(BoatJumpPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ServerLevel level = player.serverLevel();
                Entity entity = level.getEntity(packet.entityId);

                if (entity instanceof BoatJumpAccessor boat) {
                    boat.setJumping(packet.isJumping);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
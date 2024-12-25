package com.leclowndu93150.boat_limiter.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.network.NetworkEvent;
import com.leclowndu93150.boat_limiter.BoatJumpAccessor;
import java.util.function.Supplier;

public class JumpKeyPacket {
    private final boolean jumping;

    public JumpKeyPacket(boolean jumping) {
        this.jumping = jumping;
    }

    public JumpKeyPacket(FriendlyByteBuf buf) {
        this.jumping = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(jumping);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.getVehicle() instanceof Boat boat) {
                BoatJumpAccessor accessor = (BoatJumpAccessor) boat;

                if (jumping && !accessor.isJumping() && accessor.getJumpRechargeTicks() == 0) {
                    accessor.setJumping(true);
                    accessor.setJumpPower(0.0f);
                } else if (!jumping && accessor.isJumping()) {
                    accessor.setJumping(false);
                    accessor.setJumpRechargeTicks(20);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
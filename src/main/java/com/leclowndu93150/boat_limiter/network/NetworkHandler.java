package com.leclowndu93150.boat_limiter.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("boat_limiter", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        INSTANCE.messageBuilder(JumpKeyPacket.class, id++)
                .encoder(JumpKeyPacket::encode)
                .decoder(JumpKeyPacket::new)
                .consumerMainThread(JumpKeyPacket::handle)
                .add();

        INSTANCE.messageBuilder(SyncBoatJumpPacket.class, id++)
                .encoder(SyncBoatJumpPacket::encode)
                .decoder(SyncBoatJumpPacket::new)
                .consumerMainThread(SyncBoatJumpPacket::handle)
                .add();
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void sendToAllTracking(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), packet);
    }
}
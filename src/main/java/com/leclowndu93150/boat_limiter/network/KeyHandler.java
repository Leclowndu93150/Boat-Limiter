package com.leclowndu93150.boat_limiter;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.leclowndu93150.boat_limiter.network.NetworkHandler;
import com.leclowndu93150.boat_limiter.network.JumpKeyPacket;

@Mod.EventBusSubscriber(modid = Boat_limiter.MODID, value = Dist.CLIENT)
public class KeyHandler {
    private static boolean wasJumping = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null && minecraft.player.getVehicle() instanceof Boat) {
                boolean jumping = minecraft.options.keyJump.isDown();
                if (jumping != wasJumping) {
                    NetworkHandler.sendToServer(new JumpKeyPacket(jumping));
                    wasJumping = jumping;
                }
            } else {
                wasJumping = false;
            }
        }
    }
}
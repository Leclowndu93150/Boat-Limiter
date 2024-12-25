package com.leclowndu93150.boat_limiter;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Boat_limiter.MODID,value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BoatClientEvents {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        if (minecraft.player.getVehicle() instanceof Boat boat) {
            boolean spacePressed = minecraft.options.keyJump.isDown();
            BoatJumpAccessor accessor = (BoatJumpAccessor) boat;

            if (spacePressed != accessor.isJumping() &&
                    accessor.getJumpRechargeTicks() == 0) {
                NetworkHandler.INSTANCE.sendToServer(
                        new BoatJumpPacket(boat.getId(), spacePressed)
                );
            }
        }
    }
}
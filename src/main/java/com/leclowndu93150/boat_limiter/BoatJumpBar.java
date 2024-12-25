package com.leclowndu93150.boat_limiter;

import com.leclowndu93150.boat_limiter.BoatJumpAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BoatJumpBar implements IGuiOverlay {
    private static final ResourceLocation JUMP_BAR = new ResourceLocation("textures/gui/icons.png");
    private boolean wasJumping = false;
    private boolean showPower = true;

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getVehicle() instanceof Boat boat) {
            BoatJumpAccessor accessor = (BoatJumpAccessor)boat;
            float jumpPower = accessor.getJumpPower();

            if (accessor.isJumping() && !wasJumping) {
                showPower = true;
            }

            if (!accessor.isJumping() && wasJumping) {
                showPower = false;
            }

            wasJumping = accessor.isJumping();

            if (!showPower) {
                jumpPower = 0.0f;
            }

            int x = width / 2 - 91;
            int y = height - 29;

            RenderSystem.setShaderTexture(0, JUMP_BAR);
            graphics.blit(JUMP_BAR, x, y, 0, 84, 182, 5);

            if (jumpPower > 0.0F) {
                graphics.blit(JUMP_BAR, x, y, 0, 89, (int)(jumpPower * 182), 5);
            }
        }
    }
}
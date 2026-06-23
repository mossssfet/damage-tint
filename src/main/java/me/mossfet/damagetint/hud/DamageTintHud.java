package me.mossfet.damagetint.hud;

import me.mossfet.damagetint.TintConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;

public class DamageTintHud {
    private static final Identifier VIGNETTE_LOCATION = Identifier.withDefaultNamespace("textures/misc/vignette.png");

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || minecraft.gameMode == null) {
            return;
        }

        if (!isGameTypeWithHearts(minecraft.gameMode.getPlayerMode())) {
            return;
        }

        float health = player.getHealth();
        float threshold = TintConfig.DYNAMIC.getAsBoolean() ? player.getMaxHealth() : TintConfig.THRESHOLD_HEALTH.get().floatValue();
        if (health <= threshold)
        {
            float intensity = (threshold - health) / threshold + 1.0F / threshold * 2.0F;
            intensity = Mth.clamp(intensity, 0.0F, 1.0F);
            int color = ARGB.colorFromFloat(
                    1.0F,
                    0.1F,
                    intensity,
                    intensity
            );
            graphics.blit(
                    RenderPipelines.VIGNETTE,
                    VIGNETTE_LOCATION,
                    0,
                    0,
                    0.0F,
                    0.0F,
                    graphics.guiWidth(),
                    graphics.guiHeight(),
                    graphics.guiWidth(),
                    graphics.guiHeight(),
                    color
            );
        }

    }

    private static boolean isGameTypeWithHearts(GameType type) {
        return type == GameType.SURVIVAL || type == GameType.ADVENTURE;
    }
}

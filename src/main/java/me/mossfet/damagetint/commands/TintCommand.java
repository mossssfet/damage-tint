package me.mossfet.damagetint.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.mossfet.damagetint.TintConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import static net.minecraft.commands.SharedSuggestionProvider.suggest;

public class TintCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("tint")
                .executes(TintCommand::status)
                .then(Commands.argument("health", FloatArgumentType.floatArg(0))
                        .suggests((c, b) -> suggest(new String[]{"0", "10", "20"}, b))
                        .executes(c -> changeThreshold(c, FloatArgumentType.getFloat(c, "health"))))
                .then(Commands.literal("reset")
                        .executes(TintCommand::reset))
                .then(Commands.literal("dynamic")
                        .executes(TintCommand::dynamicStatus)
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(c -> dynamic(c, BoolArgumentType.getBool(c, "value")))));
        dispatcher.register(command);
    }

    private static int status(CommandContext<CommandSourceStack> ctx) {
        boolean dynamic = TintConfig.DYNAMIC.getAsBoolean();

        Entity entity = ctx.getSource().getEntity();
        if (!(entity instanceof LocalPlayer)) {
            ctx.getSource().sendSuccess(() -> Component.literal("Only a Local player can use this command!").withStyle(ChatFormatting.GRAY), false);
            return 0;
        }
        float maxHealth = ((LocalPlayer) entity).getMaxHealth();
        float thresholdHealth = TintConfig.THRESHOLD_HEALTH.get().floatValue();
        ctx.getSource().sendSuccess( () -> Component.literal("Predefined health threshold = ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(thresholdHealth + " hp" + " (" + thresholdHealth / 2 + " hearts)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                .append(Component.literal(".").withStyle(ChatFormatting.GRAY)), false
        );
        if (dynamic)
            ctx.getSource().sendSuccess(() -> Component.literal("Current health threshold = ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(maxHealth + " hp" + " (" + maxHealth / 2 + " hearts)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                            .append(Component.literal(".").withStyle(ChatFormatting.GRAY)), false
            );

        ctx.getSource().sendSuccess(() -> dynamic ? Component.literal("Health threshold is updating dynamically! (Ignoring predefined value)").withStyle(ChatFormatting.GRAY) :
                        Component.literal("Health threshold is not updating dynamically! (Using predefined value)").withStyle(ChatFormatting.GRAY), false
        );
        return 0;
    }

    private static int changeThreshold(CommandContext<CommandSourceStack> ctx, float health) {
        TintConfig.DYNAMIC.set(false);
        TintConfig.DYNAMIC.save();
        TintConfig.THRESHOLD_HEALTH.set((double) health);
        TintConfig.THRESHOLD_HEALTH.save();

        ctx.getSource().sendSuccess(() -> Component.literal("Gradually tint screen at ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(health + " hp"+ " (" + health / 2 + " hearts)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                        .append(Component.literal(".").withStyle(ChatFormatting.GRAY)), false
        );
        return 0;
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) {
        TintConfig.THRESHOLD_HEALTH.set(20.0);
        TintConfig.THRESHOLD_HEALTH.save();
        TintConfig.DYNAMIC.set(true);
        TintConfig.DYNAMIC.save();
        ctx.getSource().sendSuccess(() -> Component.literal("Reset all configs to default.").withStyle(ChatFormatting.GRAY), false);
        return 0;
    }

    private static int dynamicStatus(CommandContext<CommandSourceStack> ctx) {
        boolean dynamic = TintConfig.DYNAMIC.getAsBoolean();
        if (dynamic) {
            ctx.getSource().sendSuccess(() -> Component.literal("Health threshold is updating dynamically! (Ignoring predefined value)").withStyle(ChatFormatting.GRAY), false);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("Health threshold is not updating dynamically! (Using predefined value)").withStyle(ChatFormatting.GRAY), false);
        }
        return 0;
    }

    private static int dynamic(CommandContext<CommandSourceStack> ctx, boolean dynamic) {
        TintConfig.DYNAMIC.set(dynamic);
        TintConfig.DYNAMIC.save();
        if (TintConfig.DYNAMIC.getAsBoolean()) {
            ctx.getSource().sendSuccess(() -> Component.literal("Health threshold will update dynamically!").withStyle(ChatFormatting.GRAY), false);
        } else {
            float health = TintConfig.THRESHOLD_HEALTH.get().floatValue();
            ctx.getSource().sendSuccess(() -> Component.literal("Health threshold will not update dynamically! Using predefined health threshold = ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(health + " hp"+ " (" + health / 2 + " hearts)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                            .append(Component.literal(".").withStyle(ChatFormatting.GRAY)), false
            );
        }
        return 0;
    }
}

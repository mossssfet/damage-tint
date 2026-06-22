package me.mossfet.damagetint.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.mossfet.damagetint.TintConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.SharedSuggestionProvider.suggest;

public class TintCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommands.literal("tint")
                .executes(TintCommand::status)
                .then(ClientCommands.argument("health", FloatArgumentType.floatArg(0))
                        .suggests((c, b) -> suggest(new String[]{"0", "10", "20"}, b))
                        .executes(c -> changeThreshold(c, FloatArgumentType.getFloat(c, "health"))))
                .then(ClientCommands.literal("reset")
                        .executes(TintCommand::reset))
                .then(ClientCommands.literal("dynamic")
                        .executes(TintCommand::dynamicStatus)
                        .then(ClientCommands.argument("value", BoolArgumentType.bool())
                                .executes(c -> dynamic(c, BoolArgumentType.getBool(c, "value")))));
        dispatcher.register(command);
    }

    private static int status(CommandContext<FabricClientCommandSource> ctx) {
        boolean dynamic = TintConfig.instance().isDynamic();
        float maxHealth = ctx.getSource().getPlayer().getMaxHealth();
        float thresholdHealth = TintConfig.instance().getHealth();
        ctx.getSource().sendFeedback(
                Component.literal("Predefined health threshold = ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(thresholdHealth + " hp" + " (" + thresholdHealth / 2 + " hearts)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                        .append(Component.literal(".").withStyle(ChatFormatting.GRAY))
        );
        if (dynamic)
            ctx.getSource().sendFeedback(
                    Component.literal("Current health threshold = ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(maxHealth + " hp" + " (" + maxHealth / 2 + " hearts)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                            .append(Component.literal(".").withStyle(ChatFormatting.GRAY))
            );

        ctx.getSource().sendFeedback(
                dynamic ? Component.literal("Health threshold is updating dynamically! (Ignoring predefined value)").withStyle(ChatFormatting.GRAY) :
                          Component.literal("Health threshold is not updating dynamically! (Using predefined value)").withStyle(ChatFormatting.GRAY)
        );
        return 0;
    }

    private static int changeThreshold(CommandContext<FabricClientCommandSource> ctx, float health) {
        TintConfig.instance().update(health, false);
        ctx.getSource().sendFeedback(
                Component.literal("Gradually tint screen at ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(health + " hp"+ " (" + health / 2 + " hearts)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                        .append(Component.literal(".").withStyle(ChatFormatting.GRAY))
        );
        return 0;
    }

    private static int reset(CommandContext<FabricClientCommandSource> ctx) {
        TintConfig.instance().update(20F, true);
        ctx.getSource().sendFeedback(Component.literal("Reset all configs to default.").withStyle(ChatFormatting.GRAY));
        return 0;
    }

    private static int dynamicStatus(CommandContext<FabricClientCommandSource> ctx) {
        boolean dynamic = TintConfig.instance().isDynamic();
        if (dynamic) {
            ctx.getSource().sendFeedback(Component.literal("Health threshold is updating dynamically! (Ignoring predefined value)").withStyle(ChatFormatting.GRAY));
        } else {
            ctx.getSource().sendFeedback(Component.literal("Health threshold is not updating dynamically! (Using predefined value)").withStyle(ChatFormatting.GRAY));
        }
        return 0;
    }

    private static int dynamic(CommandContext<FabricClientCommandSource> ctx, boolean dynamic) {
        TintConfig.instance().dynamic(dynamic);
        if (TintConfig.instance().isDynamic()) {
            ctx.getSource().sendFeedback(Component.literal("Health threshold will update dynamically!").withStyle(ChatFormatting.GRAY));
        } else {
            float health = TintConfig.instance().getHealth();
            ctx.getSource().sendFeedback(
                    Component.literal("Health threshold will not update dynamically! Using predefined health threshold = ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(health + " hp"+ " (" + health / 2 + " hearts)").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                            .append(Component.literal(".").withStyle(ChatFormatting.GRAY))
            );
        }
        return 0;
    }
}

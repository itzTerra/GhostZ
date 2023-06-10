package com.terra.ghostz.command;

import static net.minecraft.server.command.CommandManager.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.terra.ghostz.item.GhostLantern;
import com.terra.ghostz.util.GRegistry;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class GhostLanternCommand {
    private static final DynamicCommandExceptionType INVALID_ITEM = new DynamicCommandExceptionType((value) -> {
        return Text.translatable("command.ghostz.exception.invaliditem", value, GRegistry.GHOST_LANTERN.getDefaultStack());
    });

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        
        dispatcher.register(literal("ghostlantern").requires((permission) -> {
            return permission.hasPermissionLevel(2);
        })
        .then(literal("setlevel")
            .then(argument("player", EntityArgumentType.players())
                .then(argument("value", IntegerArgumentType.integer(1, GhostLantern.MAX_LEVEL)).executes((ctx) -> {
                    return setLevelCommand(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player"),
                            IntegerArgumentType.getInteger(ctx, "value"), ctx);
                }))))

        .then(literal("setxp")
            .then(argument("player", EntityArgumentType.players())
                .then(argument("value", IntegerArgumentType.integer(0)).executes((ctx) -> {
                    return setPointCommand(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player"),
                            IntegerArgumentType.getInteger(ctx, "value"), ctx);
        })))));
    }

    private static int setLevelCommand(ServerCommandSource source, ServerPlayerEntity player, int level, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ItemStack stack = player.getMainHandStack();

        if (!(stack.getItem() instanceof GhostLantern)) {
            throw INVALID_ITEM.create(stack);
        }

        GhostLantern.pingNBT(stack).putInt("level", level);
        GhostLantern.onLevelChange(stack);
        source.sendFeedback(Text.translatable("command.ghostz.setlevel", stack.getName(), level), true);

        return 1;
    }

    private static int setPointCommand(ServerCommandSource source, ServerPlayerEntity player, int xp, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ItemStack stack = player.getMainHandStack();

        if (!(stack.getItem() instanceof GhostLantern)) {
            throw INVALID_ITEM.create(stack);
        }

        int level = GhostLantern.pingNBT(stack).getInt("level");
        if(level >= GhostLantern.MAX_LEVEL) {
            throw INVALID_ITEM.create(stack);
        }

        stack.getOrCreateNbt().putInt("xp", xp);
        GhostLantern.onXpChange(stack);
        source.sendFeedback(Text.translatable("command.ghostz.setxp",stack.getName(), xp), true);

        return 1;
    }
}

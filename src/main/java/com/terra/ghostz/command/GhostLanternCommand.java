package com.terra.ghostz.command;

import static net.minecraft.server.command.CommandManager.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.terra.ghostz.GRegistry;
import com.terra.ghostz.GhostZ;
import com.terra.ghostz.util.GUtil;

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
                .then(argument("value", IntegerArgumentType.integer(1, GhostZ.CONFIG.getLevelCount())).executes((ctx) -> {
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

        if (!GUtil.isGhostLantern(stack)) {
            throw INVALID_ITEM.create(stack);
        }

        stack.getOrCreateNbt().putInt("level", level);
        source.sendFeedback(Text.translatable("command.ghostz.setlevel", stack.getName(), level), true);

        return 1;
    }

    private static int setPointCommand(ServerCommandSource source, ServerPlayerEntity player, int xp, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ItemStack stack = player.getMainHandStack();

        if (!GUtil.isGhostLantern(stack)) {
            throw INVALID_ITEM.create(stack);
        }

        int level = stack.getNbt().getInt("level");
        if(level >= GhostZ.CONFIG.getLevelCount()) {
            throw INVALID_ITEM.create(stack);
        }

        int maxXP = GhostZ.CONFIG.levels.get(level).get("xpnext");

        stack.getOrCreateNbt().putInt("xp", Math.min(xp, maxXP));
        source.sendFeedback(Text.translatable("command.ghostz.setxp",stack.getName(), xp), true);

        return 1;
    }
}

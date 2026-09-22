package com.altnoir.flyimpact;

import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class FlyimpactCommands {
    private static final int GRID_COLUMNS = 8;
    private static final double GRID_SPACING = 1.5;

    private FlyimpactCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("flyimpact")
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("spawnall")
                                .executes(FlyimpactCommands::spawnAll)));
    }

    private static int spawnAll(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        List<String> types = allFlyTypes();
        if (types.isEmpty()) {
            source.sendFailure(Component.translatable("commands.flyimpact.spawnall.failed"));
            return 0;
        }

        Vec3 origin = source.getPosition();
        int rows = (types.size() + GRID_COLUMNS - 1) / GRID_COLUMNS;
        int spawned = 0;
        for (int i = 0; i < types.size(); i++) {
            int col = i % GRID_COLUMNS;
            int row = i / GRID_COLUMNS;
            double x = origin.x + (col - (GRID_COLUMNS - 1) / 2.0) * GRID_SPACING;
            double z = origin.z + (row - (rows - 1) / 2.0) * GRID_SPACING;
            FlyEntity fly = FlyRelease.spawnTyped(level, x, origin.y, z, types.get(i));
            if (fly != null) {
                spawned++;
            }
        }
        if (spawned == 0) {
            source.sendFailure(Component.translatable("commands.flyimpact.spawnall.failed"));
            return 0;
        }
        int count = spawned;
        source.sendSuccess(() -> Component.translatable("commands.flyimpact.spawnall.success", count), true);
        return Command.SINGLE_SUCCESS;
    }

    private static List<String> allFlyTypes() {
        FlyTypeInjector.ensureExtraTypesRegistered();
        Set<String> types = new LinkedHashSet<>(FlyType.getAll().keySet());
        types.addAll(Flyimpact.EXTRA_FLY_TYPES);
        return new ArrayList<>(types);
    }
}

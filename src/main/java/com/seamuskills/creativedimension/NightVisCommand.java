package com.seamuskills.creativedimension;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;

public class NightVisCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context){
        dispatcher.register(Commands.literal("nightvis").executes(NightVisCommand::nightvis));
    }

    public static int nightvis(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (player.gameMode.isCreative()){
            if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                player.removeEffect(MobEffects.NIGHT_VISION);
                player.sendSystemMessage(Component.literal("Night vision disabled."));
            }else {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, false));
                player.sendSystemMessage(Component.literal("Night vision enabled."));
            }
        }else{
            context.getSource().sendFailure(Component.literal("You may only gain night vision in creative."));
        }
        return 0;
    }
}

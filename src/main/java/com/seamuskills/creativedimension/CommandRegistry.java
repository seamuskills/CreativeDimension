package com.seamuskills.creativedimension;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Creativedimension.MODID)
public class CommandRegistry {
    @SubscribeEvent
    public static void registerCommands(final RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();

        CreativeSwapCommand.register(dispatcher, context);
        NightVisCommand.register(dispatcher, context);
    }
}

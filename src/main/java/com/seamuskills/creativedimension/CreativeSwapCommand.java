package com.seamuskills.creativedimension;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.neoforged.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;

public class CreativeSwapCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context){
        dispatcher.register(Commands.literal("creative").executes(CreativeSwapCommand::toggleCreative));
    }

    private static int toggleCreative(CommandContext<CommandSourceStack> commandSourceStackCommandContext) throws CommandSyntaxException {
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayerOrException();
        boolean playerInDimension = player.serverLevel().dimension() == Creativedimension.CREATIVE_DIMENSION;
        var pos = player.position();
        BlockPos targetPos;

        ServerLevel target = player.server.getLevel(playerInDimension ? ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(player.getData(ModDataAttachments.SURVIVAL_DIMENSION))) : Creativedimension.CREATIVE_DIMENSION);

        if (playerInDimension){ //go from creative to survival
            player.setData(ModDataAttachments.CREATIVE_POS, player.blockPosition());

            targetPos = player.getData(ModDataAttachments.SURVIVAL_POS);

            CompoundTag cinvTag = new CompoundTag();
            cinvTag.put("Inventory", player.getInventory().save(new ListTag()));
            player.setData(ModDataAttachments.CREATIVE_INV, cinvTag);

            CompoundTag invTag = player.getData(ModDataAttachments.SURVIVAL_INV);
            player.getInventory().load(invTag.getList("Inventory", ListTag.TAG_COMPOUND));
            player.getEnderChestInventory().fromTag(invTag.getList("EnderItems", ListTag.TAG_COMPOUND), player.registryAccess());

            player.setHealth(player.getData(ModDataAttachments.SURVIVAL_HEALTH));

            ModDataAttachments.XPthing xp = player.getData(ModDataAttachments.SURVIVAL_XP);
            player.totalExperience = xp.xpTotal();
            player.experienceProgress = xp.xpProgress();
            player.experienceLevel = xp.level();

            player.getFoodData().readAdditionalSaveData(player.getData(ModDataAttachments.SURVIVAL_HUNGER));

            player.removeAllEffects();

            for (MobEffectInstance effect : player.getData(ModDataAttachments.SURVIVAL_EFFECTS)){
                player.addEffect(effect);
            }

            player.setGameMode(GameType.SURVIVAL);
        }else{ //from survival to creative
            player.setData(ModDataAttachments.SURVIVAL_POS, player.blockPosition());
            player.setData(ModDataAttachments.SURVIVAL_DIMENSION, player.serverLevel().dimension().location().toString());

            CompoundTag invTag = new CompoundTag();
            invTag.put("Inventory", player.getInventory().save(new ListTag()));
            invTag.put("EnderItems", player.getEnderChestInventory().createTag(player.registryAccess()));
            player.setData(ModDataAttachments.SURVIVAL_INV, invTag);

            player.setData(ModDataAttachments.SURVIVAL_HEALTH, player.getHealth());

            player.setData(ModDataAttachments.SURVIVAL_XP, new ModDataAttachments.XPthing(
                    player.totalExperience,
                    player.experienceProgress,
                    player.experienceLevel
            ));

            CompoundTag fooddata = new CompoundTag();
            player.getFoodData().addAdditionalSaveData(fooddata);
            player.setData(ModDataAttachments.SURVIVAL_HUNGER, fooddata);

            if (!player.getActiveEffects().isEmpty()) {
                ArrayList<MobEffectInstance> effects = new ArrayList<>(player.getActiveEffects());

                player.setData(ModDataAttachments.SURVIVAL_EFFECTS, effects);
            }

            player.removeAllEffects();

            CompoundTag cinvTag = player.getData(ModDataAttachments.CREATIVE_INV);
            player.getInventory().load(cinvTag.getList("Inventory", ListTag.TAG_COMPOUND));

            if (player.hasData(ModDataAttachments.CREATIVE_POS)){
                targetPos = player.getData(ModDataAttachments.CREATIVE_POS);
            }else{
                targetPos = new BlockPos(0, 1, 0);
            }

            player.setGameMode(GameType.CREATIVE);
        }

        player.fallDistance = 0;


        player.teleportTo(target, targetPos.getX(), targetPos.getY(), targetPos.getZ(), Collections.EMPTY_SET, player.getYRot(), player.getXRot());
        return 0;
    }
}

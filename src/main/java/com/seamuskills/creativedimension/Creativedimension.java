package com.seamuskills.creativedimension;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import org.slf4j.Logger;

import static com.seamuskills.creativedimension.ModDataAttachments.ATTACHMENT_TYPES;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Creativedimension.MODID)
public class Creativedimension {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "creativedimension";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceKey<Level> CREATIVE_DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MODID, "creative"));
    public static final ResourceKey<DimensionType> CREATIVE_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "creative"));

    public Creativedimension(IEventBus bus, ModContainer container) {

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.addListener(Creativedimension::onCheckSpawn);
        NeoForge.EVENT_BUS.addListener(Creativedimension::onSetSpawn);
        NeoForge.EVENT_BUS.addListener(Creativedimension::onClone);
        NeoForge.EVENT_BUS.addListener(Creativedimension::onPlayerDeath);
        NeoForge.EVENT_BUS.addListener(Creativedimension::onDimensionChange);

        ATTACHMENT_TYPES.register(bus);
    }

    public static void onCheckSpawn(MobSpawnEvent.SpawnPlacementCheck event){
        LevelAccessor level = event.getLevel();
        if (level instanceof ServerLevel) {
            if (((ServerLevel)level).dimension() == CREATIVE_DIMENSION) {
                event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
            }
        }
    }

    public static void onSetSpawn(PlayerSetSpawnEvent event){
        if (event.getSpawnLevel() == CREATIVE_DIMENSION){
            event.setCanceled(true);
        }
    }

    public static void onClone(PlayerEvent.Clone event){
        if (event.isWasDeath()){
            var newPlayer = event.getEntity();
            var ogPlayer = event.getOriginal();

            newPlayer.setData(ModDataAttachments.CREATIVE_POS, ogPlayer.getData(ModDataAttachments.CREATIVE_POS));
            newPlayer.setData(ModDataAttachments.CREATIVE_INV, ogPlayer.getData(ModDataAttachments.CREATIVE_INV));
        }
    }

    public static void onPlayerDeath(LivingDeathEvent event){
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.serverLevel().dimension() == CREATIVE_DIMENSION) {
                player.setHealth(20.0f);
                event.setCanceled(true);
                player.teleportTo(player.getX(), 1, player.getZ());
            }
        }
    }

    public static void onDimensionChange(EntityTravelToDimensionEvent event){
        if (event.getEntity() instanceof ServerPlayer && event.getEntity().level().dimension() == CREATIVE_DIMENSION){
            event.setCanceled(true);
        }
    }
}

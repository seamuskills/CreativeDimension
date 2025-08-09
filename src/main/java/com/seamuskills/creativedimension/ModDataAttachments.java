package com.seamuskills.creativedimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ModDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Creativedimension.MODID);

    public static final Supplier<AttachmentType<BlockPos>> CREATIVE_POS = ATTACHMENT_TYPES.register(
            "creative_pos", () -> AttachmentType.builder(() -> new BlockPos(0, 0, 0)).serialize(BlockPos.CODEC).build()
    );

    public static final Supplier<AttachmentType<BlockPos>> SURVIVAL_POS = ATTACHMENT_TYPES.register(
            "survival_pos", () -> AttachmentType.builder(() -> new BlockPos(0, 0, 0)).serialize(BlockPos.CODEC).build()
    );

    public static final Supplier<AttachmentType<String>> SURVIVAL_DIMENSION = ATTACHMENT_TYPES.register(
           "survival_dimension", () -> AttachmentType.builder(() -> "minecraft:overworld").serialize(Codec.STRING).build()
    );

    public static final Supplier<AttachmentType<CompoundTag>> SURVIVAL_INV = ATTACHMENT_TYPES.register(
            "survival_inv", () -> AttachmentType.builder(() -> new CompoundTag()).serialize(CompoundTag.CODEC).build()
    );

    public static final Supplier<AttachmentType<CompoundTag>> CREATIVE_INV = ATTACHMENT_TYPES.register(
            "creative_inv", () -> AttachmentType.builder(() -> new CompoundTag()).serialize(CompoundTag.CODEC).build()
    );

    public static final Supplier<AttachmentType<Float>> SURVIVAL_HEALTH = ATTACHMENT_TYPES.register(
            "survival_hp", () -> AttachmentType.builder(() -> 20f).serialize(Codec.FLOAT).build()
    );

    public static final Supplier<AttachmentType<XPthing>> SURVIVAL_XP = ATTACHMENT_TYPES.register(
            "survival_xp", () -> AttachmentType.builder(() -> new XPthing(0, 0.0f, 0)).serialize(XPthing.CODEC).build()
    );

    public static final Supplier<AttachmentType<CompoundTag>> SURVIVAL_HUNGER = ATTACHMENT_TYPES.register(
            "survival_hunger", () -> AttachmentType.builder(() -> new CompoundTag()).serialize(CompoundTag.CODEC).build()
    );

    public static final Supplier<AttachmentType<List<MobEffectInstance>>> SURVIVAL_EFFECTS = ATTACHMENT_TYPES.register(
            "survival_effects", () -> AttachmentType.builder(() -> (List<MobEffectInstance>)new ArrayList<MobEffectInstance>()).serialize(MobEffectInstance.CODEC.listOf()).build()
    );

    public record XPthing(int xpTotal, float xpProgress, int level) {
        public static final Codec<XPthing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("total").forGetter(XPthing::xpTotal),
                Codec.FLOAT.fieldOf("progress").forGetter(XPthing::xpProgress),
                Codec.INT.fieldOf("level").forGetter(XPthing::level)
                ).apply(instance, XPthing::new)
        );
    }

//    public record Effectdata(List<MobEffectInstance> effects){
//        public static final Codec<Effectdata> CODEC = RecordCodecBuilder.create(effectdataInstance -> effectdataInstance.group(
//                MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(Effectdata::effects)
//        ).apply(effectdataInstance, Effectdata::new));
//    }
}

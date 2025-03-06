package com.dimensiondelvers.dimensiondelvers.abilities.effects;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.abilities.AbilityAttributeHelper;
import com.dimensiondelvers.dimensiondelvers.abilities.AbstractAbility;
import com.dimensiondelvers.dimensiondelvers.abilities.Targetting.EffectTargeting;
import com.dimensiondelvers.dimensiondelvers.abilities.effects.util.ParticleInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public class HealEffect extends AbstractEffect {
    private float healAmount = 0;

    //TODO setup healing amount as part of the codec
    public static final MapCodec<HealEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    EffectTargeting.CODEC.fieldOf("targeting").forGetter(AbstractEffect::getTargeting),
                    Codec.list(AbstractEffect.DIRECT_CODEC).fieldOf("effects").forGetter(AbstractEffect::getEffects),
                    Codec.optionalField("particles", ParticleInfo.CODEC.codec(), true).forGetter(AbstractEffect::getParticles),
                    Codec.FLOAT.fieldOf("amount").forGetter(HealEffect::getAmount)
            ).apply(instance, HealEffect::new)
    );

    public float getAmount() {
        return healAmount;
    }

    @Override
    public MapCodec<? extends AbstractEffect> getCodec() {
        return CODEC;
    }

    public HealEffect(EffectTargeting targeting, List<AbstractEffect> effects, Optional<ParticleInfo> particles, float amount) {
        super(targeting, effects, particles);
        this.healAmount = amount;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, Player caster) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, caster);
        applyParticlesToUser(user);

        DimensionDelvers.LOGGER.info("Healing: " + targets.size());
        float finalHealAmount = AbilityAttributeHelper.getAbilityAttribute(Attributes.ATTACK_DAMAGE, healAmount, user);
        for(Entity target: targets) {
            applyParticlesToTarget(target);
            if(target instanceof LivingEntity living)
            {
                living.heal(finalHealAmount);
            }
            //Then apply children affects to targets
            super.apply(target, getTargeting().getBlocks(user), caster);
        }


        if(targets.isEmpty())
        {
            super.apply(null, getTargeting().getBlocks(user), caster);
        }
    }
}

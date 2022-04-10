package L_Ender.cataclysm.effects;


import net.minecraft.world.effect.AttackDamageMobEffect;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EffectBlazing_Brand extends AttackDamageMobEffect {

    public EffectBlazing_Brand() {
        super(MobEffectCategory.HARMFUL, 0X865337,-4);
        this.addAttributeModifier(Attributes.ARMOR, "68078724-8653-42D5-A245-9D14A1F54685", 0.0D, AttributeModifier.Operation.ADDITION);
    }

    public void applyEffectTick(LivingEntity LivingEntityIn, int amplifier) {

    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }

}

package L_Ender.cataclysm.event;

import L_Ender.cataclysm.init.ModEntities;
import L_Ender.cataclysm.init.ModItems;
import L_Ender.cataclysm.init.ModStructures;
import L_Ender.cataclysm.items.final_fractal;
import L_Ender.cataclysm.items.zweiender;
import L_Ender.cataclysm.cataclysm;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = cataclysm.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {



    @SubscribeEvent
    public void onLivingDamage(LivingHurtEvent event) {
        if (event.getSource() instanceof EntityDamageSource && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            LivingEntity target = event.getEntityLiving();
            ItemStack weapon = attacker.getMainHandItem();
            if (weapon != null && weapon.getItem() instanceof zweiender) {
                Vec3 lookDir = new Vec3(target.getLookAngle().x, 0, target.getLookAngle().z).normalize();
                Vec3 vecBetween = new Vec3(target.getX() - attacker.getX(), 0, target.getZ() - attacker.getZ()).normalize();
                double dot = lookDir.dot(vecBetween);
                if (dot > 0.05) {
                    event.setAmount(event.getAmount() * 2);
                    target.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.75F, 0.5F);
                }
            }
            if (weapon != null && weapon.getItem() instanceof final_fractal) {
                event.setAmount(event.getAmount() + target.getMaxHealth() * 0.03f);
            }

        }
    }
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (!event.getEntityLiving().getUseItem().isEmpty() && event.getSource() != null && event.getSource().getEntity() != null) {
            if (event.getEntityLiving().getUseItem().getItem() == ModItems.BULWARK_OF_THE_FLAME.get()) {
                Entity attacker = event.getSource().getEntity();
                if (attacker instanceof LivingEntity) {
                    if (attacker.distanceTo(event.getEntityLiving()) <= 4 && !attacker.isOnFire()) {
                        attacker.setSecondsOnFire(5);

                    }
                }

            }
        }
    }

}



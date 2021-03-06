package L_Ender.cataclysm;

import L_Ender.cataclysm.client.event.ClientEvent;
import L_Ender.cataclysm.client.model.armor.ModelMonstrousHelm;
import L_Ender.cataclysm.client.render.CMItemstackRenderer;
import L_Ender.cataclysm.client.render.entity.*;
import L_Ender.cataclysm.client.render.item.CMItemRenderProperties;
import L_Ender.cataclysm.client.render.item.CustomArmorRenderProperties;
import L_Ender.cataclysm.client.sound.SoundEnderGuardianMusic;
import L_Ender.cataclysm.client.sound.SoundMonstrosityMusic;
import L_Ender.cataclysm.config.CMConfig;
import L_Ender.cataclysm.entity.Ender_Guardian_Entity;
import L_Ender.cataclysm.entity.Netherite_Monstrosity_Entity;
import L_Ender.cataclysm.init.ModEntities;
import L_Ender.cataclysm.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = cataclysm.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public static final Map<Integer, SoundMonstrosityMusic> MONSTROSITY_SOUND_MAP = new HashMap<>();
    public static final Map<Integer, SoundEnderGuardianMusic> GUARDIAN_SOUND_MAP = new HashMap<>();

    public void clientInit() {
        ItemRenderer itemRendererIn = Minecraft.getInstance().getItemRenderer();
        EntityRenderers.register(ModEntities.ENDER_GOLEM.get(), RendererEnder_Golem::new);
        EntityRenderers.register(ModEntities.NETHERITE_MONSTROSITY.get(), RendererNetherite_Monstrosity::new);
        EntityRenderers.register(ModEntities.LAVA_BOMB.get(), RendererLava_Bomb::new);
        EntityRenderers.register(ModEntities.NAMELESS_SORCERER.get(), RendererNameless_Sorcerer::new);
        EntityRenderers.register(ModEntities.IGNIS.get(), RendererIgnis::new);
        EntityRenderers.register(ModEntities.ENDER_GUARDIAN.get(), RendererEnder_Guardian::new);
        EntityRenderers.register(ModEntities.ENDER_GUARDIAN_BULLET.get(), RendererEnder_Guardian_bullet::new);
        EntityRenderers.register(ModEntities.VOID_RUNE.get(), RendererVoid_Rune::new);
        EntityRenderers.register(ModEntities.ENDERMAPTERA.get(), RendererEndermaptera::new);
        EntityRenderers.register(ModEntities.VOID_SCATTER_ARROW.get(), RendererVoid_Scatter_Arrow::new);
        EntityRenderers.register(ModEntities.SCREEN_SHAKE.get(), RendererNull::new);
        EntityRenderers.register(ModEntities.CM_FALLING_BLOCK.get(), RendererCm_Falling_Block::new);
        EntityRenderers.register(ModEntities.IGNIS_FLAME.get(), RendererNull::new);
        EntityRenderers.register(ModEntities.IGNIS_FIREBALL.get(), RendererIgnis_Fireball::new);
        EntityRenderers.register(ModEntities.VOID_SHARD.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
        MinecraftForge.EVENT_BUS.register(new ClientEvent());
        try {
            ItemProperties.register(ModItems.BULWARK_OF_THE_FLAME.get(), new ResourceLocation("blocking"), (stack, p_239421_1_, p_239421_2_, j) -> p_239421_2_ != null && p_239421_2_.isUsingItem() && p_239421_2_.getUseItem() == stack ? 1.0F : 0.0F);
            ItemProperties.register(Items.CROSSBOW, new ResourceLocation(cataclysm.MODID, "void_scatter_arrow"), (stack, world, entity, j) -> entity != null && CrossbowItem.isCharged(stack) && CrossbowItem.containsChargedProjectile(stack, ModItems.VOID_SCATTER_ARROW.get()) ? 1.0F : 0.0F);
        } catch (Exception e) {
            cataclysm.LOGGER.warn("Could not load item models for weapons");

        }
    }


    @OnlyIn(Dist.CLIENT)
    public static Callable<BlockEntityWithoutLevelRenderer> getTEISR() {
        return CMItemstackRenderer::new;
    }


    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }


    @OnlyIn(Dist.CLIENT)
    public void onEntityStatus(Entity entity, byte updateKind) {
        float f2 = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.RECORDS);
        if(CMConfig.BossMusic) {
            if (entity instanceof Netherite_Monstrosity_Entity && entity.isAlive() && updateKind == 67) {
                if (f2 <= 0) {
                    MONSTROSITY_SOUND_MAP.clear();
                } else {
                    SoundMonstrosityMusic sound;
                    if (MONSTROSITY_SOUND_MAP.get(entity.getId()) == null) {
                        sound = new SoundMonstrosityMusic((Netherite_Monstrosity_Entity) entity);
                        MONSTROSITY_SOUND_MAP.put(entity.getId(), sound);
                    } else {
                        sound = MONSTROSITY_SOUND_MAP.get(entity.getId());
                    }
                    if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.isNearest()) {
                        Minecraft.getInstance().getSoundManager().play(sound);
                    }
                }

            }
            if (entity instanceof Ender_Guardian_Entity && entity.isAlive() && updateKind == 67) {
                if (f2 <= 0) {
                    GUARDIAN_SOUND_MAP.clear();
                } else {
                    SoundEnderGuardianMusic sound;
                    if (GUARDIAN_SOUND_MAP.get(entity.getId()) == null) {
                        sound = new SoundEnderGuardianMusic((Ender_Guardian_Entity) entity);
                        GUARDIAN_SOUND_MAP.put(entity.getId(), sound);
                    } else {
                        sound = GUARDIAN_SOUND_MAP.get(entity.getId());
                    }
                    if (!Minecraft.getInstance().getSoundManager().isActive(sound) && sound.isNearest()) {
                        Minecraft.getInstance().getSoundManager().play(sound);
                    }
                }

            }
        }


    }

    @Override
    public Object getISTERProperties() {
        return new CMItemRenderProperties();
    }

    @Override
    public Object getArmorRenderProperties() {
        return new CustomArmorRenderProperties();
    }

}
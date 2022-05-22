package L_Ender.cataclysm.entity;

import L_Ender.cataclysm.config.CMConfig;
import L_Ender.cataclysm.entity.AI.CmAttackGoal;
import L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import L_Ender.cataclysm.entity.etc.CMPathNavigateGround;
import L_Ender.cataclysm.entity.etc.SmartBodyHelper2;
import L_Ender.cataclysm.init.ModEffect;
import L_Ender.cataclysm.init.ModSounds;
import L_Ender.cataclysm.init.ModTag;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class Ignis_Entity extends Boss_monster {
    private final ServerBossEvent bossInfo = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(false);
    public static final Animation SWING_ATTACK = Animation.create(65);
    public static final Animation HORIZONTAL_SWING_ATTACK = Animation.create(68);
    public static final Animation SHIELD_SMASH_ATTACK = Animation.create(70);
    public static final Animation PHASE_2 = Animation.create(68);
    public static final Animation POKE_ATTACK = Animation.create(65);
    public static final Animation POKE_ATTACK2 = Animation.create(56);
    public static final Animation POKE_ATTACK3 = Animation.create(50);
    public static final Animation POKED_ATTACK = Animation.create(65);
    public static final Animation PHASE = Animation.create(130);
    public static final Animation MAGIC_ATTACK = Animation.create(95);
    public static final Animation SMASH_IN_AIR = Animation.create(105);
    public static final Animation SMASH = Animation.create(47);
    public static final Animation BODY_CHECK_ATTACK1 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK2 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK3 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK4 = Animation.create(62);
    public static final Animation COMBO_SWING_ATTACK = Animation.create(95);
    public static final int BODY_CHECK_COOLDOWN = 200;
    private static final EntityDataAccessor<Boolean> IS_BLOCKING = SynchedEntityData.defineId(Ignis_Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SHIELD = SynchedEntityData.defineId(Ignis_Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BOSS_PHASE = SynchedEntityData.defineId(Ignis_Entity.class, EntityDataSerializers.INT);
    private Vec3 prevBladePos = new Vec3(0, 0, 0);
    private int body_check_cooldown = 0;
    private int timeWithoutTarget;
    public float blockingProgress;
    public float prevblockingProgress;


    public Ignis_Entity(EntityType entity, Level world) {
        super(entity, world);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.maxUpStep = 1.5F;
        if (world.isClientSide)
            socketPosArray = new Vec3[] {new Vec3(0, 0, 0)};
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{
                NO_ANIMATION,
                SWING_ATTACK,
                HORIZONTAL_SWING_ATTACK,
                POKE_ATTACK,
                POKE_ATTACK2,
                POKE_ATTACK3,
                POKED_ATTACK,
                MAGIC_ATTACK,
                PHASE,
                SHIELD_SMASH_ATTACK,
                PHASE_2,
                BODY_CHECK_ATTACK4,
                BODY_CHECK_ATTACK3,
                BODY_CHECK_ATTACK2,
                BODY_CHECK_ATTACK1,
                SMASH,
                SMASH_IN_AIR,
                COMBO_SWING_ATTACK};
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new CmAttackGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new Hornzontal_Swing());
        this.goalSelector.addGoal(1, new Poke());
        this.goalSelector.addGoal(1, new Phase_Transition());
        this.goalSelector.addGoal(1, new Shield_Smash());
        this.goalSelector.addGoal(1, new Body_Check());
        this.goalSelector.addGoal(1, new Poked());
        this.goalSelector.addGoal(1, new Air_Smash());
        this.goalSelector.addGoal(1, new Smash());
        this.goalSelector.addGoal(1, new Vertical_Swing());
        this.goalSelector.addGoal(1, new Combo_Swing());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

    }


    @Override
    public boolean hurt(DamageSource source, float damage) {
        double range = calculateRange(source);

        if (range > CMConfig.IgnisLongRangelimit * CMConfig.IgnisLongRangelimit) {
            return false;
        }

        if (!source.isBypassInvul()) {
            damage = Math.min(CMConfig.IgnisDamageCap, damage);
        }

        if (this.getAnimation() == PHASE_2 && !source.isBypassInvul()) {
            return false;
        }

        Entity entity = source.getDirectEntity();
        if (damage > 0.0F && this.canBlockDamageSource(source)) {
            this.hurtCurrentlyUsedShield(damage);

            if (!source.isProjectile()) {
                if (entity instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity) entity);
                }
            }
            this.playSound(SoundEvents.BLAZE_HURT, 0.5f, 0.4F + this.getRandom().nextFloat() * 0.1F);
            return false;
        }

        return super.hurt(source, damage);
    }

    private boolean canBlockDamageSource(DamageSource damageSourceIn) {
        Entity entity = damageSourceIn.getDirectEntity();
        boolean flag = false;
        if (entity instanceof AbstractArrow) {
            AbstractArrow abstractarrowentity = (AbstractArrow)entity;
            if (abstractarrowentity.getPierceLevel() > 0) {
                flag = true;
            }
        }

        if (!damageSourceIn.isBypassArmor() && !flag && this.getIsShield()) {
            Vec3 vector3d2 = damageSourceIn.getSourcePosition();
            if (vector3d2 != null) {
                Vec3 vector3d = this.getViewVector(1.0F);
                Vec3 vector3d1 = vector3d2.vectorTo(this.position()).normalize();
                vector3d1 = new Vec3(vector3d1.x, 0.0D, vector3d1.z);
                return vector3d1.dot(vector3d) < 0.0D;
            }
        }
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_BLOCKING, false);
        this.entityData.define(IS_SHIELD, false);
        this.entityData.define(BOSS_PHASE, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BossPhase", this.getBossPhase());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setBossPhase(compound.getInt("BossPhase"));
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    public void setIsBlocking(boolean isBlocking) {
         this.entityData.set(IS_BLOCKING, isBlocking);
    }

    public boolean getIsBlocking() {
        return this.entityData.get(IS_BLOCKING);
    }

    public void setIsShield(boolean isShield) {
        this.entityData.set(IS_SHIELD, isShield);
    }

    public boolean getIsShield() {
        return this.entityData.get(IS_SHIELD);
    }


    public void setBossPhase(int bossPhase) {
        this.entityData.set(BOSS_PHASE, bossPhase);
    }

    public int getBossPhase() {
        return this.entityData.get(BOSS_PHASE);
    }



    public static AttributeSupplier.Builder ignis() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33F)
                .add(Attributes.ATTACK_DAMAGE, CMConfig.IgnisDamage)
                .add(Attributes.MAX_HEALTH, CMConfig.IgnisHealth)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public float getBrightness() {
        return 1.0F;
    }

    protected int decreaseAirSupply(int air) {
        return air;
    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.IGNIS_AMBIENT.get();
    }

    private static Animation getRandomPoke(Random rand) {
        switch (rand.nextInt(3)) {
            case 0:
                return POKE_ATTACK;
            case 1:
                return POKE_ATTACK2;
            case 2:
                return POKE_ATTACK3;
        }
        return POKE_ATTACK;
    }

    public void tick() {
        if (!this.onGround && this.getDeltaMovement().y < 0.0D && this.getAnimation() == NO_ANIMATION) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }

        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        prevblockingProgress = blockingProgress;
        if (this.getIsBlocking() && blockingProgress < 10F) {
            blockingProgress++;
        }
        if (!this.getIsBlocking() && blockingProgress > 0F) {
            blockingProgress--;

        }
        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0).isShiftKeyDown()) {
            this.getPassengers().get(0).setShiftKeyDown(false);
        }

        LivingEntity target = this.getTarget();
        spawnSwipeParticles();
        if (this.level.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.level.playLocalSound(this.getX() + 0.5D, this.getY() + 0.5D, this.getZ() + 0.5D, SoundEvents.BLAZE_BURN, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for(int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }else{
            timeWithoutTarget++;
            if (target != null) {
                timeWithoutTarget = 0;
                if(!this.getIsBlocking()) {
                    this.setIsBlocking(true);
                }
            }

            if (timeWithoutTarget > 150 && this.getIsBlocking() && target == null) {
                timeWithoutTarget = 0;
                this.setIsBlocking(false);
            }

            if (this.getBossPhase() > 0){
                bossInfo.setColor(BossEvent.BossBarColor.BLUE);
            }
        }
        if (body_check_cooldown > 0) body_check_cooldown--;
        repelEntities(1.7F, 4, 1.7F, 1.7F);

        setYRot(yBodyRot);
        if (this.isAlive()) {
            if (!isNoAi() && this.getAnimation() == NO_ANIMATION && this.getHealth() <= this.getMaxHealth() / 2.0F && this.getBossPhase() < 1) {
                this.setAnimation(PHASE_2);
            } else if (target != null && target.isAlive()) {
                if (!isNoAi() && this.getAnimation() == NO_ANIMATION && this.distanceToSqr(target) >= 64 && this.distanceToSqr(target) <= 1024.0D && target.isOnGround() && this.getRandom().nextFloat() * 100.0F < 0.9f) {
                    this.setAnimation(SMASH_IN_AIR);
                } else if (!isNoAi() && this.getAnimation() == NO_ANIMATION && this.distanceTo(target) < 5.5F && this.getRandom().nextFloat() * 100.0F < 10f) {
                    Animation animation = getRandomPoke(random);
                    this.setAnimation(COMBO_SWING_ATTACK);
                } else if (!isNoAi() && this.getAnimation() == NO_ANIMATION && this.distanceTo(target) < 5F && this.getRandom().nextFloat() * 100.0F < 8f) {
                    if (this.random.nextInt(3) == 0) {
                        this.setAnimation(COMBO_SWING_ATTACK);
                    }else{
                        this.setAnimation(COMBO_SWING_ATTACK);
                    }
                } else if (!isNoAi() && this.getAnimation() == NO_ANIMATION && this.distanceTo(target) < 3F && this.getRandom().nextFloat() * 100.0F < 28f) {
                    if (this.random.nextInt(3) == 0 && body_check_cooldown <= 0) {
                        body_check_cooldown = BODY_CHECK_COOLDOWN;
                        this.setAnimation(COMBO_SWING_ATTACK);
                    } else {
                        this.setAnimation(COMBO_SWING_ATTACK);
                    }
                }
            }
        }
        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);

        if(this.getIsBlocking() && blockingProgress == 10){
            if(this.getAnimation() == NO_ANIMATION) {
                setIsShield(true);
            }
            else if(this.getAnimation() == POKED_ATTACK) {
                setIsShield(false);
            }
            else if (this.getAnimation() == HORIZONTAL_SWING_ATTACK) {
                setIsShield(this.getAnimationTick() > 31);
            }
            else if (this.getAnimation() == BODY_CHECK_ATTACK1 || this.getAnimation() == BODY_CHECK_ATTACK2 ||
                    this.getAnimation() == BODY_CHECK_ATTACK3 || this.getAnimation() == BODY_CHECK_ATTACK4) {
                setIsShield(this.getAnimationTick() < 25);
            }
            else if(this.getAnimation() == POKE_ATTACK) {
                setIsShield(this.getAnimationTick() < 39);
            }
            else if(this.getAnimation() == POKE_ATTACK2) {
                setIsShield(this.getAnimationTick() < 34);
            }
            else if(this.getAnimation() == POKE_ATTACK3) {
                setIsShield(this.getAnimationTick() < 29);
            }
            else if (this.getAnimation() == SWING_ATTACK) {
                setIsShield(this.getAnimationTick() < 34);
            }
        }else{
            setIsShield(false);
        }


    }
    public void aiStep() {
        super.aiStep();
        if (this.getAnimation() == SWING_ATTACK) {
            if (this.getAnimationTick() == 34) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRandom().nextFloat() * 0.1F);
                AreaAttack(5.25f,6,60,1.0f,0.05f,80,3 ,150);
            }
        }
        if (this.getAnimation() == HORIZONTAL_SWING_ATTACK) {
            if (this.getAnimationTick() == 31) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRandom().nextFloat() * 0.1F);
                AreaAttack(5.25f,6,170,1.1f,0.08f,100,5 ,150);
            }
        }
        if (this.getAnimation() == PHASE_2) {
            if (this.getAnimationTick() == 29){
                this.playSound(ModSounds.FLAME_BURST.get(), 1.0f, 1F + this.getRandom().nextFloat() * 0.1F);
            }
            if (this.getAnimationTick() > 29 && this.getAnimationTick() < 39){
                Sphereparticle(2, 5);
            }
            if (this.getAnimationTick() == 34) {
                setBossPhase(1);
            }
        }
        if (this.getAnimation() == SHIELD_SMASH_ATTACK) {
            if (this.getAnimationTick() == 34){
                this.playSound(SoundEvents.TOTEM_USE, 1.5f, 0.8F + this.getRandom().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(level, this.position(), 20, 0.3f, 0, 20);
                AreaAttack(4.85f,2.5f,45,1.5f,0.15f,200,0,0);
                ShieldSmashDamage(4,2.75f);
                ShieldSmashparticle(2.75f,-0.1f);
            }
            if (this.getAnimationTick() == 37) {
                ShieldSmashDamage(5,2.75f);
            }
            if (this.getAnimationTick() == 40) {
                ShieldSmashDamage(6,2.75f);
            }
        }
        if (this.getAnimation() == SMASH) {
            if (this.getAnimationTick() == 5){
                this.playSound(SoundEvents.TOTEM_USE, 1.5f, 0.8F + this.getRandom().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(level, this.position(), 20, 0.3f, 0, 20);
                AreaAttack(4.85f,2.5f,45,1.5f,0.15f,200,0,0);
                ShieldSmashDamage(3,1.5f);
                ShieldSmashparticle(1.5f,0.0f);
            }
            if (this.getAnimationTick() == 8) {
                ShieldSmashDamage(4,1.5f);
            }
            if (this.getAnimationTick() == 11) {
                ShieldSmashDamage(5,1.5f);
            }
            if (this.getAnimationTick() == 14) {
                ShieldSmashDamage(6,1.5f);
            }
        }

        if (this.getAnimation() == POKE_ATTACK) {
            if (this.getAnimationTick() == 39) {
                Poke(7, 70,60);
            }
        }

        if (this.getAnimation() == POKE_ATTACK2) {
            if (this.getAnimationTick() == 34) {
                Poke(7, 65,50);
            }
        }

        if (this.getAnimation() == POKE_ATTACK3) {
            if (this.getAnimationTick() == 29) {
                Poke(7, 60,40);
            }
        }

        if (this.getAnimation() == BODY_CHECK_ATTACK1
                || this.getAnimation() == BODY_CHECK_ATTACK2
                || this.getAnimation() == BODY_CHECK_ATTACK3
                || this.getAnimation() == BODY_CHECK_ATTACK4) {
            if (this.getAnimationTick() == 25) {
                BodyCheckAttack(3.0f,6,120,0.8f,0.03f,40,80);
            }

        }
    }


    public void setMaxHealth(double maxHealth, boolean heal){
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        if(heal){
            this.heal((float)maxHealth);
        }
    }

    private void AreaAttack(float range,float height,float arc ,float damage, float hpdamage ,int shieldbreakticks, int firetime, int brandticks) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.yBodyRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                if (!(entityHit instanceof Ignis_Entity)) {
                    boolean flag = entityHit.hurt(DamageSource.mobAttack(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage + entityHit.getMaxHealth() * hpdamage));
                    if (entityHit instanceof Player && entityHit.isBlocking() && shieldbreakticks > 0) {
                        disableShield(entityHit, shieldbreakticks);
                    }
                    if (flag) {
                        entityHit.setSecondsOnFire(firetime);
                        if (brandticks > 0){
                            MobEffectInstance effectinstance1 = entityHit.getEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                            int i = 1;
                            if (effectinstance1 != null) {
                                i += effectinstance1.getAmplifier();
                                entityHit.removeEffectNoUpdate(ModEffect.EFFECTBLAZING_BRAND.get());
                            } else {
                                --i;
                            }

                            i = Mth.clamp(i, 0, 4);
                            MobEffectInstance effectinstance = new MobEffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), brandticks, i, false, false, true);
                            entityHit.addEffect(effectinstance);
                            this.heal(6 * (i + 1));
                        }
                    }
                }
            }
        }
    }

    private void BodyCheckAttack(float range, float height, float arc, float damage, float hpdamage, int shieldbreakticks, int slowticks) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.yBodyRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                if (!isAlliedTo(entityHit) && !(entityHit instanceof Ignis_Entity)) {
                    boolean flag = entityHit.hurt(DamageSource.mobAttack(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage + entityHit.getMaxHealth() * hpdamage));
                    if (entityHit instanceof Player) {
                        if (entityHit.isBlocking() && shieldbreakticks > 0) {
                            disableShield(entityHit, shieldbreakticks);
                        }
                    }
                    double d0 = entityHit.getX() - this.getX();
                    double d1 = entityHit.getZ() - this.getZ();
                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                    entityHit.push(d0 / d2 * 2.5D, 0.2D, d1 / d2 * 2.5D);
                    if (flag) {
                        this.playSound(SoundEvents.ANVIL_LAND, 1.5f, 0.8F + this.getRandom().nextFloat() * 0.1F);
                        if (slowticks > 0){
                            entityHit.addEffect(new MobEffectInstance(ModEffect.EFFECTSTUN.get(), slowticks, 1,false, false, true));

                        }
                    }
                }
            }
        }
    }

    private void Poke(float range, float arc, int shieldbreakticks){
        LivingEntity target = this.getTarget();
        if (target != null) {
            float entityHitAngle = (float) ((Math.atan2(target.getZ() - this.getZ(), target.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.yBodyRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            if (this.distanceTo(target) <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                boolean flag = target.hurt(DamageSource.mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + target.getMaxHealth() * 0.1f);
                if (target instanceof Player) {
                    if (target.isBlocking() && shieldbreakticks > 0) {
                        disableShield(target, shieldbreakticks);
                    }
                }

                if (flag) {
                    if(target.isShiftKeyDown()) {
                        target.setShiftKeyDown(false);
                    }
                    target.startRiding(this, true);
                    AnimationHandler.INSTANCE.sendAnimationMessage(this, POKED_ATTACK);
                }
            }
        }
    }

    private void spawnSwipeParticles() {
        if (level.isClientSide) {
            Vec3 bladePos = socketPosArray[0];
            int snowflakeDensity = 4;
            float snowflakeRandomness = 0.5f;
            double length = prevBladePos.subtract(bladePos).length();
            int numClouds = (int) Math.floor(2 * length);
            if(this.getAnimation() == HORIZONTAL_SWING_ATTACK) {
                if (this.getAnimationTick() > 27 && this.getAnimationTick() < 33) {
                    for (int i = 0; i < numClouds; i++) {
                        double x = prevBladePos.x + i * (bladePos.x - prevBladePos.x) / numClouds;
                        double y = prevBladePos.y + i * (bladePos.y - prevBladePos.y) / numClouds;
                        double z = prevBladePos.z + i * (bladePos.z - prevBladePos.z) / numClouds;
                        for (int j = 0; j < snowflakeDensity; j++) {
                            float xOffset = snowflakeRandomness * (2 * random.nextFloat() - 1);
                            float yOffset = snowflakeRandomness * (2 * random.nextFloat() - 1);
                            float zOffset = snowflakeRandomness * (2 * random.nextFloat() - 1);
                            if (this.getBossPhase() > 0) {
                                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x + xOffset, y + yOffset, z + zOffset, 0, 0, 0);
                            }else{
                                level.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0, 0, 0);
                            }
                        }
                    }
                }
            }
            if(this.getAnimation() == SWING_ATTACK) {
                if (this.getAnimationTick() > 25 && this.getAnimationTick() < 37) {
                    for (int i = 0; i < numClouds; i++) {
                        double x = prevBladePos.x + i * (bladePos.x - prevBladePos.x) / numClouds;
                        double y = prevBladePos.y + i * (bladePos.y - prevBladePos.y) / numClouds;
                        double z = prevBladePos.z + i * (bladePos.z - prevBladePos.z) / numClouds;
                        for (int j = 0; j < snowflakeDensity; j++) {
                            float xOffset = snowflakeRandomness * (2 * random.nextFloat() - 1);
                            float yOffset = snowflakeRandomness * (2 * random.nextFloat() - 1);
                            float zOffset = snowflakeRandomness * (2 * random.nextFloat() - 1);
                            if (this.getBossPhase() > 0) {
                                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x + xOffset, y + yOffset, z + zOffset, 0, 0, 0);
                            }else{
                                level.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0, 0, 0);
                            }
                        }
                    }
                }
            }
            prevBladePos = bladePos;
        }
    }

    private void ShieldSmashparticle(float vec, float math) {
        if (this.level.isClientSide) {
            for (int i1 = 0; i1 < 80 + random.nextInt(12); i1++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float angle = (0.01745329251F * this.yBodyRot) + i1;
                float f = Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) ;
                float f1 = Mth.sin(this.getYRot() * ((float)Math.PI / 180F)) ;
                double extraX = 1.3 * Mth.sin((float) (Math.PI + angle));
                double extraY = 0.3F;
                double extraZ = 1.3 * Mth.cos(angle);
                double theta = (yBodyRot) * (Math.PI / 180);
                theta += Math.PI / 2;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                int hitX = Mth.floor(getX() + vec * vecX+ extraX);
                int hitY = Mth.floor(getY());
                int hitZ = Mth.floor(getZ() + vec * vecZ + extraZ);
                BlockPos hit = new BlockPos(hitX, hitY, hitZ);
                BlockState block = level.getBlockState(hit.below());
                this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block), getX() + vec * vecX + extraX + f * math, this.getY() + extraY, getZ() + vec * vecZ + extraZ + f1 * math, motionX, motionY, motionZ);

            }
        }
    }


    private void ShieldSmashDamage(int distance, float vec) {
        double perpFacing = this.yBodyRot * (Math.PI / 180);
        double facingAngle = perpFacing + Math.PI / 2;
        int hitY = Mth.floor(this.getBoundingBox().minY - 0.5);
        double spread = Math.PI * 2;
        int arcLen = Mth.ceil(distance * spread);
        double minY = this.getY() - 1;
        double maxY = this.getY() + 1.5;
        for (int i = 0; i < arcLen; i++) {
            double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = this.getX() + vx * distance + vec * Math.cos((yBodyRot + 90) * Math.PI / 180);
            double pz = this.getZ() + vz * distance + vec * Math.sin((yBodyRot + 90) * Math.PI / 180);
            if (!this.level.isClientSide) {
                int hitX = Mth.floor(px);
                int hitZ = Mth.floor(pz);
                BlockPos pos = new BlockPos(hitX, hitY, hitZ);
                BlockPos abovePos = new BlockPos(pos).above();
                BlockState block = level.getBlockState(pos);
                BlockState blockAbove = level.getBlockState(abovePos);
                if (block.getMaterial() != Material.AIR && !block.hasBlockEntity() && !blockAbove.getMaterial().blocksMotion() && !block.is(ModTag.NETHERITE_MONSTROSITY_IMMUNE)) {
                    Cm_Falling_Block_Entity fallingBlockEntity = new Cm_Falling_Block_Entity(level, hitX + 0.5D, hitY + 0.5D, hitZ + 0.5D, block);
                    level.setBlock(pos, block.getFluidState().createLegacyBlock(), 3);
                    fallingBlockEntity.push(0, 0.2D + getRandom().nextGaussian() * 0.15D, 0);
                    level.addFreshEntity(fallingBlockEntity);
                }
            }
            AABB selection = new AABB(px - 0.5, minY, pz - 0.5, px + 0.5, maxY, pz + 0.5);
            List<LivingEntity> hit = level.getEntitiesOfClass(LivingEntity.class, selection);
            for (LivingEntity entity : hit) {
                if (!isAlliedTo(entity) && !(entity instanceof Ignis_Entity) && entity != this) {
                    boolean flag = entity.hurt(DamageSource.mobAttack(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
                    if (flag) {
                        double airborne = 0.1 * distance + level.random.nextDouble() * 0.15;
                        entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, airborne, 0.0D));
                    }
                }
            }

        }
    }


    private void Sphereparticle(float height, float size) {
        if (this.level.isClientSide) {
            if (this.tickCount % 2 == 0) {
                double d0 = this.getX();
                double d1 = this.getY() + height;
                double d2 = this.getZ();
                for (float i = -size; i <= size; ++i) {
                    for (float j = -size; j <= size; ++j) {
                        for (float k = -size; k <= size; ++k) {
                            double d3 = (double) j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                            double d4 = (double) i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                            double d5 = (double) k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                            double d6 = (double) Mth.sqrt((float) (d3 * d3 + d4 * d4 + d5 * d5)) / 0.5 + this.random.nextGaussian() * 0.05D;
                            if (this.getBossPhase() == 0) {
                                this.level.addParticle(ParticleTypes.FLAME, d0, d1, d2, d3 / d6, d4 / d6, d5 / d6);
                            } else {
                                this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, d3 / d6, d4 / d6, d5 / d6);
                            }
                            if (i != -size && i != size && j != -size && j != size) {
                                k += size * 2 - 1;
                            }
                        }
                    }
                }
            }
        }
    }

    public void positionRider(Entity passenger) {
        super.positionRider(passenger);
        if (hasPassenger(passenger)) {
            int tick = 5;
            if (this.getAnimation() == POKED_ATTACK) {
                tick = this.getAnimationTick();
                if(this.getAnimationTick() == 46) {
                    passenger.stopRiding();
                    float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.getYRot() + 90));
                    float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.getYRot() + 90));
                    //passenger.push(f1 * 2.5, 0.8, f2 * 2.5);
                }
                this.setYRot(this.yRotO);
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.getYRot();
            }
            float radius = 4F;
            float math = 0.2f;
            float angle = (0.01745329251F * this.yBodyRot);
            float f = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
            float f1 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            double extraY = tick < 10 ? 0 : 0.2F * Mth.clamp(tick - 10, 0, 15);
            passenger.setPos(this.getX() + f * math + extraX, this.getY() + extraY + 1.2F, this.getZ() + f1 * math + extraZ);
            if ((tick - 10) % 4 == 0) {
                LivingEntity target = this.getTarget();
                if (target != null) {
                    if(passenger == target) {
                        boolean flag = target.hurt(DamageSource.mobAttack(this), 4 + target.getMaxHealth() * 0.02f);
                        if (flag) {
                            MobEffectInstance effectinstance1 = target.getEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                            int i = 1;
                            if (effectinstance1 != null) {
                                i += effectinstance1.getAmplifier();
                                target.removeEffectNoUpdate(ModEffect.EFFECTBLAZING_BRAND.get());
                            } else {
                                --i;
                            }

                            i = Mth.clamp(i, 0, 4);
                            MobEffectInstance effectinstance = new MobEffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), 150, i, false, false, true);
                            target.addEffect(effectinstance);
                            this.heal(2f * (i + 1));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    protected void repelEntities(float x, float y, float z, float radius) {
        super.repelEntities(x, y, z, radius);
    }

    @Override
    public boolean canBePushedByEntity(Entity entity) {
        return false;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.setMaxHealth(CMConfig.IgnisHealth, true);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(CMConfig.IgnisDamage);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public void travel(Vec3 travelVector) {
        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (isInLava() ? 0.2F : 1F));
        if (this.getAnimation() == POKED_ATTACK) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
            super.travel(travelVector);
            return;
        }
        super.travel(travelVector);

    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new SmartBodyHelper2(this);
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new CMPathNavigateGround(this, worldIn);
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    private boolean shouldFollowUp(float Range) {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            Vec3 targetMoveVec = target.getDeltaMovement();
            Vec3 betweenEntitiesVec = this.position().subtract(target.position());
            boolean targetComingCloser = targetMoveVec.dot(betweenEntitiesVec) > 0;
            return this.distanceTo(target) < Range || (this.distanceTo(target) < 5 + Range && targetComingCloser);
        }
        return false;
    }

    class Hornzontal_Swing extends Goal {

        public Hornzontal_Swing() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == HORIZONTAL_SWING_ATTACK;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getTarget();
            if (Ignis_Entity.this.getAnimationTick() < 31 && target != null || Ignis_Entity.this.getAnimationTick() > 51 && target != null) {
                Ignis_Entity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.setYRot(Ignis_Entity.this.yRotO);
            }
            if (Ignis_Entity.this.getAnimationTick() == 26) {
                float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.getYRot() + 90));
                float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.getYRot() + 90));
                if(target != null) {
                    float r =  Ignis_Entity.this.distanceTo(target);
                    r = Mth.clamp(r, 0, 15);
                    Ignis_Entity.this.push(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                }else{
                    Ignis_Entity.this.push(f1,0, f2);
                }
            }
            if (Ignis_Entity.this.getAnimationTick() == 36 && shouldFollowUp(3.5f) && Ignis_Entity.this.random.nextInt(3) == 0 && body_check_cooldown <= 0) {
                body_check_cooldown = BODY_CHECK_COOLDOWN;
                AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK2);
            }

            if(Ignis_Entity.this.getAnimationTick() > 32 || Ignis_Entity.this.getAnimationTick() < 26){
                Ignis_Entity.this.setDeltaMovement(0, Ignis_Entity.this.getDeltaMovement().y, 0);
            }
        }
    }


    class Poke extends Goal {

        public Poke() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == POKE_ATTACK || Ignis_Entity.this.getAnimation() == POKE_ATTACK2 || Ignis_Entity.this.getAnimation() == POKE_ATTACK3;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getTarget();
            float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.getYRot() + 90));
            float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.getYRot() + 90));
            if(Ignis_Entity.this.getAnimation() == POKE_ATTACK) {
                if (Ignis_Entity.this.getAnimationTick() < 39 && target != null || Ignis_Entity.this.getAnimationTick() > 59 && target != null) {
                    Ignis_Entity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.setYRot(Ignis_Entity.this.yRotO);
                }
                if (Ignis_Entity.this.getAnimationTick() == 34) {
                    if (target != null) {
                        float r = Ignis_Entity.this.distanceTo(target);
                        r = Mth.clamp(r, 0, 6.5f);
                        Ignis_Entity.this.push(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                    } else {
                        Ignis_Entity.this.push(f1 * 1.5, 0, f2 * 1.5);
                    }
                }
                if (Ignis_Entity.this.getAnimationTick() == 42 && shouldFollowUp(3.0f) && Ignis_Entity.this.random.nextInt(2) == 0 && body_check_cooldown <= 0) {
                    body_check_cooldown = BODY_CHECK_COOLDOWN;
                    AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK4);
                }

                if (Ignis_Entity.this.getAnimationTick() > 40 || Ignis_Entity.this.getAnimationTick() < 34) {
                    Ignis_Entity.this.setDeltaMovement(0, Ignis_Entity.this.getDeltaMovement().y, 0);
                }
            }
            if(Ignis_Entity.this.getAnimation() == POKE_ATTACK2) {
                if (Ignis_Entity.this.getAnimationTick() < 33 && target != null || Ignis_Entity.this.getAnimationTick() > 53 && target != null) {
                    Ignis_Entity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.setYRot(Ignis_Entity.this.yRotO);
                }
                if (Ignis_Entity.this.getAnimationTick() == 28) {
                    if (target != null) {
                        float r = Ignis_Entity.this.distanceTo(target);
                        r = Mth.clamp(r, 0, 6.5f);
                        Ignis_Entity.this.push(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                    } else {
                        Ignis_Entity.this.push(f1 * 1.5, 0, f2 * 1.5);
                    }
                }
                if (Ignis_Entity.this.getAnimationTick() == 36 && shouldFollowUp(3.0f) && Ignis_Entity.this.random.nextInt(2) == 0 && body_check_cooldown <= 0) {
                    body_check_cooldown = BODY_CHECK_COOLDOWN;
                    AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK4);
                }
                if (Ignis_Entity.this.getAnimationTick() > 34 || Ignis_Entity.this.getAnimationTick() < 28) {
                    Ignis_Entity.this.setDeltaMovement(0, Ignis_Entity.this.getDeltaMovement().y, 0);
                }
            }
            if(Ignis_Entity.this.getAnimation() == POKE_ATTACK3) {
                if (Ignis_Entity.this.getAnimationTick() < 29 && target != null) {
                    Ignis_Entity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.setYRot(Ignis_Entity.this.yRotO);
                }
                if (Ignis_Entity.this.getAnimationTick() == 24) {
                    if (target != null) {
                        float r = Ignis_Entity.this.distanceTo(target);
                        r = Mth.clamp(r, 0, 6.5f);
                        Ignis_Entity.this.push(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                    } else {
                        Ignis_Entity.this.push(f1 * 1.5, 0, f2 * 1.5);
                    }
                }
                if (Ignis_Entity.this.getAnimationTick() == 33 && shouldFollowUp(3.0f) && Ignis_Entity.this.random.nextInt(2) == 0 && body_check_cooldown <= 0) {
                    body_check_cooldown = BODY_CHECK_COOLDOWN;
                    AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK4);
                }

                if (Ignis_Entity.this.getAnimationTick() > 30 || Ignis_Entity.this.getAnimationTick() < 24) {
                    Ignis_Entity.this.setDeltaMovement(0, Ignis_Entity.this.getDeltaMovement().y, 0);
                }
            }
        }
    }

    class Poked extends Goal {

        public Poked() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == POKED_ATTACK;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getTarget();
            if (target != null) {
                Ignis_Entity.this.getLookControl().setLookAt(target, 20.0F, 20.0F);
            }
            Ignis_Entity.this.setDeltaMovement(0, Ignis_Entity.this.getDeltaMovement().y, 0);
        }
    }

    class Phase_Transition extends Goal {

        public Phase_Transition() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == PHASE_2;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getTarget();
            if (Ignis_Entity.this.getAnimationTick() < 34 && target != null || Ignis_Entity.this.getAnimationTick() > 54 && target != null) {
                Ignis_Entity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.setYRot(Ignis_Entity.this.yRotO);
            }
            Ignis_Entity.this.setDeltaMovement(0, Ignis_Entity.this.getDeltaMovement().y, 0);
        }
    }

    class Shield_Smash extends Goal {

        public Shield_Smash() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == SHIELD_SMASH_ATTACK;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getTarget();
            if (Ignis_Entity.this.getAnimationTick() < 34 && target != null) {
                Ignis_Entity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.setYRot(Ignis_Entity.this.yRotO);
            }
            Ignis_Entity.this.setDeltaMovement(0, Ignis_Entity.this.getDeltaMovement().y, 0);

            if (Ignis_Entity.this.getAnimationTick() == 45 && shouldFollowUp(4.0f) && Ignis_Entity.this.random.nextInt(3) == 0 && body_check_cooldown <= 0) {
                body_check_cooldown = BODY_CHECK_COOLDOWN;
                AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK3);
            }
        }
    }

    class Air_Smash extends Goal {

        public Air_Smash() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == SMASH_IN_AIR;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getTarget();
            if (target != null) {
                Ignis_Entity.this.lookAt(target, 30.0F, 30.0F);
            }
            if (Ignis_Entity.this.getAnimationTick() == 19) {
                if (target != null) {
                    Ignis_Entity.this.setDeltaMovement((target.getX() - Ignis_Entity.this.getX()) * 0.2D, 1.4D, (target.getZ() - Ignis_Entity.this.getZ()) * 0.2D);
                }else{
                    Ignis_Entity.this.setDeltaMovement(0, 1.4D, 0);
                }
            }

            if (Ignis_Entity.this.getAnimationTick() > 19 && Ignis_Entity.this.isOnGround()){
                AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, SMASH);
            }

        }
    }

    class Smash extends Goal {

        public Smash() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == SMASH;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
           // Ignis_Entity.this.setDeltaMovement(0, Ignis_Entity.this.getDeltaMovement().y, 0);
        }
    }

    class Body_Check extends Goal {

        public Body_Check() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK1
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK2
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK3
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK4;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getTarget();
            if (Ignis_Entity.this.getAnimationTick() < 25 && target != null) {
                Ignis_Entity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.setYRot(Ignis_Entity.this.yRotO);
            }
            if (Ignis_Entity.this.getAnimationTick() == 20 && target != null){
                Ignis_Entity.this.setDeltaMovement((target.getX() - Ignis_Entity.this.getX()) * 0.25D, 0, (target.getZ() - Ignis_Entity.this.getZ()) * 0.25D);

            }

        }
    }

    class Vertical_Swing extends Goal {

        public Vertical_Swing() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == SWING_ATTACK;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getTarget();
            if (Ignis_Entity.this.getAnimationTick() < 34 && target != null) {
                Ignis_Entity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.setYRot(Ignis_Entity.this.yRotO);
            }
        }
    }
    class Combo_Swing extends Goal {

        public Combo_Swing() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return Ignis_Entity.this.getAnimation() == COMBO_SWING_ATTACK;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getTarget();
            if (Ignis_Entity.this.getAnimationTick() < 34 && target != null) {
                Ignis_Entity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.setYRot(Ignis_Entity.this.yRotO);
            }
        }
    }

}

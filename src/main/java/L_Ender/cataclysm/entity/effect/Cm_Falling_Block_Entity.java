package L_Ender.cataclysm.entity.effect;

import L_Ender.cataclysm.cataclysm;
import L_Ender.cataclysm.init.ModEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class Cm_Falling_Block_Entity extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private BlockState blockState = Blocks.SAND.defaultBlockState();
    public int time;
    public boolean dropItem = true;
    private boolean cancelDrop;
    private boolean hurtEntities;
    private int fallDamageMax = 40;
    private float fallDamagePerDistance;
    @Nullable
    public CompoundTag blockData;
    protected static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData.defineId(net.minecraft.world.entity.item.FallingBlockEntity.class, EntityDataSerializers.BLOCK_POS);

    public Cm_Falling_Block_Entity(EntityType<Cm_Falling_Block_Entity> type, Level level) {
        super(type, level);
    }

    public Cm_Falling_Block_Entity(Level p_31953_, double p_31954_, double p_31955_, double p_31956_, BlockState p_31957_) {
        this(ModEntities.CM_FALLING_BLOCK.get(), p_31953_);
        this.blockState = p_31957_;
        this.blocksBuilding = true;
        this.setPos(p_31954_, p_31955_ + (double)((1.0F - this.getBbHeight()) / 2.0F), p_31956_);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = p_31954_;
        this.yo = p_31955_;
        this.zo = p_31956_;
        this.setStartPos(this.blockPosition());
    }


    public boolean isAttackable() {
        return false;
    }

    public void setStartPos(BlockPos p_31960_) {
        this.entityData.set(DATA_START_POS, p_31960_);
    }

    public BlockPos getStartPos() {
        return this.entityData.get(DATA_START_POS);
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    protected void defineSynchedData() {
        this.entityData.define(DATA_START_POS, BlockPos.ZERO);
    }

    public boolean isPickable() {
        return !this.isRemoved();
    }

    public void tick() {
        if (this.blockState.isAir()) {
            this.discard();
        } else {
            Block block = this.blockState.getBlock();
            ++this.time;
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            if (!this.level.isClientSide) {
                BlockPos blockpos = this.blockPosition();
                boolean flag = this.blockState.getBlock() instanceof ConcretePowderBlock;
                boolean flag1 = flag && this.level.getFluidState(blockpos).is(FluidTags.WATER);
                double d0 = this.getDeltaMovement().lengthSqr();
                if (flag && d0 > 1.0D) {
                    BlockHitResult blockhitresult = this.level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
                    if (blockhitresult.getType() != HitResult.Type.MISS && this.level.getFluidState(blockhitresult.getBlockPos()).is(FluidTags.WATER)) {
                        blockpos = blockhitresult.getBlockPos();
                        flag1 = true;
                    }
                }

                if (!this.onGround && !flag1) {
                    if (!this.level.isClientSide && (this.time > 100 && (blockpos.getY() <= this.level.getMinBuildHeight() || blockpos.getY() > this.level.getMaxBuildHeight()) || this.time > 600)) {
                        if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.spawnAtLocation(block);
                        }

                        this.discard();
                    }
                } else {
                    BlockState blockstate = this.level.getBlockState(blockpos);
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                    if (!blockstate.is(Blocks.MOVING_PISTON)) {
                        if (!this.cancelDrop) {
                            boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                            boolean flag3 = FallingBlock.isFree(this.level.getBlockState(blockpos.below())) && (!flag || !flag1);
                            boolean flag4 = this.blockState.canSurvive(this.level, blockpos) && !flag3;
                            if (flag2 && flag4) {
                                if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState(blockpos).getType() == Fluids.WATER) {
                                    this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
                                }

                                if (this.level.setBlock(blockpos, this.blockState, 3)) {
                                    ((ServerLevel)this.level).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket(blockpos, this.level.getBlockState(blockpos)));
                                    this.discard();
                                    if (this.blockData != null && this.blockState.hasBlockEntity()) {
                                        BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                                        if (blockentity != null) {
                                            CompoundTag compoundtag = blockentity.saveWithoutMetadata();

                                            for(String s : this.blockData.getAllKeys()) {
                                                compoundtag.put(s, this.blockData.get(s).copy());
                                            }

                                            try {
                                                blockentity.load(compoundtag);
                                            } catch (Exception exception) {
                                                cataclysm.LOGGER.error("Failed to load block entity from falling block", (Throwable)exception);
                                            }

                                            blockentity.setChanged();
                                        }
                                    }
                                } else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.discard();
                                    this.spawnAtLocation(block);
                                }
                            } else {
                                this.discard();
                                if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.spawnAtLocation(block);
                                }
                            }
                        } else {
                            this.discard();
                        }
                    }
                }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }
    }



    public boolean causeFallDamage(float p_149643_, float p_149644_, DamageSource p_149645_) {
        return false;
    }

    protected void addAdditionalSaveData(CompoundTag p_31973_) {
        p_31973_.put("BlockState", NbtUtils.writeBlockState(this.blockState));
        p_31973_.putInt("Time", this.time);
        p_31973_.putBoolean("DropItem", this.dropItem);
        p_31973_.putBoolean("HurtEntities", this.hurtEntities);
        p_31973_.putFloat("FallHurtAmount", this.fallDamagePerDistance);
        p_31973_.putInt("FallHurtMax", this.fallDamageMax);
        if (this.blockData != null) {
            p_31973_.put("TileEntityData", this.blockData);
        }

    }

    protected void readAdditionalSaveData(CompoundTag p_31964_) {
        this.blockState = NbtUtils.readBlockState(p_31964_.getCompound("BlockState"));
        this.time = p_31964_.getInt("Time");
        if (p_31964_.contains("HurtEntities", 99)) {
            this.hurtEntities = p_31964_.getBoolean("HurtEntities");
            this.fallDamagePerDistance = p_31964_.getFloat("FallHurtAmount");
            this.fallDamageMax = p_31964_.getInt("FallHurtMax");
        } else if (this.blockState.is(BlockTags.ANVIL)) {
            this.hurtEntities = true;
        }

        if (p_31964_.contains("DropItem", 99)) {
            this.dropItem = p_31964_.getBoolean("DropItem");
        }

        if (p_31964_.contains("TileEntityData", 10)) {
            this.blockData = p_31964_.getCompound("TileEntityData");
        }

        if (this.blockState.isAir()) {
            this.blockState = Blocks.SAND.defaultBlockState();
        }

    }

    public void setHurtsEntities(float p_149657_, int p_149658_) {
        this.hurtEntities = true;
        this.fallDamagePerDistance = p_149657_;
        this.fallDamageMax = p_149658_;
    }

    public boolean displayFireAnimation() {
        return false;
    }

    public void fillCrashReportCategory(CrashReportCategory p_31962_) {
        super.fillCrashReportCategory(p_31962_);
        p_31962_.setDetail("Immitating BlockState", this.blockState.toString());
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.getBlockState()));
    }

    public void recreateFromPacket(ClientboundAddEntityPacket p_149654_) {
        super.recreateFromPacket(p_149654_);
        this.blockState = Block.stateById(p_149654_.getData());
        this.blocksBuilding = true;
        double d0 = p_149654_.getX();
        double d1 = p_149654_.getY();
        double d2 = p_149654_.getZ();
        this.setPos(d0, d1, d2);
        this.setStartPos(this.blockPosition());
    }
}
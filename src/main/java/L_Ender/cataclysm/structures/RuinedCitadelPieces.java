package L_Ender.cataclysm.structures;

import L_Ender.cataclysm.cataclysm;
import L_Ender.cataclysm.entity.Ender_Golem_Entity;
import L_Ender.cataclysm.entity.Netherite_Monstrosity_Entity;
import L_Ender.cataclysm.init.ModEntities;
import L_Ender.cataclysm.init.ModStructures;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RuinedCitadelPieces {

    private static final ResourceLocation CITADEL1 = new ResourceLocation(cataclysm.MODID, "ruined_citadel1");
    private static final ResourceLocation CITADEL2 = new ResourceLocation(cataclysm.MODID, "ruined_citadel2");
    private static final ResourceLocation CITADEL3 = new ResourceLocation(cataclysm.MODID, "ruined_citadel3");
    private static final ResourceLocation CITADEL4 = new ResourceLocation(cataclysm.MODID, "ruined_citadel4");
    private static final ResourceLocation CITADEL5 = new ResourceLocation(cataclysm.MODID, "ruined_citadel5");
    private static final ResourceLocation CITADEL6 = new ResourceLocation(cataclysm.MODID, "ruined_citadel6");
    private static final ResourceLocation CITADEL7 = new ResourceLocation(cataclysm.MODID, "ruined_citadel7");
    private static final ResourceLocation CITADEL8 = new ResourceLocation(cataclysm.MODID, "ruined_citadel8");
    private static final ResourceLocation CITADEL9 = new ResourceLocation(cataclysm.MODID, "ruined_citadel9");
    private static final ResourceLocation CITADEL10 = new ResourceLocation(cataclysm.MODID, "ruined_citadel10");
    private static final ResourceLocation CITADEL11 = new ResourceLocation(cataclysm.MODID, "ruined_citadel11");
    private static final ResourceLocation CITADEL12 = new ResourceLocation(cataclysm.MODID, "ruined_citadel12");
    private static final ResourceLocation CITADEL13 = new ResourceLocation(cataclysm.MODID, "ruined_citadel13");
    private static final ResourceLocation CITADEL14 = new ResourceLocation(cataclysm.MODID, "ruined_citadel14");
    private static final ResourceLocation CITADEL15 = new ResourceLocation(cataclysm.MODID, "ruined_citadel15");
    private static final ResourceLocation CITADEL16 = new ResourceLocation(cataclysm.MODID, "ruined_citadel16");
    private static final ResourceLocation CITADEL17 = new ResourceLocation(cataclysm.MODID, "ruined_citadel17");
    private static final ResourceLocation CITADEL18 = new ResourceLocation(cataclysm.MODID, "ruined_citadel18");


    private static final Map<ResourceLocation, BlockPos> OFFSET = ImmutableMap.<ResourceLocation, BlockPos>builder()
            .put(CITADEL1, new BlockPos(0, 1, 0))
            .put(CITADEL2, new BlockPos(0, 1, 0))
            .put(CITADEL3, new BlockPos(0, 1, 0))
            .put(CITADEL4, new BlockPos(0, 1, 0))
            .put(CITADEL5, new BlockPos(0, 1, 0))
            .put(CITADEL6, new BlockPos(0, 1, 0))
            .put(CITADEL7, new BlockPos(0, 1, 0))
            .put(CITADEL8, new BlockPos(0, 1, 0))
            .put(CITADEL9, new BlockPos(0, 1, 0))
            .put(CITADEL10, new BlockPos(0, 1, 0))
            .put(CITADEL11, new BlockPos(0, 1, 0))
            .put(CITADEL12, new BlockPos(0, 1, 0))
            .put(CITADEL13, new BlockPos(0, 1, 0))
            .put(CITADEL14, new BlockPos(0, 1, 0))
            .put(CITADEL15, new BlockPos(0, 1, 0))
            .put(CITADEL16, new BlockPos(0, 1, 0))
            .put(CITADEL17, new BlockPos(0, 1, 0))
            .put(CITADEL18, new BlockPos(0, 1, 0))
            .build();

    /*
     * Begins assembling your structure and where the pieces needs to go.
     */
    public static void start(StructureManager templateManager, BlockPos pos, Rotation rotation, StructurePieceAccessor pieceList, Random random) {
        int x = pos.getX();
        int z = pos.getZ();


        BlockPos rotationOffSet = new BlockPos(0, -45, 0).rotate(rotation);
        BlockPos blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL5, blockpos, rotation));

        rotationOffSet = new BlockPos(0, 0, 0).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL14, blockpos, rotation));


        rotationOffSet = new BlockPos(0, -45, 37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL6, blockpos, rotation));

        rotationOffSet = new BlockPos(0, 0, 37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL15, blockpos, rotation));


        rotationOffSet = new BlockPos(0, -45, -37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL4, blockpos, rotation));

        rotationOffSet = new BlockPos(0, 0, -37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL13, blockpos, rotation));


        rotationOffSet = new BlockPos(-36, -45, 0).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL2, blockpos, rotation));

        rotationOffSet = new BlockPos(-36, 0, 0).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL11, blockpos, rotation));


        rotationOffSet = new BlockPos(36, -45, 0).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL8, blockpos, rotation));

        rotationOffSet = new BlockPos(36, 0, 0).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL17, blockpos, rotation));


        rotationOffSet = new BlockPos(-36, -45, -37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL1, blockpos, rotation));

        rotationOffSet = new BlockPos(-36, 0, -37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL10, blockpos, rotation));


        rotationOffSet = new BlockPos(-36, -45, 37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL3, blockpos, rotation));

        rotationOffSet = new BlockPos(-36, 0, 37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL12, blockpos, rotation));


        rotationOffSet = new BlockPos(36, -45, 37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL9, blockpos, rotation));

        rotationOffSet = new BlockPos(36, 0, 37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL18, blockpos, rotation));


        rotationOffSet = new BlockPos(36, -45, -37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL7, blockpos, rotation));

        rotationOffSet = new BlockPos(36, 0, -37).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.addPiece(new RuinedCitadelPieces.Piece(templateManager, CITADEL16, blockpos, rotation));


    }

    /*
     * Here's where some voodoo happens. Most of this doesn't need to be touched but you do
     * have to pass in the IStructurePieceType you registered into the super constructors.
     *
     * The method you will most likely want to touch is the handleDataMarker method.
     */
    public static class Piece extends TemplateStructurePiece {


        public Piece(StructureManager templateManagerIn, ResourceLocation resourceLocationIn, BlockPos pos, Rotation rotation) {
            super(ModStructures.RCP, 0, templateManagerIn, resourceLocationIn, resourceLocationIn.toString(), makeSettings(rotation), makePosition(resourceLocationIn, pos));
        }

        public Piece(StructureManager templateManagerIn, CompoundTag tagCompound) {
            super(ModStructures.RCP, tagCompound, templateManagerIn, (p_162451_) -> {
                return makeSettings(Rotation.valueOf(tagCompound.getString("Rot")));
            });

        }

        private static StructurePlaceSettings makeSettings(Rotation p_163156_) {
            BlockIgnoreProcessor blockignoreprocessor = BlockIgnoreProcessor.STRUCTURE_BLOCK;

            StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setRotation(p_163156_).setMirror(Mirror.NONE)
                    .addProcessor(blockignoreprocessor)
                    .addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE));


            return structureplacesettings;
        }

        private static BlockPos makePosition(ResourceLocation p_162453_, BlockPos p_162454_) {
            return p_162454_.offset(OFFSET.get(p_162453_));
        }

        protected void addAdditionalSaveData(StructurePieceSerializationContext p_162444_, CompoundTag tagCompound) {
            super.addAdditionalSaveData(p_162444_, tagCompound);
            tagCompound.putString("Rot", this.placeSettings.getRotation().name());
        }

        @Override
        protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, Random rand, BoundingBox sbb) {
            if (sbb.isInside(pos) && Level.isInSpawnableBounds(pos)) {
                if (function.startsWith("sentry")) {
                    worldIn.setBlock(pos,Blocks.AIR.defaultBlockState(), 2);
                    Shulker shulker = EntityType.SHULKER.create(worldIn.getLevel());
                    shulker.setPos((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
                    worldIn.addFreshEntity(shulker);
                } else if (function.startsWith("mimic")) {
                    worldIn.setBlock(pos,Blocks.AIR.defaultBlockState(), 2);
                    Shulker Silentshulkerentity = EntityType.SHULKER.create(worldIn.getLevel());
                    Silentshulkerentity.setPos((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
                    Silentshulkerentity.setSilent(true);
                    worldIn.addFreshEntity(Silentshulkerentity);
                } else if ("golem".equals(function)) {
                    Ender_Golem_Entity golem = ModEntities.ENDER_GOLEM.get().create(worldIn.getLevel());
                    worldIn.setBlock(pos,Blocks.AIR.defaultBlockState(), 2);
                    golem.moveTo(pos, 180.0F, 180.0F);
                    worldIn.addFreshEntity(golem);
                }
            }
        }
    }
}

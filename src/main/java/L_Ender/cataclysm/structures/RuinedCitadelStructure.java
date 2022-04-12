package L_Ender.cataclysm.structures;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class RuinedCitadelStructure extends StructureFeature<NoneFeatureConfiguration> {
    public RuinedCitadelStructure(Codec<NoneFeatureConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(RuinedCitadelStructure::checkLocation, RuinedCitadelStructure::generatePieces));
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    private static boolean checkLocation(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> p_197134_) {
        ChunkPos chunkpos = p_197134_.chunkPos();
        int i = p_197134_.chunkPos().x >> 4;
        int j = p_197134_.chunkPos().z >> 4;

        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setSeed((long) (i ^ j << 4) ^ p_197134_.seed());
        worldgenrandom.nextInt();
        return p_197134_.validBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG);
    }


    private static void generatePieces(StructurePiecesBuilder p_197233_, PieceGenerator.Context<NoneFeatureConfiguration> p_197234_) {
        BlockPos blockpos = new BlockPos(p_197234_.chunkPos().getMinBlockX(), 53, p_197234_.chunkPos().getMinBlockZ());
        Rotation rotation = Rotation.getRandom(p_197234_.random());
        RuinedCitadelPieces.start(p_197234_.structureManager(), blockpos, rotation, p_197233_, p_197234_.random());
    }

}
package L_Ender.cataclysm.init;

import L_Ender.cataclysm.cataclysm;
import L_Ender.cataclysm.structures.RuinedCitadelPieces;
import L_Ender.cataclysm.structures.RuinedCitadelStructure;
import L_Ender.cataclysm.structures.SoulBlackSmithPieces;
import L_Ender.cataclysm.structures.SoulBlackSmithStructure;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = cataclysm.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModStructures {

    public static final StructureFeature<NoneFeatureConfiguration> SOUL_BLACK_SMITH = new SoulBlackSmithStructure(NoneFeatureConfiguration.CODEC);
    public static final StructureFeature<NoneFeatureConfiguration> RUINED_CITADEL = new RuinedCitadelStructure(NoneFeatureConfiguration.CODEC);
    public static StructurePieceType SBSP;
    public static StructurePieceType RCP;

    @SubscribeEvent
    public static void registerFeature(RegistryEvent.Register<StructureFeature<?>> registry) {
        registry.getRegistry().register(SOUL_BLACK_SMITH.setRegistryName("soul_black_smith"));
        registry.getRegistry().register(RUINED_CITADEL.setRegistryName("ruined_citadel"));
    }

    public static void init() {
        SBSP = setPieceId(SoulBlackSmithPieces.Piece::new, cataclysm.MODID + "soul_black_smith");
        RCP = setPieceId(RuinedCitadelPieces.Piece::new, cataclysm.MODID + "ruined_citadel");
    }


    static StructurePieceType setPieceId(StructurePieceType.StructureTemplateType p_67164_, String p_67165_) {
        return Registry.register(Registry.STRUCTURE_PIECE, p_67165_.toLowerCase(Locale.ROOT), p_67164_);
    }


}

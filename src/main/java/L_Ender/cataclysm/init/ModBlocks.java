package L_Ender.cataclysm.init;


import L_Ender.cataclysm.cataclysm;
import L_Ender.cataclysm.blocks.BlockEnderGuardianSpawner;
import L_Ender.cataclysm.blocks.EndStoneTeleportTrapBricks;
import L_Ender.cataclysm.blocks.ObsidianExplosionTrapBricks;
import L_Ender.cataclysm.blocks.PurpurVoidRuneTrapBlock;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.ToIntFunction;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            cataclysm.MODID);

    public static final RegistryObject<Block> WITHERITE_BLOCK = BLOCKS.register("witherite_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                    .strength(50f, 1200f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> ENDERRITE_BLOCK = BLOCKS.register("enderite_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
                    .strength(50f, 1200f)
                    .sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> IGNITIUM_BLOCK = BLOCKS.register("ignitium_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                    .strength(50f, 1200f)
                    .sound(SoundType.NETHERITE_BLOCK).lightLevel((state) -> {
                        return 15;
                    })));

    public static final RegistryObject<Block> POLISHED_END_STONE = BLOCKS.register("polished_end_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.END_STONE)));

    public static final RegistryObject<Block> POLISHED_END_STONE_SLAB = BLOCKS.register("polished_end_stone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(POLISHED_END_STONE.get())));

    public static final RegistryObject<Block> POLISHED_END_STONE_STAIRS = BLOCKS.register("polished_end_stone_stairs",
            () -> new StairBlock(POLISHED_END_STONE.get().defaultBlockState(),BlockBehaviour.Properties.copy(POLISHED_END_STONE.get())));


    public static final RegistryObject<Block> CHISELED_END_STONE_BRICKS = BLOCKS.register("chiseled_end_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.END_STONE_BRICKS)));

    public static final RegistryObject<Block> VOID_INFUSED_END_STONE_BRICKS = BLOCKS.register("void_infused_end_stone_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.END_STONE_BRICKS)
                    .lightLevel((state) -> {
                        return 7;
                    })));

    public static final RegistryObject<Block> VOID_STONE = BLOCKS.register("void_stone",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_PURPLE)
                    .requiresCorrectToolForDrops()
                    .strength(50f, 1200f).lightLevel((state) -> {
                        return 7;
                    })));

    public static final RegistryObject<Block> VOID_LANTERN_BLOCK = BLOCKS.register("void_lantern_block" ,
            () -> new Block(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.QUARTZ)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
            .strength(50f, 1200f)
            .lightLevel((state) -> {
        return 15;
    })));

    public static final RegistryObject<Block> END_STONE_PILLAR = BLOCKS.register("end_stone_pillar",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND)
                    .requiresCorrectToolForDrops()
                    .strength(3f, 9f)));

    public static final RegistryObject<Block> CHISELED_PURPUR_BLOCK = BLOCKS.register("chiseled_purpur_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.PURPUR_BLOCK)));

    public static final RegistryObject<Block> OBSIDIAN_BRICKS = BLOCKS.register("obsidian_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)));

    public static final RegistryObject<Block> CHISELED_OBSIDIAN_BRICKS = BLOCKS.register("chiseled_obsidian_bricks",
            () -> new Block(BlockBehaviour.Properties.copy(OBSIDIAN_BRICKS.get())));

    public static final RegistryObject<Block> OBSIDIAN_BRICK_SLAB = BLOCKS.register("obsidian_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(OBSIDIAN_BRICKS.get())));

    public static final RegistryObject<Block> OBSIDIAN_BRICK_STAIRS = BLOCKS.register("obsidian_brick_stairs",
            () -> new StairBlock(OBSIDIAN_BRICKS.get().defaultBlockState(),BlockBehaviour.Properties.copy(OBSIDIAN_BRICKS.get())));

    public static final RegistryObject<Block> OBSIDIAN_BRICK_WALL = BLOCKS.register("obsidian_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(OBSIDIAN_BRICKS.get())));

    public static final RegistryObject<Block> PURPUR_WALL = BLOCKS.register("purpur_wall",
            () -> new WallBlock(BlockBehaviour.Properties.copy(Blocks.PURPUR_BLOCK)));

    public static final RegistryObject<Block> PURPUR_VOID_RUNE_TRAP_BLOCK = BLOCKS.register("purpur_void_rune_trap_block",
            () -> new PurpurVoidRuneTrapBlock(BlockBehaviour.Properties.copy(Blocks.PURPUR_BLOCK).randomTicks().lightLevel(getLightValueLit(7))));

    public static final RegistryObject<Block> END_STONE_TELEPORT_TRAP_BRICKS = BLOCKS.register("end_stone_teleport_trap_bricks",
            () -> new EndStoneTeleportTrapBricks(BlockBehaviour.Properties.copy(Blocks.END_STONE_BRICKS).randomTicks().lightLevel(getLightValueLit(7))));

    public static final RegistryObject<Block> OBSIDIAN_EXPLOSION_TRAP_BRICKS = BLOCKS.register("obsidian_explosion_trap_bricks",
            () -> new ObsidianExplosionTrapBricks(BlockBehaviour.Properties.copy(OBSIDIAN_BRICKS.get()).randomTicks().lightLevel(getLightValueLit(7))));

    public static final RegistryObject<Block> ENDER_GUARDIAN_SPAWNER = BLOCKS.register("ender_guardian_spawner",
            () -> new BlockEnderGuardianSpawner(BlockBehaviour.Properties.of(Material.STONE)
                    .strength(-1.0F, 3600000.0F)
                    .noDrops()
                    .noOcclusion()
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> CHORUS_PLANKS = BLOCKS.register("chorus_planks",
            () -> new Block(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MaterialColor.COLOR_PURPLE).
                    strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)));

    public static final RegistryObject<Block> CHORUS_SLAB = BLOCKS.register("chorus_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(CHORUS_PLANKS.get())));

    public static final RegistryObject<Block> CHORUS_STAIRS = BLOCKS.register("chorus_stairs",
            () -> new StairBlock(CHORUS_PLANKS.get().defaultBlockState(),BlockBehaviour.Properties.copy(CHORUS_PLANKS.get())));
    
    public static final RegistryObject<Block> CHORUS_FENCE = BLOCKS.register("chorus_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(CHORUS_PLANKS.get())));


    private static ToIntFunction<BlockState> getLightValueLit(int lightValue) {
        return (state) -> {
            return state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
        };
    }

}

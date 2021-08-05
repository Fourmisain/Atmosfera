package dev.hephaestus.atmosfera.world.context;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Hemisphere extends AbstractEnvironmentContext {
    private final byte[][] offsets;

    private final Map<Block, Integer> blockTypes = new ConcurrentHashMap<>();
    private final Map<Tag<Block>, Integer> blockTags = new ConcurrentHashMap<>();
    private final Map<Biome, Integer> biomeTypes = new ConcurrentHashMap<>();
    private final Map<Tag<Biome>, Integer> biomeTags = new ConcurrentHashMap<>();
    private final Map<Biome.Category, Integer> biomeCategories = new ConcurrentHashMap<>();

    private int blockCount = 0;
    private int skyVisibility = 0;

    Hemisphere(byte[][] offsets) {
        this.offsets = offsets;
    }

    @Override
    public float getBlockTypePercentage(Block block) {
        return this.blockTypes.getOrDefault(block, 0) / (float) this.blockCount;
    }

    @Override
    public float getBlockTagPercentage(Tag<Block> blocks) {
        return this.blockTags.getOrDefault(blocks, 0) / (float) this.blockCount;
    }

    @Override
    public float getBiomePercentage(Biome biome) {
        return this.biomeTypes.getOrDefault(biome, 0) / (float) this.blockCount;
    }

    @Override
    public float getBiomeTagPercentage(Tag<Biome> biomes) {
        return this.biomeTags.getOrDefault(biomes, 0) / (float) this.blockCount;
    }

    @Override
    public float getBiomeCategoryPercentage(Biome.Category biomes) {
        return this.biomeCategories.getOrDefault(biomes, 0) / (float) this.blockCount;
    }

    @Override
    public float getSkyVisibility() {
        return this.skyVisibility / (float) this.blockCount;
    }

    private void clear() {
        this.blockCount = 0;
        this.skyVisibility = 0;
        this.blockTypes.clear();
        this.blockTags.clear();
        this.biomeTypes.clear();
        this.biomeTags.clear();
        this.biomeCategories.clear();
    }

    private void add(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        this.blockTypes.merge(block, 1, Integer::sum);

        for (Identifier tag : world.getTagManager().getOrCreateTagGroup(Registry.BLOCK_KEY).getTagsFor(block)) {
            this.blockTags.merge(TagRegistry.block(tag), 1, Integer::sum);
        }

        Biome biome = world.getBiome(pos);
        TagGroup<Biome> biomeTags = world.getTagManager().getOrCreateTagGroup(Registry.BIOME_KEY);

        for (Identifier tag : biomeTags.getTagsFor(biome)) {
            this.biomeTags.merge(TagRegistry.create(tag, () -> biomeTags), 1, Integer::sum);
        }

        this.biomeTypes.merge(biome, 1, Integer::sum);
        this.biomeCategories.merge(biome.getCategory(), 1, Integer::sum);
        this.skyVisibility += world.isSkyVisible(pos) ? 1 : 0;
        this.blockCount++;
    }

    void update(ClientPlayerEntity player, BlockPos center) {
        this.clear();

        BlockPos.Mutable mut = new BlockPos.Mutable();
        World world = player.world;

        for (int i = 0; i < this.offsets.length; ++i) {
            byte[] a = this.offsets[i];

            mut.set(center.getX() + a[0], center.getY() + a[1], center.getZ() + a[2]);
            this.add(world, mut);
        }
    }
}

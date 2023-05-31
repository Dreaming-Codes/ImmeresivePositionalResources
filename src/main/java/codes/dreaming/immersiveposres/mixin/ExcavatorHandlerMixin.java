package codes.dreaming.immersiveposres.mixin;

import blusunrize.immersiveengineering.api.excavator.ExcavatorHandler;
import blusunrize.immersiveengineering.api.excavator.MineralMix;
import blusunrize.immersiveengineering.api.excavator.MineralVein;
import blusunrize.immersiveengineering.api.utils.SetRestrictedField;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

import static blusunrize.immersiveengineering.api.excavator.ExcavatorHandler.*;

@Mixin(ExcavatorHandler.class)
public class ExcavatorHandlerMixin {

    @Final
    @Shadow(remap = false)
    private static Multimap<ResourceKey<Level>, MineralVein> MINERAL_VEIN_LIST;

    @Final
    @Shadow(remap = false)
    static SetRestrictedField<Runnable> MARK_SAVE_DATA_DIRTY;

    @Inject(at = @At("HEAD"), method = "generatePotentialVein(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/util/RandomSource;)V", cancellable = true, remap = false)
    private static void generatePotentialVein(Level world, ChunkPos chunkpos, RandomSource rand, CallbackInfo ci) {
        ci.cancel();

        int xStart = chunkpos.getMinBlockX();
        int zStart = chunkpos.getMinBlockZ();
        double d0 = 0.0625;
        ColumnPos pos = null;
        double maxNoise = 0.0;

        for(int xx = 0; xx < 16; ++xx) {
            for(int zz = 0; zz < 16; ++zz) {
                double noise = noiseGenerator.getValue((double)(xStart + xx) * d0, (double)(zStart + zz) * d0, true) * 0.55;
                double chance = Math.abs(noise) / 0.55;
                if (chance > mineralNoiseThreshold && chance > maxNoise) {
                    pos = new ColumnPos(xStart + xx, zStart + zz);
                    maxNoise = chance;
                }
            }
        }

        if (pos != null) {
            synchronized(MINERAL_VEIN_LIST) {
                int radius = 12 + rand.nextInt(32);
                int radiusSq = radius * radius;
                ColumnPos finalPos = pos;
                boolean crossover = MINERAL_VEIN_LIST.get(world.dimension()).stream().anyMatch((veinx) -> {
                    long dX = (long)(veinx.getPos().x() - finalPos.x());
                    long dZ = (long)(veinx.getPos().z() - finalPos.z());
                    long dSq = dX * dX + dZ * dZ;
                    return dSq < (long)(veinx.getRadius() * veinx.getRadius()) || dSq < (long)radiusSq;
                });
                if (!crossover) {
                    MineralMix mineralMix = null;
                    ExcavatorHandler.MineralSelection selection = new ExcavatorHandler.MineralSelection(world);
                    if (selection.getTotalWeight() > 0) {
                        int weight = selection.getRandomWeight(rand);
                        Iterator var18 = selection.getMinerals().iterator();

                        while(var18.hasNext()) {
                            MineralMix e = (MineralMix)var18.next();
                            weight -= e.weight;
                            if (weight < 0) {
                                mineralMix = e;
                                break;
                            }
                        }
                    }

                    if (mineralMix != null) {
                        MineralVein vein = new MineralVein(pos, mineralMix.getId(), radius);
                        if (initialVeinDepletion > 0.0) {
                            vein.setDepletion((int)((double)mineralVeinYield * rand.nextDouble() * initialVeinDepletion));
                        }

                        addVein(world.dimension(), vein);
                        ((Runnable)MARK_SAVE_DATA_DIRTY.getValue()).run();
                    }
                }
            }
        }

    }
}

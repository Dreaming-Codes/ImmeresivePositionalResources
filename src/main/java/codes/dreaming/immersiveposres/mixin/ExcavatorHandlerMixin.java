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
        double d0 = 0.0625D;
        ColumnPos pos = null;
        double maxNoise = 0;

        // Find highest noise value in chunk
        for(int xx = 0; xx < 16; ++xx)
            for(int zz = 0; zz < 16; ++zz)
            {
                double noise = noiseGenerator.getValue((xStart+xx)*d0, (zStart+zz)*d0, true)*0.55D;
                // Vanilla Perlin noise scales to 0.55, so we un-scale it
                double chance = Math.abs(noise)/.55;
                if(chance > mineralNoiseThreshold&&chance > maxNoise)
                {
                    pos = new ColumnPos(xStart+xx, zStart+zz);
                    maxNoise = chance;
                }
            }

        if(pos!=null)
            synchronized(MINERAL_VEIN_LIST)
            {
                ColumnPos finalPos = pos;
                int radius = 12+rand.nextInt(32);
                int radiusSq = radius*radius;
                boolean crossover = MINERAL_VEIN_LIST.get(world.dimension()).stream().anyMatch(vein -> {
                    // Use longs to prevent overflow
                    long dX = vein.getPos().x()-finalPos.x();
                    long dZ = vein.getPos().z()-finalPos.z();
                    long dSq = dX*dX+dZ*dZ;
                    return dSq < vein.getRadius()*vein.getRadius()||dSq < radiusSq;
                });
                if(!crossover)
                {
                    MineralMix mineralMix = null;
                    MineralSelection selection = new MineralSelection(world);
                    if(selection.getTotalWeight() > 0)
                    {
                        int weight = selection.getRandomWeight(rand);
                        for(MineralMix e : selection.getMinerals())
                        {
                            weight -= e.weight;
                            if(weight < 0)
                            {
                                mineralMix = e;
                                break;
                            }
                        }
                    }
                    if(mineralMix!=null)
                    {
                        MineralVein vein = new MineralVein(pos, mineralMix.getId(), radius);
                        // generate initial depletion
                        if(initialVeinDepletion > 0)
                            vein.setDepletion((int)(mineralVeinYield*(rand.nextDouble()*initialVeinDepletion)));
                        addVein(world.dimension(), vein);
                        MARK_SAVE_DATA_DIRTY.getValue().run();
                    }
                }
            }
    }
}

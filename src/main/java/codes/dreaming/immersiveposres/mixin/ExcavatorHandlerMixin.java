package codes.dreaming.immersiveposres.mixin;

import blusunrize.immersiveengineering.api.excavator.ExcavatorHandler;
import codes.dreaming.immersiveposres.ImmersivePositionalResources;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExcavatorHandler.class)
public class ExcavatorHandlerMixin {

    @Inject(at = @At("RETURN"), method = "generatePotentialVein(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/util/RandomSource;)V", cancellable = true, remap = false)
    private static void generatePotentialVein(Level world, ChunkPos chunkpos, RandomSource rand, CallbackInfo ci) {
        ImmersivePositionalResources.LOGGER.info("Hello from generatePotentialVein!");
    }
}

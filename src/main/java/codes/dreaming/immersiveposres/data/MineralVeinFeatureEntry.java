package codes.dreaming.immersiveposres.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

//Thanks to https://github.com/screret for the help with this
public class MineralVeinFeatureEntry {
    public static final Map<ResourceLocation, MineralVeinFeatureEntry> ALL = new HashMap<>();
    public final ResourceLocation id;
    public static final Codec<MineralVeinFeatureEntry> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(field -> field.id),
                    Codec.DOUBLE.fieldOf("minDistance").forGetter(field -> field.minDistance),
                    Codec.DOUBLE.fieldOf("maxDistance").forGetter(field -> field.maxDistance)
            ).apply(instance, MineralVeinFeatureEntry::new)
    );
    public double minDistance;
    public double maxDistance;

    public MineralVeinFeatureEntry(ResourceLocation id, double minDistance, double maxDistance) {
        this.id = id;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }
}
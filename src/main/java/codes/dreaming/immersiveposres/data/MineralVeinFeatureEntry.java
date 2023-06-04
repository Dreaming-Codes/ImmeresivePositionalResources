package codes.dreaming.immersiveposres.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import static codes.dreaming.immersiveposres.ImmersivePositionalResources.MODID;

import java.util.HashMap;
import java.util.Map;

//Thanks to https://github.com/screret for the help with this
public class MineralVeinFeatureEntry {
    public static final Map<String, MineralVeinFeatureEntry> ALL = new HashMap<>();
    public final String id;
    public static final Codec<MineralVeinFeatureEntry> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("id").forGetter(field -> field.id),
                    Codec.DOUBLE.fieldOf("minDistance").forGetter(field -> field.minDistance),
                    Codec.DOUBLE.fieldOf("maxDistance").forGetter(field -> field.maxDistance)
            ).apply(instance, MineralVeinFeatureEntry::new)
    );
    public double minDistance;
    public double maxDistance;

    public MineralVeinFeatureEntry(String id, double minDistance, double maxDistance) {
        //remove modid from id namespace removing and the ':' after it
        this.id = id.substring(MODID.length() + 1);
        System.out.println("something strange id: " + this.id);
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }
}
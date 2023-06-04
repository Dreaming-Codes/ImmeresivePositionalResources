package codes.dreaming.immersiveposres.data.loader;

import codes.dreaming.immersiveposres.ImmersivePositionalResources;
import codes.dreaming.immersiveposres.data.MineralVeinFeatureEntry;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import org.jetbrains.annotations.NotNull;
import static codes.dreaming.immersiveposres.ImmersivePositionalResources.LOGGER;

import java.util.Map;

//Thanks to https://github.com/screret for the help with this
public class PositionalMineralVeinLoader extends SimpleJsonResourceReloadListener {
    public static PositionalMineralVeinLoader INSTANCE;
    public static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();
    private static final String FOLDER = ImmersivePositionalResources.MODID + "/positional_mineral_veins";

    public PositionalMineralVeinLoader() {
        super(GSON_INSTANCE, FOLDER);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        for(Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
            ResourceLocation location = entry.getKey();

            try {
                MineralVeinFeatureEntry ore = fromJson(location, GsonHelper.convertToJsonObject(entry.getValue(), "top element"));
                if (ore == null) {
                    LOGGER.info("Skipping loading vein {} as it's serializer returned null", location);
                    continue;
                }
                MineralVeinFeatureEntry.ALL.put(location, ore);
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                LOGGER.error("Parsing error loading recipe {}", location, jsonParseException);
            }
        }
    }

    public static MineralVeinFeatureEntry fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("id")) json.addProperty("id", id.toString());
        return MineralVeinFeatureEntry.CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst).getOrThrow(false, LOGGER::error);
    }
}

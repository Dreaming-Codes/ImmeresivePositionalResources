package codes.dreaming.immersiveposres;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.excavator.MineralMix;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.forge.RRPEvent;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ImmersivePositionalResources.MODID)
public class ImmersivePositionalResources {
    public static final String MODID = "immersiveposres";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final RuntimeResourcePack pack = RuntimeResourcePack.create(new ResourceLocation(MODID, "runtime_resources"));

    public static final Config CONFIG = new Config();

    public ImmersivePositionalResources() {
        CONFIG.registerConfig();

        LOGGER.info("Hello from ImmersivePositionalResources!");

        FMLJavaModLoadingContext.get().getModEventBus().addListener((RRPEvent.BeforeUser event) -> {
            event.addPack(pack);
        });

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarted(final ServerStartedEvent event) throws IOException, InterruptedException {
        byte[] emptyMix = event.getServer().getResourceManager().getResource(new ResourceLocation(MODID, "empty_mix.json")).get().open().readAllBytes();

        pack.clearResources();

        List<MineralMix> mineralMixesToRemove = StreamSupport.stream(event.getServer().getAllLevels().spliterator(), true)
                .flatMap(level -> MineralMix.RECIPES.getRecipes(level).stream())
                .distinct()
                .toList();

        mineralMixesToRemove.forEach((mix) -> {
            pack.addRecipe(mix.getId(), emptyMix);
            LOGGER.info("Found Mineral Mix: " + mix.getId().getPath() + " Removing it from the world." + mix.getId().getNamespace());
        });

        pack.regenerate();
        pack.dumpToDefaultPath();
    }
}

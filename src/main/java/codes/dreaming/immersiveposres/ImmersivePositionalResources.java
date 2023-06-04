package codes.dreaming.immersiveposres;

import codes.dreaming.immersiveposres.data.loader.PositionalMineralVeinLoader;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ImmersivePositionalResources.MODID)
public class ImmersivePositionalResources {
    public static final String MODID = "immersiveposres";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ImmersivePositionalResources() {
        LOGGER.info("Hello from ImmersivePositionalResources!");

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void registerReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new PositionalMineralVeinLoader());
    }
}

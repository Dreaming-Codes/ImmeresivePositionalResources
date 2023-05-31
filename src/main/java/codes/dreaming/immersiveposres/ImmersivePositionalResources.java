package codes.dreaming.immersiveposres;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ImmersivePositionalResources.MODID)
public class ImmersivePositionalResources {
    public static final String MODID = "immersiveposres";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Config CONFIG = new Config();

    public ImmersivePositionalResources() {
        CONFIG.registerConfig();

        LOGGER.info("Hello from ImmersivePositionalResources!");
    }
}

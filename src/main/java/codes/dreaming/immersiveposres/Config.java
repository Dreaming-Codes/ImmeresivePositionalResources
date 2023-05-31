package codes.dreaming.immersiveposres;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    private final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public final ForgeConfigSpec.ConfigValue<String> sampleOption = this.BUILDER.comment("This is a sample option.").define("sampleOption", "sampleDefaultValue");

    public final ForgeConfigSpec COMMON_CONFIG = this.BUILDER.build();

    public void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }
}

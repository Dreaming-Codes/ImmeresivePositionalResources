package codes.dreaming.immersiveposres;

import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.Level;

public class Utils {
    public static double getSpawnDistance(Level world, ColumnPos pos) {
        ColumnPos spawnPos = new ColumnPos(world.getLevelData().getXSpawn(), world.getLevelData().getZSpawn());
        return Math.sqrt(Math.pow(spawnPos.x() - pos.x(), 2) + Math.pow(spawnPos.z() - pos.z(), 2));
    }
}

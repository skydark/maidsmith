package info.skydark.maidsmith;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by skydark on 15-8-28.
 */
public final class Config {
    public static Configuration config;
    public static int waitTick;
    public static int repairRate;
    public static double bookshelfFactor;
    public static double enchantFactor;
    public static double bookshelfMultiplierLimit;
    public static double enchantMultiplierLimit;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        config.load();
        waitTick = loadInt("waitTick", 800, "wait at most 800 ticks between two repairs");
        repairRate = loadInt("repairRate", 10, "reduce 10 damage per repair");
        bookshelfMultiplierLimit = loadDouble("bookshelfMultiplierLimit", 3.0, "");
        if (bookshelfMultiplierLimit < 1.0) {
            bookshelfMultiplierLimit = 3.0;
        }
        bookshelfFactor = loadDouble("bookshelfFactor", 0.9, "");
        if (bookshelfFactor >= 1.0 || bookshelfFactor <= 0.0) {
            bookshelfFactor = 0.9;
        }
        enchantMultiplierLimit = loadDouble("enchantMultiplierLimit", 2.0, "");
        if (enchantMultiplierLimit < 1.0) {
            enchantMultiplierLimit = 2.0;
        }
        enchantFactor = loadDouble("enchantFactor", 0.9, "");
        if (enchantFactor >= 1.0 || enchantFactor <= 0.0) {
            enchantFactor = 0.9;
        }

        config.save();
    }

    public static int loadInt(String name, int _default, String comment) {
        return config.get(Configuration.CATEGORY_GENERAL, name, _default, comment).getInt(_default);
    }

    public static double loadDouble(String name, double _default, String comment) {
        return config.get(Configuration.CATEGORY_GENERAL, name, _default, comment).getDouble(_default);
    }
}

package info.skydark.maidsmith;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = MaidSmith.MODID, version = MaidSmith.VERSION, name="EntityModeSmith", dependencies="required-after:lmmx")
public class MaidSmith
{
    public static final String MODID = "${MOD_ID}";
    public static final String VERSION = "${MOD_VERSION}";

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        Config.init(event.getSuggestedConfigurationFile());
    }
}

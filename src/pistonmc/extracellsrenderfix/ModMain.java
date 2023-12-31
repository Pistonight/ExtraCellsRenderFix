package pistonmc.extracellsrenderfix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pistonmc.extracellsrenderfix.init.Init;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = ModInfo.Id, version = ModInfo.Version, dependencies = "required-after:extracells; required-after:appliedenergistics2")
public class ModMain {
	public static Logger log = LogManager.getLogger("EC2RenderFix");
	@SidedProxy(
		clientSide = ModInfo.Group + ".init.InitClient",
		serverSide = ModInfo.Group + ".init.InitServer"
	)
	public static Init initProxy;

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        initProxy.init();
    }
}

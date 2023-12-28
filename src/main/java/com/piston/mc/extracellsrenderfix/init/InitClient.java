package com.piston.mc.extracellsrenderfix.init;

import com.piston.mc.extracellsrenderfix.ModMain;
import com.piston.mc.extracellsrenderfix.PatchedRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import extracells.registries.BlockEnum;
import extracells.render.RenderHandler;
import extracells.tileentity.TileEntityCertusTank;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class InitClient implements Init {

	@Override
	public void init() {
		PatchedRenderer patch = new PatchedRenderer();
		PatchedRenderer.renderID = RenderHandler.getId();
		
        RenderingRegistry.registerBlockHandler(patch);
        ModMain.log.info("Patched Certus Tank Block Renderer binded");
        
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCertusTank.class, patch);
        ModMain.log.info("Patched Certus Tank TileEntity Renderer binded");
        
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockEnum.CERTUSTANK.getBlock()), patch);
        ModMain.log.info("Patched Certus Tank Item Renderer binded");
	}

}

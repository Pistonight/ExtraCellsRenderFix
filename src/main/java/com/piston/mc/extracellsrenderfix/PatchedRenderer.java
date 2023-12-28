package com.piston.mc.extracellsrenderfix;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import extracells.render.RenderHandler;
import extracells.render.model.ModelCertusTank;
import extracells.tileentity.TileEntityCertusTank;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Patches for rendering Certus Tank in the inventory and overworld
 * 
 * Adds fluid color and improves flickering
 * 
 * Don't need ASM for this part since we can just re-register the renderers
 */
public class PatchedRenderer extends TileEntitySpecialRenderer implements IItemRenderer, ISimpleBlockRenderingHandler {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glColor3f(1, 1, 1);
        RenderBlocks renderer = RenderBlocks.getInstance();
        IBlockAccess world = tileEntity.getWorldObj();
        Block block = tileEntity.getBlockType();

        // Set correct brightness
        int brightness = 0xffffff;
        if (world != null) {
            brightness = block.getMixedBrightnessForBlock(world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        }
        boolean oldAO = renderer.enableAO;
        renderer.enableAO = false;

        if (tileEntity != null && ((TileEntityCertusTank) tileEntity).getTankInfo(ForgeDirection.UNKNOWN)[0].fluid != null) {
            FluidStack fs = ((TileEntityCertusTank) tileEntity).tank.getFluid();
            float scale = ((TileEntityCertusTank) tileEntity).getRenderScale();
            if (fs != null && fs.getFluid() != null && scale > 0) {
                this.renderFluid(fs, scale, brightness, renderer);
            }

        }

        renderer.enableAO = oldAO;
        GL11.glPopMatrix();
    }

    public void renderOuter(int x, int y, int z, Block block, int connectFlag, int brightness, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        renderer.setRenderBounds(0.0625F, 0f, 0.0625F, 0.9375f, 1f, 0.9375f);

        tessellator.setColorOpaque_F(1, 1, 1);
        tessellator.setBrightness(brightness);
        if ((connectFlag & 1) == 0) {
            tessellator.setNormal(0.0F, -1F, 0.0F);
            renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, 0));
        }
        if ((connectFlag & 2) == 0) {
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderer.renderFaceYPos(block, x, y, z, block.getIcon(1, 0));
        }

        IIcon sideIcon = block.getIcon(3, connectFlag);
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderer.renderFaceZNeg(block, x, y, z, sideIcon);
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, x, y, z, sideIcon);
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, x, y, z, sideIcon);
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, x, y, z, sideIcon);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public void renderInner(int x, int y, int z, Block block, int connectFlag, int brightness, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        renderer.setRenderBounds(0.0625F, 0f, 0.0625F, 0.9375f, 1f, 0.9375f);

        tessellator.setBrightness(brightness);
        if ((connectFlag & 1) == 0) {
            tessellator.setNormal(0.0F, 1F, 0.0F);
            renderer.renderFaceYPos(block, x, y - 0.99f, z, block.getIcon(0, 0));
        }
        if ((connectFlag & 2) == 0) {
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            renderer.renderFaceYNeg(block, x, y + 0.99f, z, block.getIcon(1, 0));
        }

        IIcon sideIcon = block.getIcon(3, connectFlag);
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderer.renderFaceZNeg(block, x, y, z + 0.875, sideIcon);
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, x, y, z - 0.875, sideIcon);
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, x + 0.875, y, z, sideIcon);
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, x - 0.875, y, z, sideIcon);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public void renderFluid(FluidStack fluidStack, float scale, int brightness, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        renderer.setRenderBounds(0.065F, 0.001f, 0.065F, 0.935f, scale * 0.999f, 0.935f);

        // Get Color
        Fluid storedFluid = fluidStack.getFluid();
        int fluidColor = storedFluid.getColor(fluidStack);
        byte red = (byte) (fluidColor >> 16);
        byte green = (byte) (fluidColor >> 8);
        byte blue = (byte) fluidColor;

        // Get fluid block
        Block fluidBlock = storedFluid.getBlock();
        if (fluidBlock == null)
            fluidBlock = Block.getBlockById(FluidRegistry.WATER.getID());

        // Get fluid icon
        IIcon fluidIcon = storedFluid.getIcon();
        if (fluidIcon == null)
            fluidIcon = FluidRegistry.LAVA.getIcon();

        tessellator.startDrawingQuads();
        tessellator.setBrightness(brightness);
        tessellator.setColorOpaque(red & 0xff, green & 0xff, blue & 0xff);
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderer.renderFaceYNeg(fluidBlock, 0, 0, 0, fluidIcon);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(fluidBlock, 0, 0, 0, fluidIcon);
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderer.renderFaceZNeg(fluidBlock, 0, 0, 0, fluidIcon);
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(fluidBlock, 0, 0, 0, fluidIcon);
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(fluidBlock, 0, 0, 0, fluidIcon);
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(fluidBlock, 0, 0, 0, fluidIcon);
        tessellator.draw();
        GL11.glPopAttrib();
        GL11.glPopMatrix();

    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    private ModelCertusTank model = new ModelCertusTank();

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("extracells", "textures/blocks/texmap_tank.png"));
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glScalef(1, -1, -1);
        this.model.render(0.0625f);
        GL11.glScalef(1, -1, 1);
        this.model.render(0.0625f);

        if (item != null && item.hasTagCompound()) {
            FluidStack storedFluid = FluidStack.loadFluidStackFromNBT(item.getTagCompound().getCompoundTag("tileEntity"));
            int tankCapacity = 32000;

            if (storedFluid != null && storedFluid.getFluid() != null) {
                // Add color
                int fluidColor = storedFluid.getFluid().getColor(storedFluid);
                byte red = (byte) (fluidColor >> 16);
                byte green = (byte) (fluidColor >> 8);
                byte blue = (byte) fluidColor;
                GL11.glColor3ub(red, green, blue);

                IIcon fluidIcon = storedFluid.getFluid().getIcon();

                Tessellator tessellator = Tessellator.instance;
                RenderBlocks renderer = new RenderBlocks();

                GL11.glScalef(1, 1, -1);
                renderer.setRenderBounds(0.08F, 0.001F, 0.08F, 0.92, (float) storedFluid.amount / (float) tankCapacity * 0.999F, 0.92F);
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                Block waterBlock = FluidRegistry.WATER.getBlock();

                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, -1F, 0.0F);
                renderer.renderFaceYNeg(waterBlock, 0.0D, 0.0D, 0.0D, fluidIcon);
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                renderer.renderFaceYPos(waterBlock, 0.0D, 0.0D, 0.0D, fluidIcon);
                tessellator.setNormal(0.0F, 0.0F, -1F);
                renderer.renderFaceZNeg(waterBlock, 0.0D, 0.0D, 0.0D, fluidIcon);
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                renderer.renderFaceZPos(waterBlock, 0.0D, 0.0D, 0.0D, fluidIcon);
                tessellator.setNormal(-1F, 0.0F, 0.0F);
                renderer.renderFaceXNeg(waterBlock, 0.0D, 0.0D, 0.0D, fluidIcon);
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                renderer.renderFaceXPos(waterBlock, 0.0D, 0.0D, 0.0D, fluidIcon);
                tessellator.draw();
            }
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (RenderHandler.renderPass == 0) {
            // Set correct brightness
            int brightness = 0xffffff;
            if (world != null) {
                brightness = block.getMixedBrightnessForBlock(world, x, y, z);
            }
            boolean oldAO = renderer.enableAO;
            renderer.enableAO = false;

            boolean tankUp = world.getTileEntity(x, y + 1, z) instanceof TileEntityCertusTank;
            boolean tankDown = world.getTileEntity(x, y - 1, z) instanceof TileEntityCertusTank;
            int connectFlag = 0;
            if (tankUp && tankDown)
                connectFlag = 3;
            else if (tankUp)
                connectFlag = 2;
            else if (tankDown)
                connectFlag = 1;

            this.renderOuter(x, y, z, block, connectFlag, brightness, renderer);
            this.renderInner(x, y, z, block, connectFlag, brightness, renderer);

            renderer.enableAO = oldAO;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

    public static int renderID;

}

package pistonmc.extracellsrenderfix.coremod;

import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;

import extracells.gui.widget.fluid.WidgetFluidSelector;
import extracells.gui.widget.fluid.WidgetFluidSlot;
import net.minecraftforge.fluids.FluidStack;

public class Patches {
	public static MethodInsnNode getInsnForSetFluidColorForFluidStack() {
		return new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			Type.getInternalName(Patches.class),
			"setFluidColor",
			"(Lnet/minecraftforge/fluids/FluidStack;)V",
			false);
	}
	public static void setFluidColor(FluidStack fluid) {
		setColor(fluid.getFluid().getColor(fluid));
	}
	
	public static MethodInsnNode getInsnForSetFluidColorForWidgetFluidSelector() {
		return new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			Type.getInternalName(Patches.class),
			"setFluidColor",
			"(Lextracells/gui/widget/fluid/WidgetFluidSelector;)V",
			false);
	}
	public static void setFluidColor(WidgetFluidSelector selector) {
		setColor(selector.getFluid().getColor());
	}
	
	public static MethodInsnNode getInsnForSetFluidColorForWidgetFluidSlot() {
		return new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			Type.getInternalName(Patches.class),
			"setFluidColor",
			"(Lextracells/gui/widget/fluid/WidgetFluidSlot;)V",
			false);
	}
	public static void setFluidColor(WidgetFluidSlot slot) {
		setColor(slot.getFluid().getColor());
	}
	
	private static void setColor(int color) {
		GL11.glColor3ub((byte) (color >> 16), (byte) (color >> 8), (byte) color);
	}
	
	public static MethodInsnNode getInsnForResetColor() {
		return new MethodInsnNode(
			Opcodes.INVOKESTATIC,
			Type.getInternalName(Patches.class),
			"resetColor",
			"()V",
			false);
	}
	public static void resetColor() {
		GL11.glColor3f(1, 1, 1);
	}

}

package pistonmc.extracellsrenderfix.coremod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Patch for WidgetFluidSlot (UI to mark a fluid)
 * 
 * Patches color not shown.
 * 
 * It seems like the author meant to add tooltips to this, but didn't finish. 
 * However, multiple classes need to be patched to support it. This could be potential
 * improvement
 *
 */
public class PatchWidgetFluidSlot implements PatchClass {
	public void apply(ClassNode node) {
		CoremodMain.log.info("Patching WidgetFluidSlot");
		for (MethodNode method: node.methods) {
			if (method.name.equals("drawWidget")) {				
				// Fix fluid color not drawn
				// Find first INVOKEVIRTUAL getIcon
				AbstractInsnNode current = method.instructions.getFirst();
				current = InsnUtils.findNextMethodCall(current, "getIcon");
				if (current == null) {
					CoremodMain.log.error("Cannot find first getIcon, skipping patching");
					continue;
				}
				current = InsnUtils.findNextMethodCall(current.getNext(), "getIcon");
				if (current == null) {
					CoremodMain.log.error("Cannot find second getIcon, skipping patching");
					continue;
				}
				AbstractInsnNode secondGetIcon = current;
				// previous method call is where it sets the wrong color
				current = InsnUtils.findAnyPreviousMethodCall(current.getPrevious());
				if (current == null) {
					CoremodMain.log.error("Cannot find GL11.glColor3f, skipping patching");
					continue;
				}
				// load "this"
				VarInsnNode aload0 = new VarInsnNode(Opcodes.ALOAD, 0);
				MethodInsnNode invokeFix = Patches.getInsnForSetFluidColorForWidgetFluidSlot();
				InsnList list = new InsnList();
				list.add(aload0);
				list.add(invokeFix);
				method.instructions.insert(current, list);
				// we want to reset color after the draw
				current = InsnUtils.findAnyNextMethodCall(secondGetIcon.getNext());
				if (current == null) {
					CoremodMain.log.error("Cannot find drawTexturedModelRectFromIcon, skipping patching");
					continue;
				}
				method.instructions.insert(current, Patches.getInsnForResetColor());

				CoremodMain.log.info("Patched WidgetFluidSlot.drawWidget successfully!");

			}
		}
		
		CoremodMain.log.info("Patched WidgetFluidSlot");
	}

	@Override
	public String getTarget() {
		return "extracells.gui.widget.fluid.WidgetFluidSlot";
	}
}

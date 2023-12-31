package pistonmc.extracellsrenderfix.coremod;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Patch for WidgetFluidTank (UI that holds real amount of fluid)
 * 
 * Fixes:
 * - Tooltips causing NEI items to not light correctly.
 * - Fluid color not shown
 *
 */
public class PatchWidgetFluidTank implements PatchClass {
	public void apply(ClassNode node) {
		CoremodMain.log.info("Patching WidgetFluidTank");
		for (MethodNode method: node.methods) {
			if (method.name.equals("draw")) {
				// Fix fluid stacks don't get colored
				// find INVOKEVIRTUAL Fluid.getStillIcon
				AbstractInsnNode current = method.instructions.getFirst();
				current = InsnUtils.findNextMethodCall(current, "getStillIcon");
				if (current == null) {
					CoremodMain.log.error("Cannot find getStillIcon, skipping patching");
					continue;
				}
				AbstractInsnNode getStillIcon = current;
				// the fluid is a local variable. since it is referenced just before,
				// we can reuse it
				// find the INVOKEVIRTUAL FluidStack.getFluid before it
				current = InsnUtils.findPreviousMethodCall(current.getPrevious(), "getFluid");
				if (current == null) {
					CoremodMain.log.error("Cannot find getFluid, skipping patching");
					continue;
				}
				// find the ALOAD just before
				AbstractInsnNode loadInsn = current.getPrevious();
				if (!(loadInsn instanceof VarInsnNode)) {
					CoremodMain.log.error("Cannot find ALOAD, skipping patching");
					continue;
				}
				
				VarInsnNode aloadInsn = (VarInsnNode) loadInsn;
				// Load the same arg again
				VarInsnNode aloadInsnClone = new VarInsnNode(aloadInsn.getOpcode(), aloadInsn.var);
				MethodInsnNode invokeFix = Patches.getInsnForSetFluidColorForFluidStack();
				// after getStillIcon, there's an ASTORE to set local variable
				current = current.getNext(); // current is ASTORE
				if (current == null) {
					CoremodMain.log.error("Cannot find ASTORE, skipping patching");
					continue;
				}
				// insert set color after that
				InsnList list = new InsnList();
				list.add(aloadInsnClone);
				list.add(invokeFix);
				method.instructions.insert(current, list);
				
				// to find where to reset color, skip to the get capacity call
				current = InsnUtils.findNextMethodCall(getStillIcon, "getCapacity");
				if (current == null) {
					CoremodMain.log.error("Cannot find getCapacity, skipping patching");
					continue;
				}
				// the previous call should be bindTexture
				current = InsnUtils.findAnyPreviousMethodCall(current.getPrevious());
				if (current == null) {
					CoremodMain.log.error("Cannot find bindTexture, skipping patching");
					continue;
				}
				// reset color just after it
				method.instructions.insert(current, Patches.getInsnForResetColor());
				CoremodMain.log.info("Patched WidgetFluidTank.draw succesfully!");
			} else if (method.name.equals("drawHoveringText")) {
				CoremodMain.log.info("Patching WidgetFluidTank.drawHoveringText method");
				// Fix draw hovering text shouldn't need to enable item lighting 
				PatchMethodDrawHoveringText.apply(method);
			}
		}
		CoremodMain.log.info("Patched WidgetFluidTank");
	}

	@Override
	public String getTarget() {
		return "extracells.gui.widget.WidgetFluidTank";
	}
}

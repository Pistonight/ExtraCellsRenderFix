package com.piston.mc.extracellsrenderfix.coremod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Patch for WidgetFluidSelector (UI in the fluid terminal for selecting fluid)
 * 
 * Patches color not shown
 */
public class PatchWidgetFluidSelector implements PatchClass {
	public void apply(ClassNode node) {
		for (MethodNode method: node.methods) {
			if (method.name.equals("drawWidget")) {
				// Fix fluid color not drawn
				// Find INVOKEVIRTUAL getIcon
				AbstractInsnNode current = method.instructions.getFirst();
				current = InsnUtils.findNextMethodCall(current, "getIcon");
				if (current == null) {
					CoremodMain.log.error("Cannot find getIcon, skipping patching");
					continue;
				}
				// we want to set color after the if
				JumpInsnNode ifNode = InsnUtils.findAnyNextJump(current);
				// load "this"
				VarInsnNode aload0 = new VarInsnNode(Opcodes.ALOAD, 0);
				MethodInsnNode invokeFix = Patches.getInsnForSetFluidColorForWidgetFluidSelector();
				InsnList list = new InsnList();
				list.add(aload0);
				list.add(invokeFix);
				method.instructions.insert(current, list);
				// jump to after the if
				current = ifNode.label;
				// reset color after drawing
				method.instructions.insertBefore(current, Patches.getInsnForResetColor());

				CoremodMain.log.info("Patched WidgetFluidSelector.drawWidget successfully!");

			}
		}
	}

	@Override
	public String getTarget() {
		return "extracells.gui.widget.fluid.WidgetFluidSelector";
	}
}

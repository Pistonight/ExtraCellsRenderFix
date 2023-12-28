package com.piston.mc.extracellsrenderfix.coremod;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Fixes the same lighting issue
 */
public class PatchAbstractFluidWidget implements PatchClass {
	public void apply(ClassNode node) {
		for (MethodNode method: node.methods) {
			if (method.name.equals("drawHoveringText")) {
				CoremodMain.log.info("Patching AbstractFluidWidget.drawHoveringText method");
				PatchMethodDrawHoveringText.apply(method);
			}
		}
	}

	@Override
	public String getTarget() {
		return "extracells.gui.widget.fluid.AbstractFluidWidget";
	}
}

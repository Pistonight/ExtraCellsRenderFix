package com.piston.mc.extracellsrenderfix.coremod;

import org.lwjgl.opengl.GL12;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Patch for drawHoveringText in multiple locations that disables standard item lighting
 * and enables them in the end. I don't fully understand, but this messes up NEI item lighting.
 * It works fine without it (with and without NEI)
 */
public class PatchMethodDrawHoveringText {
	public static void apply(MethodNode method) {
		AbstractInsnNode current = method.instructions.getFirst();
		
		// cannot find enableStandardItemLighting directly because it's obfuscated
		// and I am too lazy to find the symbol

		// find disable call
		while (current != null) {
			if (current instanceof LdcInsnNode) {
				if (((LdcInsnNode) current).cst.equals(GL12.GL_RESCALE_NORMAL)) {
					break;
				}
			}
			current = current.getNext();
		}
		if (current == null) {
			CoremodMain.log.error("Cannot find first GL12.GL_RESCALE_NORMAL, skipping patching");
			return;
		}
		// disable should be 2 method calls afterwards (first is call to glDisable)
		current = InsnUtils.findAnyNextMethodCall(current);
		if (current != null) {
			current = InsnUtils.findAnyNextMethodCall(current.getNext());
		}
		if (current == null) {
			CoremodMain.log.error("Cannot find disableStandardItemLighting, skipping patching");
			return;
		}
		AbstractInsnNode toRemove = current;
		current = current.getNext();
		method.instructions.remove(toRemove);
		// find enable call
		while (current != null) {
			if (current instanceof LdcInsnNode) {
				if (((LdcInsnNode) current).cst.equals(GL12.GL_RESCALE_NORMAL)) {
					break;
				}
			}
			current = current.getNext();
		}
		if (current == null) {
			CoremodMain.log.error("Cannot find second GL12.GL_RESCALE_NORMAL, skipping patching");
			return;
		}
		// the previous should be the call to enable item lighting
		current = InsnUtils.findAnyPreviousMethodCall(current);
		if (current == null) {
			CoremodMain.log.error("Cannot find enableStandardItemLighting, skipping patching");
			return;
		}
		method.instructions.remove(current);
		CoremodMain.log.info("Patched drawHoveringText succesfully!");
	}
}

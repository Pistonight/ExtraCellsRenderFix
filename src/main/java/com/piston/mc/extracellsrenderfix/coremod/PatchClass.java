package com.piston.mc.extracellsrenderfix.coremod;

import org.objectweb.asm.tree.ClassNode;

public interface PatchClass {
	/**
	 * Transforms the class
	 * 
	 * By default, reads it into a ClassNode, then call apply
	 * @param classContent
	 * @return
	 */
	default byte[] transform(byte[] classContent) {
		CoremodMain.log.info("Patching " + getTarget());
		ClassNode node = Transformer.deserializeClass(classContent);
		apply(node);
		CoremodMain.log.info("Patched " + getTarget());
		return Transformer.serializeClass(node);
	}
	/**
	 * Apply the patch to the class
	 * @param node
	 */
	void apply(ClassNode node);
	
	/**
	 * Get the target (transformed) class name for this patch
	 * @return
	 */
	String getTarget();
}

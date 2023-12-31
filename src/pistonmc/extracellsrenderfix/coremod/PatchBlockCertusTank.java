package pistonmc.extracellsrenderfix.coremod;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Patches the textures to not have semi-transparency, to avoid
 * rendering flickers
 */
public class PatchBlockCertusTank implements PatchClass {
	public void apply(ClassNode node) {
		CoremodMain.log.info("Patching BlockCertusTank");
		for (MethodNode method: node.methods) {
			if (method.name.equals("registerBlockIcons")) {
				// replace the string constants
				AbstractInsnNode current = method.instructions.getFirst();
				while (current != null) {
					if(current instanceof LdcInsnNode) {
						String cst = ((LdcInsnNode) current).cst.toString();
						if (cst.equals("extracells:CTankTop")
							|| cst.equals("extracells:CTankBottom")
							|| cst.equals("extracells:CTankSide")
							|| cst.equals("extracells:CTankSideMiddle")
							|| cst.equals("extracells:CTankSideTop")
							|| cst.equals("extracells:CTankSideBottom")) {
							CoremodMain.log.info("Replacing texture " + cst);
							((LdcInsnNode) current).cst = cst.replace("extracells", CoremodInfo.Id);
						}
					}
					current = current.getNext();
				}

				CoremodMain.log.info("Patched BlockCertusTank.registerBlockIcons successfully!");
			}
		}
		CoremodMain.log.info("Patched BlockCertusTank");
	}

	@Override
	public String getTarget() {
		return "extracells.block.BlockCertusTank";
	}
}

package pistonmc.extracellsrenderfix.coremod;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer {
	
	private Map<String, PatchClass> patches;
	
	public Transformer() {
		patches = new HashMap<>();
		addPatch(new PatchAbstractFluidWidget());
		addPatch(new PatchWidgetFluidSelector());
		addPatch(new PatchWidgetFluidSlot());
		addPatch(new PatchWidgetFluidTank());
		addPatch(new PatchBlockCertusTank());
		
	}
	
	private void addPatch(PatchClass p) {
		String target = p.getTarget();
		patches.put(target, p);
		CoremodMain.log.info("Registered patch for "+ target);
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] original) {
		PatchClass patch = patches.get(transformedName);
		if (patch == null) {
			return original;
		}
		
		return patch.transform(original);
	}
	
	public static ClassNode deserializeClass(byte[] content) {
		CoremodMain.log.info("old length="+content.length);
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(content);
		reader.accept(node, 0);
		
		return node;
	}
	
	public static byte[] serializeClass(ClassNode node) {
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		node.accept(writer);
		byte[] newContent = writer.toByteArray();
		CoremodMain.log.info("new length="+newContent.length);
		
		return newContent;
	}

}

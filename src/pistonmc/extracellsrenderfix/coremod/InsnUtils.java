package pistonmc.extracellsrenderfix.coremod;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

/**
 * Utilities for working with bytecode
 */
public class InsnUtils {
	public static MethodInsnNode findPreviousMethodCall(AbstractInsnNode current, String methodName) {
		while (current != null) {
			if (current instanceof MethodInsnNode) {
				MethodInsnNode methodInsn = (MethodInsnNode) current;
				if (methodInsn.name.equals(methodName)) {
					return (MethodInsnNode) current;	
				}
			}
			current = current.getPrevious();
		}
		return null;
	}
	
	public static MethodInsnNode findNextMethodCall(AbstractInsnNode current, String methodName) {
		while (current != null) {
			if (current instanceof MethodInsnNode) {
				MethodInsnNode methodInsn = (MethodInsnNode) current;
				if (methodInsn.name.equals(methodName)) {
					return (MethodInsnNode) current;	
				}
			}
			current = current.getNext();
		}
		return null;
	}
	
	public static MethodInsnNode findAnyPreviousMethodCall(AbstractInsnNode current) {
		while (current != null) {
			if (current instanceof MethodInsnNode) {
				return (MethodInsnNode) current;
			}
			current = current.getPrevious();
		}
		return null;
	}
	
	public static MethodInsnNode findAnyNextMethodCall(AbstractInsnNode current) {
		while (current != null) {
			if (current instanceof MethodInsnNode) {
				return (MethodInsnNode) current;
			}
			current = current.getNext();
		}
		return null;
	}
	
	public static JumpInsnNode findAnyPreviousJump(AbstractInsnNode current) {
		while (current != null) {
			if (current instanceof JumpInsnNode) {
				return (JumpInsnNode) current;
			}
			current = current.getPrevious();
		}
		return null;
	}
	
	public static JumpInsnNode findAnyNextJump(AbstractInsnNode current) {
		while (current != null) {
			if (current instanceof JumpInsnNode) {
				return (JumpInsnNode) current;
			}
			current = current.getNext();
		}
		return null;
	}
	
	/**
	 * Print the bytecode of a method to console, for inspecting
	 * @param method
	 */
	public static void dumpMethod(MethodNode method) {
		Printer printer = new Textifier();
		TraceMethodVisitor methodPrinter = new TraceMethodVisitor(printer);
		for (AbstractInsnNode n: method.instructions.toArray()) {
			n.accept(methodPrinter);
			StringWriter sw = new StringWriter();
			printer.print(new PrintWriter(sw));
			printer.getText().clear();
			String insnNodeAsString = sw.toString().replaceAll("\n", "");
			CoremodMain.log.info(insnNodeAsString);
		}
	}
}

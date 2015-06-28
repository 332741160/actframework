package act.util;

import act.asm.ClassVisitor;
import act.asm.ClassWriter;
import act.asm.Opcodes;
import org.osgl._;
import org.osgl.util.E;

import java.util.Iterator;
import java.util.List;

/**
 * Base class for all bytecode visitor, either detector or enhancer
 */
public class ByteCodeVisitor extends ClassVisitor implements Opcodes {

    private _.Var<? extends ClassVisitor> _cv;

    protected ByteCodeVisitor(ClassVisitor cv) {
        super(ASM5, cv);
    }

    protected ByteCodeVisitor() {
        super(ASM5);
    }

    public ByteCodeVisitor commitDownstream() {
        E.illegalStateIf(null == _cv || null != cv);
        cv = _cv.get();
        return this;
    }

    private ByteCodeVisitor setDownstream(_.Var<? extends ClassVisitor> cv) {
        E.illegalStateIf(null != this.cv);
        _cv = cv;
        return this;
    }

    private ByteCodeVisitor setDownstream(ClassVisitor cv) {
        E.illegalStateIf(null != this.cv);
        this.cv = cv;
        return this;
    }

    public static ByteCodeVisitor chain(_.Var<ClassWriter> cw, List<? extends ByteCodeVisitor> visitors) {
        if (visitors.isEmpty()) {
            return null;
        }
        Iterator<? extends ByteCodeVisitor> i = visitors.iterator();
        ByteCodeVisitor v = i.next();
        v.setDownstream(cw);
        while (i.hasNext()) {
            ByteCodeVisitor v0 = i.next();
            v0.setDownstream(v);
            v = v0;
        }
        return v;
    }

    public static ByteCodeVisitor chain(List<? extends ByteCodeVisitor> visitors) {
        if (visitors.isEmpty()) {
            return null;
        }
        Iterator<? extends ByteCodeVisitor> i = visitors.iterator();
        ByteCodeVisitor v = i.next();
        while (i.hasNext()) {
            ByteCodeVisitor v0 = i.next();
            v0.setDownstream(v);
            v = v0;
        }
        return v;
    }

    public static ClassVisitor chain(ClassWriter cw, ByteCodeVisitor v0, ByteCodeVisitor... visitors) {
        v0.setDownstream(cw);
        int len = visitors.length;
        if (0 == len) {
            return v0;
        }
        for (int i = 0; i < len - 1; ++i) {
            ByteCodeVisitor v = visitors[i];
            v.setDownstream(v0);
            v0 = v;
        }
        return v0;
    }

    public static boolean isConstructor(String methodName) {
        return methodName.contains("<init>");
    }
    public static boolean isPublic(int access) {
        return (ACC_PUBLIC & access) > 0;
    }
    public static boolean isPrivate(int access) {
        return (ACC_PRIVATE & access) > 0;
    }
    public static boolean isAbstract(int access) {
        return (ACC_ABSTRACT & access) > 0;
    }

}

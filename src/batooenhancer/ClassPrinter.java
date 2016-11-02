/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package batooenhancer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 *
 * @author napoles
 */
public class ClassPrinter implements ClassVisitor{

    public String classNane;
    
    @Override
    public void visit(int version, int access, String name,String signature, String superName, String[] interfc) {
        this.classNane = name.replaceAll("/",".") ;
    }

    @Override
    public void visitSource(String string, String string1) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visitOuterClass(String string, String string1, String string2) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AnnotationVisitor visitAnnotation(String string, boolean bln) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    @Override
    public void visitAttribute(Attribute atrbt) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visitInnerClass(String string, String string1, String string2, int i) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FieldVisitor visitField(int i, String string, String string1, String string2, Object o) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int i, String string, String string1, String string2, String[] strings) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    @Override
    public void visitEnd() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}

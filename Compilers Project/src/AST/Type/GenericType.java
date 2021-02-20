package AST.Type;

import AST.ASTNode;
import Visitor.VisitorInterface;
import sun.net.www.content.text.Generic;

public class GenericType extends ASTNode {

    public String Ctype(GenericType t) {
        String tipo = "";

        if(t.getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
            tipo = "int";
        }

        if(t.getClass().getSimpleName().equals(FloatType.class.getSimpleName())) {
            tipo = "float";
        }


        if(t.getClass().getSimpleName().equals(VoidType.class.getSimpleName())) {
            tipo = "void";
        }

        return tipo;
    }

    public String toCtype() {
        String tipo = "";

        if(this.getClass().getSimpleName().equals(IntegerType.class.getSimpleName())) {
            tipo = "int";
        }

        if(this.getClass().getSimpleName().equals(FloatType.class.getSimpleName())) {
            tipo = "float";
        }

        if(this.getClass().getSimpleName().equals(VoidType.class.getSimpleName())) {
            tipo = "void";
        }

        if(this.getClass().getSimpleName().equals(StringType.class.getSimpleName())) {
            tipo = "char";
        }

        if(this.getClass().getSimpleName().equals(BoolType.class.getSimpleName())) {
            tipo = "int";
        }

        return tipo;
    }
    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }
}

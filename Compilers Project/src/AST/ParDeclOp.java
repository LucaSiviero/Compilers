package AST;

import AST.Expr.IdNode;
import AST.Type.GenericType;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class ParDeclOp extends ASTNode{
    GenericType t;
    LinkedList<IdNode> ids;

    public ParDeclOp(GenericType t, LinkedList<IdNode> ids) {
        this.t = t;
        this.ids = ids;
    }

    public GenericType getType() {
        return t;
    }

    public LinkedList<IdNode> getIds() {
        return ids;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }
}

package AST;

import AST.Type.GenericType;
import Visitor.VisitorInterface;

public class VarDeclOp extends ASTNode{
    IdListInitOp ids;
    GenericType t;

    public VarDeclOp(GenericType t, IdListInitOp ids){
        this.t = t;
        this.ids = ids;
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }

    public IdListInitOp getIds() {
        return ids;
    }

    public GenericType getType() {
        return t;
    }
}

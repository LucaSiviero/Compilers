package AST.Expr;

import AST.Type.GenericType;
import Visitor.VisitorInterface;

public class IdNode extends ExprNode {
    String id;
    GenericType type;

    public IdNode(Object i) {
        this.id = i.toString();
    }

    public String getId() {
        return id.toString();
    }

    @Override
    public void setType(GenericType type) {
        this.type = type;
    }

    @Override
    public GenericType getType(){
        return this.type;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}

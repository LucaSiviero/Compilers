package AST.Const;

import Visitor.VisitorInterface;

public class NullConst extends ConstNode {
    public NullConst() {
        super("NULL");
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}

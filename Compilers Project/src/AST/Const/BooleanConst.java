package AST.Const;


import Visitor.VisitorInterface;

public class BooleanConst extends ConstNode {
    public BooleanConst(Object argument) {
        super(argument);
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }

}

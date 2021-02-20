package AST.Const;

import Visitor.VisitorInterface;

public class IntegerConst extends ConstNode {
    private int value;

    public IntegerConst(Object intero) {
        super(intero);
        this.value = Integer.parseInt(intero.toString());
    }

    public int getValue (){
        return this.value;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}

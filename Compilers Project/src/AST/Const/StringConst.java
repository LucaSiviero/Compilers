package AST.Const;


import Visitor.VisitorInterface;

public class StringConst extends ConstNode {
    public StringConst(Object argument) {
        super("\"" + argument.toString() + "\"");
    }




    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}

package AST.Const;


import Visitor.VisitorInterface;

public class FloatConst extends ConstNode {
    private float value;

    public FloatConst(Object argument) {
        super(argument);
        this.value = Float.parseFloat(argument.toString());
    }


    public float getValue(){
        return this.value;
    }
    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }

}


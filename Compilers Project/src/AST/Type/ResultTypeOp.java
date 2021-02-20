package AST.Type;

import Visitor.VisitorInterface;

public class ResultTypeOp extends GenericType{
    GenericType t;

    public ResultTypeOp (GenericType type){
        this.t = type;
    }

    public ResultTypeOp(String s) {
        this.t = new VoidType();
    }

    public GenericType getType() {
        return t;
    }

}

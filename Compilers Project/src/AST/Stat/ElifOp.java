package AST.Stat;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

public class ElifOp extends StatNode{
    ExprNode e;
    BodyOp body;

    public ElifOp(ExprNode e, BodyOp body){
        this.e = e;
        this.body = body;
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
    public ExprNode getExpr() {
        return e;
    }

    public BodyOp getBody() {
        return body;
    }
}

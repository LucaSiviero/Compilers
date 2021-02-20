package AST.Stat;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

public class WhileOp extends StatNode {
    BodyOp body;
    ExprNode e;
    BodyOp body2;

    public WhileOp(BodyOp body, ExprNode e, BodyOp body2){
        this.body = body;
        this.e = e;
        this.body2 = body2;
    }

    public WhileOp(ExprNode e, BodyOp body2){
        this.e = e;
        this.body2 = body2;
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
    public BodyOp getBody() {
        return body;
    }

    public ExprNode getExpr() {
        return e;
    }

    public BodyOp getBody2() {
        return body2;
    }
}

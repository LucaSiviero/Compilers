package AST.UnaryExpr;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

public class NotOp extends UnaryExprNode {

    public NotOp(ExprNode e) {
       super(e);
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }
}

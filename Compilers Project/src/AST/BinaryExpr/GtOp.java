package AST.BinaryExpr;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

public class GtOp extends BinaryExprNode {
    public GtOp(ExprNode e1, ExprNode e2) {
        super(e1, e2);
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }
}

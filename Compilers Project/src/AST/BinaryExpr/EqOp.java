package AST.BinaryExpr;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

public class EqOp extends BinaryExprNode {

    public EqOp(ExprNode e1, ExprNode e2) {
        super(e1,e2);
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }
}
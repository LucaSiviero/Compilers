package AST.BinaryExpr;

import AST.ArithmeticExprs.ArithmeticExpr;
import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

public class AddOp extends ArithmeticExpr {
    public AddOp(ExprNode e1, ExprNode e2) {
        super(e1, e2);
    }

    @Override
    public ExprNode getLeft() {
        return super.getLeft();
    }

    @Override
    public ExprNode getRight() {
        return super.getRight();
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}

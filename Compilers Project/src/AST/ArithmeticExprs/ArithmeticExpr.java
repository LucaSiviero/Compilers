package AST.ArithmeticExprs;

import AST.BinaryExpr.BinaryExprNode;
import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

public class ArithmeticExpr extends BinaryExprNode {
    public ArithmeticExpr(ExprNode e1, ExprNode e2){
        super(e1, e2);
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}

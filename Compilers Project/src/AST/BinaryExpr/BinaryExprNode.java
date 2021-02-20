package AST.BinaryExpr;

import AST.Expr.ExprNode;

public abstract class BinaryExprNode extends ExprNode {
    ExprNode left,right;

    public BinaryExprNode(ExprNode e1, ExprNode e2) {
        this.left = e1;
        this.right = e2;
    }

    public ExprNode getLeft() {
        return this.left;
    }

    public ExprNode getRight() {
        return this.right;

    }

}

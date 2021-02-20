package AST.UnaryExpr;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

public class UnaryExprNode extends ExprNode {
    ExprNode link;

    public ExprNode getLink() {
        return link;
    }

    public void setLink(ExprNode link) {
        this.link = link;
    }

    public UnaryExprNode(ExprNode link) {
        this.link = link;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }
}

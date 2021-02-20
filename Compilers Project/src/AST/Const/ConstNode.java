package AST.Const;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

public class ConstNode extends ExprNode {
    String lexeme;

    public ConstNode( Object argument) {
        this.lexeme = argument.toString();
    }

    public String getArgument() {
        return lexeme;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}

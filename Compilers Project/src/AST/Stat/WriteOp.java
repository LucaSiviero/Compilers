package AST.Stat;

import AST.Expr.ExprNode;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class WriteOp extends StatNode {
    LinkedList<AST.Expr.ExprNode> es = new LinkedList<AST.Expr.ExprNode>();

    public WriteOp(LinkedList<AST.Expr.ExprNode> es) {
        this.es=es;
    }


    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
    public LinkedList<ExprNode> getExprs() {
        return es;
    }
}

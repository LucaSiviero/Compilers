package AST.Functions;

import AST.Expr.ExprNode;
import AST.Expr.IdNode;
import AST.Stat.StatNode;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class CallProcOp extends StatNode{
    IdNode i;
    LinkedList<AST.Expr.ExprNode> es = new LinkedList<AST.Expr.ExprNode>();

    public CallProcOp(IdNode i){
        this.i = i;
    }

    public CallProcOp(IdNode i, LinkedList<AST.Expr.ExprNode> es) {
        this.i = i;
        this.es = es;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }

    public LinkedList<ExprNode> getExprs() {
        return es;
    }

    public IdNode getId() {
        return i;
    }
}

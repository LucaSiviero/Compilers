package AST;

import AST.Expr.ExprNode;
import AST.Expr.IdNode;
import AST.Stat.StatNode;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class AssignOp extends StatNode {
    LinkedList<ExprNode> es = new LinkedList<ExprNode>();
    LinkedList<IdNode> is = new LinkedList<IdNode>();
    ExprNode e = null;
    IdNode i = null;

    public AssignOp(LinkedList<IdNode> is, LinkedList<ExprNode> es){
        this.es = es;
        this.is = is;
    }

    public AssignOp(IdNode i, ExprNode e){
        this.i = i;
        this.e = e;
    }

    public AssignOp(IdNode i){
        this.i = i;
    }

    public AssignOp(){
    }

    public LinkedList<ExprNode> getExprs() {
        return es;
    }

    public LinkedList<IdNode> getIds() {
        return is;
    }

    public ExprNode getExpr() {
        return e;
    }

    public IdNode getId() {
        return i;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return (v.Visit(this));
    }
}

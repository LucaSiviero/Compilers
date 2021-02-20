package AST.Expr;

import AST.Functions.ProcOp;
import Visitor.VisitorInterface;

import java.util.LinkedList;

public class ExprCallProc extends ExprNode{
    IdNode i;
    LinkedList<ExprNode> es = new LinkedList<AST.Expr.ExprNode>();
    ProcOp referredTo;

    public ExprCallProc(IdNode i){
        this.i = i;
    }

    public ExprCallProc(IdNode i, LinkedList<AST.Expr.ExprNode> es) {
        this.i = i;
        this.es = es;
    }


    public void setProc(ProcOp p){
        this.referredTo = p;
    }

    public ProcOp getProc(){
        return this.referredTo;
    }

    public LinkedList<ExprNode> getExprs() {
        return es;
    }

    public IdNode getId() {
        return i;
    }

    @Override
    public <T> T accept(VisitorInterface<T> v) {
        return v.Visit(this);
    }
}
